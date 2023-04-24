package edu.up.cs301.checkers;


import java.util.ArrayList;

import edu.up.cs301.checkers.AllPieces.King;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.CheckerPlayers.CheckerHumanPlayer;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.AllPieces.Pawn;
import edu.up.cs301.checkers.InfoMessage.Piece;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

/**
 * @author Griselda
 * @author Katherine
 * @author Ruth
 * @author Nick
 * @author Ethan
 * @version 4.13.2023
 */

public class CheckerLocalGame extends LocalGame {

    //Tag for logging
    private static final String TAG = "CheckerLocalGame";
    // piece that was selected by row and column
    private int tempRow;
    private int tempCol;

    // all of the initial movements of the piece selected
    private ArrayList<Integer> initialMovementsX = new ArrayList<>();
    private ArrayList<Integer> initialMovementsY = new ArrayList<>();

    private ArrayList<Integer> newMovementsX = new ArrayList<>();
    private ArrayList<Integer> newMovementsY = new ArrayList<>();

    private String winCondition = null;
    private boolean isPromotion;
    private CheckerPromotionAction promo;

    /**
     * Constructor for the CheckerLocalGame.
     */
    public CheckerLocalGame() {

        // perform superclass initialization
        super();

        // create a new, standard CheckerState object
        super.state = new CheckerState();
        isPromotion = false;
    }


    /**
     * Constructor for the CheckerLocalGame with loaded chessState
     *
     * @param checkerState
     */
    public CheckerLocalGame(CheckerState checkerState) {
        super();
        super.state = new CheckerState(checkerState);
    }

    /**
     * This is where you should initialize anything specific to the
     * number of players.  For example you may need to init your
     * game state or part of it.  Loading data could also happen here.
     *
     * @param players
     */

    @Override
    public void start(GamePlayer[] players) {
        super.start(players);
    }

    @Override
    protected String checkIfGameOver() {

        //char resultChar = ' ';
        if (winCondition == null) {
            return null;
        } else if (winCondition.equals("B")) {
            return "Black wins! ";
        } else if (winCondition.equals("R")) {
            return "Red Wins! ";
        } else if (winCondition.equals("S")) {
            return "Stalemate no one wins! ";
        }
        return null;
    }

    /**
     * Notify the given player that its state has changed. This should involve sending
     * a GameInfo object to the player. If the game is not a perfect-information game
     * this method should remove any information from the game that the player is not
     * allowed to know.
     *
     * @param p the player to notify
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        // make a copy of the state, and send it to the player
        p.sendInfo(new CheckerState(((CheckerState) state)));
    }

    /**
     * Tell whether the given player is allowed to make a move at the
     * present point in the game.
     *
     * @param playerIdx the player's player-number (ID)
     * @return true if the player is allowed to move
     */
    @Override
    protected boolean canMove(int playerIdx) {
        return playerIdx == ((CheckerState) state).getWhoseMove();
    }

    /**
     * Makes a move on behalf of a player.
     *
     * @param action The move that the player has sent to the game
     * @return Tells whether the move was a legal one.
     */
    @Override
    protected boolean makeMove(GameAction action) {
        CheckerState state = (CheckerState) super.state;

        // get the 0/1 id of the player whose move it is
        int whoseMove = state.getWhoseMove();
        if (action instanceof CheckerSelectAction) {
            CheckerSelectAction select = (CheckerSelectAction) action;
            int row = select.getRow();
            int col = select.getCol();

            // remove the highlights if there are any previous ones
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (state.getDrawing(i, j) == 1) {
                        state.removeHighlight();
                        state.removeCircle();
                    }
                }
            }

            // highlight the piece they tapped
            state.setHighlight(row, col);

            // save temps for the row and col for movement later
            tempRow = row;
            tempCol = col;

            // the selected piece
            Piece p = state.getPiece(row, col);

            // find all movements of piece selected
            findMovement(state, p);

            // make fake movements
            moveToNotBeInDanger(state, p.getPieceColor(), p.getPieceType());

            if(newMovementsX.size() > 0) {
                state.setCanMove(true);
            } else {
                state.setCanMove(false);
            }

            // display all positions in arraylist as dots on the board
            state.setCircles(newMovementsX, newMovementsY);

            // return true to skip changing turns
            return true;
        } else if (action instanceof CheckerMoveAction) {
            CheckerMoveAction move = (CheckerMoveAction) action;
            int row = move.getRow();
            int col = move.getCol();

            // if they have no selected piece movement shouldn't occur
            if (tempRow == -1 || tempCol == -1) {
                return false;
            }



            Piece tempP = state.getPiece(tempRow, tempCol);

            // determine what team is moving (red/black) and move the piece
            if (tempP.getPieceColor() == Piece.ColorType.RED) {
                if (!setMovement(state, row, col, Piece.ColorType.RED)) {

                    state.removeHighlight();
                    state.removeCircle();
                    return false;
                }
            } else if (tempP.getPieceColor() == Piece.ColorType.BLACK) {
                if (!setMovement(state, row, col, Piece.ColorType.BLACK)) {
                    state.removeHighlight();
                    state.removeCircle();
                    return false;
                }
            }
            // make sure all highlights and dots are already removed
            state.removeCircle();


            // make it the other player's turn
            state.setWhoseMove(1 - whoseMove);

            // return true, indicating the it was a legal move
            return true;
        } else if (action instanceof CheckerPromotionAction){
            promo = (CheckerPromotionAction) action;
            CheckerPromotionAction.isPromotion = true;
            return true;
        }
        // return true, indicating the it was a legal move
        return false;
    }

    /**
     * Finds all of the positions the Piece p can move to with normal movements
     *
     * @param state the current state of the game
     * @param p     the piece that is currently selected
     */

    ArrayList<Integer> XcaptCoords = new ArrayList<Integer>();
    ArrayList<Integer> YcaptCoords = new ArrayList<Integer>();

    public void findMovement(CheckerState state, Piece p) {
        // make sure the arraylists are empty before they are filled
        initialMovementsX.clear();
        initialMovementsY.clear();

        // search through each type of piece and generate all of the movements
        // of that piece and add them to the initialMovement arraylists.
        if (p.getPieceType() == Piece.PieceType.PAWN) {
            Pawn pawn = new Pawn(p, state, p.getPieceColor());
            // Get all possible movements for pawn
            for (int i = 0; i < pawn.getX().size(); i++) {
                initialMovementsX.add(pawn.getX().get(i));
                initialMovementsY.add(pawn.getY().get(i));
            }
            // Get all possible captures for pawn
            for (int j = 0; j < pawn.getXAttack().size(); j++) {
                initialMovementsX.add(pawn.getXAttack().get(j));
                initialMovementsY.add(pawn.getYAttack().get(j));
                XcaptCoords.add(pawn.getXAttack().get(j));
                YcaptCoords.add(pawn.getXAttack().get(j));
            }

        } else if (p.getPieceType() == Piece.PieceType.KING) {
            King king = new King(p, state, p.getPieceColor());
            // Get all possible movements for king
            for (int i = 0; i < king.getX().size(); i++) {
                initialMovementsX.add(king.getX().get(i));
                initialMovementsY.add(king.getY().get(i));
            }
            // Get al possible captures for king
            for (int j = 0; j < king.getXAttack().size(); j++) {
                initialMovementsX.add(king.getXAttack().get(j));
                initialMovementsY.add(king.getYAttack().get(j));
                XcaptCoords.add(king.getXAttack().get(j));
                YcaptCoords.add(king.getXAttack().get(j));
            }
        }
    }

    /**
     * Determines if the current players piece is in danger
     *
     * @param state      the copied state displaying a movement
     * @param teamColor  the color the player that is making a movement
     * @param enemyColor the color of the other player
     * @return Determines if a king is in check
     */
    public boolean checkForDanger(CheckerState state, Piece.ColorType teamColor, Piece.ColorType enemyColor) {
        
        return false;
    }

    /**
     * Looks through all movements of the piece selected and determines
     * if that movement causes the players piece to be in danger
     *
     * @param state the current state of the game
     * @param color the color of the player that is making a move
     * @param pieceType the PieceType of the selected piece
     */
    public void moveToNotBeInDanger(CheckerState state, Piece.ColorType color, Piece.PieceType pieceType) {
        // make sure the arraylists are empty
        newMovementsX.clear();
        newMovementsY.clear();

        // iterate through all of the initial movements of the selected piece
        for (int i = 0; i < initialMovementsX.size(); i++) {

            // create a copied state so the current state is not affected yet
            CheckerState copyState = new CheckerState(state);

            // make one of the initial movements on the copied state
            makeTempMovement(copyState, initialMovementsX.get(i), initialMovementsY.get(i));

            // determine if the player is red or black so that can be passed
            // in as a parameter
            if (color == Piece.ColorType.RED) {

                // determine if the movement causes the players piece to be in danger
                if (!checkForDanger(copyState, color, Piece.ColorType.BLACK)) {

                    // if the player is not in check add that movement to the new
                    // arraylist so it can be saved
                    newMovementsX.add(initialMovementsX.get(i));
                    newMovementsY.add(initialMovementsY.get(i));
                }
            } else if (color == Piece.ColorType.BLACK) {
                if (!checkForDanger(copyState, color, Piece.ColorType.RED)) {
                    newMovementsX.add(initialMovementsX.get(i));
                    newMovementsY.add(initialMovementsY.get(i));
                }
            }
            state.setNewMovementsX(newMovementsX);
            state.setNewMovementsY(newMovementsY);
        }

    }



    /**
     * Creates a fake movement on the copied state
     *
     * @param state the copied state of the game
     * @param row   the row position of the selected piece
     * @param col   the column position of the selected piece
     */
    public void makeTempMovement(CheckerState state, int row, int col) {
        // create the temp piece with the selected position (tempRow, tempCol)
        Piece tempPiece = state.getPiece(tempRow, tempCol);


        // make the location the piece is moving to become the selected piece
        state.setPiece(row, col, tempPiece);

        // make the selected piece become empty since the piece has moved
        state.setPiece(tempRow, tempCol, state.emptyPiece);
    }

    /**
     * Move the piece that was selected to the new position
     * that the player wants to move to
     *
     * @param state the current state of the game
     * @param row   the row of the position the player is moving to
     * @param col   the column of the position the player is moving to
     * @param color the color of the piece they selected previously
     * @return tells weather the move was valid and happened
     */
    public boolean setMovement(CheckerState state, int row, int col, Piece.ColorType color) {
        // if they selected a dot/ring then move
        if (state.getDrawing(row, col) == 2 || state.getDrawing(row, col) == 4) {

            //adds captured piece to captured pieces array
            if (state.getPiece(row, col).getPieceType() != Piece.PieceType.EMPTY) {
                if(state.getPiece(row, col).getPieceColor() == Piece.ColorType.BLACK) {
                    state.addRedCapturedPiece(state.getPiece(row, col));
                } else {
                    state.addBlackCapturedPiece(state.getPiece(row, col));
                }
            }
            
            Piece tempPiece = state.getPiece(tempRow, tempCol);
            
            // set the new position to be the piece they originally selected
            boolean isCapture = state.getPiece(row,col).getPieceType() != Piece.PieceType.EMPTY;
            CheckerHumanPlayer chp = players[0] instanceof CheckerHumanPlayer ?
                    (CheckerHumanPlayer) players[0] : (CheckerHumanPlayer) players[1];
            if (CheckerPromotionAction.isPromotion) {
                state.setPiece(promo.getRow(),promo.getCol(),promo.getPromotionPiece());
                CheckerPromotionAction.isPromotion = false;
            } else {
                state.setPiece(row, col, state.getPiece(tempRow, tempCol));

                // Get distance between selected piece and new place
                int xdistance = (tempRow - row);
                int ydistance = (tempCol - col);
                // Red capturing right diagonal
                if (xdistance == -2 && ydistance == 2) {
                    Piece piece = state.getPiece((tempRow + 1), (tempCol - 1));
                    piece.setPieceType(Piece.PieceType.EMPTY);
                    piece.setColorType(Piece.ColorType.EMPTY);
                }
                // Red capturing left diagonal
                if (xdistance == 2 && ydistance == 2) {
                    Piece piece = state.getPiece((tempRow - 1), (tempCol - 1));
                    piece.setPieceType(Piece.PieceType.EMPTY);
                    piece.setColorType(Piece.ColorType.EMPTY);
                }
                // Black capturing right diagonal
                if (xdistance == -2 && ydistance == -2) {
                    Piece piece = state.getPiece((tempRow + 1), (tempCol + 1));
                    piece.setPieceType(Piece.PieceType.EMPTY);
                    piece.setColorType(Piece.ColorType.EMPTY);
                }
                // Black capturing left diagonal
                if (xdistance == 2 && ydistance == -2) {
                    Piece piece = state.getPiece((tempRow - 1), (tempCol + 1));
                    piece.setPieceType(Piece.PieceType.EMPTY);
                    piece.setColorType(Piece.ColorType.EMPTY);
                }

                /**
                 if (XcaptCoords.contains(row + 2) && YcaptCoords.contains(col - 2)) {
                 Piece piece = state.getPiece((row + 1), (col - 1));
                 piece.setPieceType(Piece.PieceType.EMPTY);
                 piece.setColorType(Piece.ColorType.EMPTY);
                 }
                 **/


            }
            //chp.displayMovesLog(row,col,tempRow,state,isCapture);
            // change the piece at the selection to be an empty piece
            state.setPiece(tempRow, tempCol, state.emptyPiece);

            // remove all highlights
            state.removeHighlight();

            // reset temp values so only selections may occur
            tempRow = -1;
            tempCol = -1;

            // remove all the circles after moving
            state.removeCircle();


            if (color == Piece.ColorType.BLACK) {
                if (checkForDanger(state, Piece.ColorType.RED, color)) {
                    checkIfGameOver();
                } else {
                    winCondition = checkForStalemate(state);
                    checkIfGameOver();
                }
            } else if (color == Piece.ColorType.RED) {
                if (checkForDanger(state, Piece.ColorType.BLACK, color)) {
                    checkIfGameOver();
                } else {
                    winCondition = checkForStalemate(state);
                    checkIfGameOver();
                }
            }
            return true;
        } else {
            // if they didn't select a dot they don't move
            return false;
        }
    }

    public String checkForStalemate(CheckerState state) {
        Piece.ColorType color = null;

        if(state.getWhoseMove() == 0) {
            // if it is now red turn that means to look for if black is in stalemate
            color = Piece.ColorType.BLACK;
        } else {
            // if it is now blacks turn that means to look for if red is in stalemate
            color = Piece.ColorType.RED;
        }

        // arraylist that adds all enemy pieces
        ArrayList<Piece> pieces = new ArrayList<>();
        // add all pieces to arraylist
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(state.getPiece(i, j).getPieceColor() == color) {
                    pieces.add(state.getPiece(i,j));
                }
            }
        }

        // create fake selections and check if there are any possible
        // movement for that selection. If there is a movement then
        // the player is not in stalemate
        for(int i = 0; i < pieces.size(); i++) {
            tempRow = pieces.get(i).getX();
            tempCol = pieces.get(i).getY();
            findMovement(state, pieces.get(i));
            moveToNotBeInDanger(state, color, state.getPiece(tempRow, tempCol).getPieceType());
            if(newMovementsX.size() > 0) {
                //if we are in here then they have at least one move
                //so it's not stalemate
                return null;
            }
        }
        //there are no possible moves
        state.setGameOver(true);
        return "S";
    }



    // unit testing
    public int whoWon(){
        String gameOver = checkIfGameOver();
        if(gameOver == null || gameOver.equals("It's a cat's game.")) return -1;
        if(gameOver.equals(playerNames[0]+" is the winner.")) return 0;
        return 1;
    }

    public boolean checkPromotion(Piece piece, int col,CheckerHumanPlayer chp){
        if (piece.getPieceType() != Piece.PieceType.PAWN) return false;
        if (piece.getPieceColor() == Piece.ColorType.RED && col == 0) return true;
        else if(piece.getPieceColor() == Piece.ColorType.BLACK && col == 7) return true;
        return false;
    }
}
