package edu.up.cs301.checkers;


import java.util.ArrayList;
import java.util.List;

import edu.up.cs301.checkers.AllPieces.King;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
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
 * @version 4.26.2023
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
    private ArrayList<Integer> XcaptCoords = new ArrayList<Integer>();
    private ArrayList<Integer> YcaptCoords = new ArrayList<Integer>();

    private String winCondition = null;
    private boolean isPromotion;
    private CheckerPromotionAction promo;
    private boolean hasCaptured = false;

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
     * @param players all the players
     */

    @Override
    public void start(GamePlayer[] players) {
        super.start(players);
    }

    /**
     * Check if the game is over
     * @return message of win
     */
    @Override
    protected String checkIfGameOver() {
        if (winCondition == null) {
            return null;
        } else if (winCondition.equals("B")) {
            return "Black wins! ";
        } else if (winCondition.equals("R")) {
            return "Red Wins! ";
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

            if(newMovementsX.size() > 0) state.setCanMove(true);
            else state.setCanMove(false);

            // display all positions in arraylist as dots on the board
            state.setCircles(newMovementsX, newMovementsY);

            // return true to skip changing turns
            return true;

        }
        else if (action instanceof CheckerMoveAction) {
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
            // check if the move resulted in a capture
            if (hasCaptured) {
                // more jumps are available for the same player
                moreJumps();
            } else {
                // switch turns to the other player
                state.setWhoseMove(1 - whoseMove);
            }

            // reset hasCaptured boolean variable
            hasCaptured = false;

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

    private void moreJumps() {
        CheckerState state = (CheckerState) super.state;
        Piece p = state.getPiece(tempRow, tempCol);

        // find all movements of the selected piece
        findMovement(state, p);

        // check if there are more jumps available
        boolean moreJumps = false;
        for (int i = 0; i < XcaptCoords.size(); i++) {
            if (p.getPieceType() == Piece.PieceType.PAWN && p.getPieceColor() == Piece.ColorType.RED) {
                if (Math.abs(tempRow - XcaptCoords.get(i)) == -2 &&
                        (Math.abs(tempCol - YcaptCoords.get(i)) == -2 || Math.abs(tempCol - YcaptCoords.get(i)) == 2)) {
                    moreJumps = true;
                    break;
                }
            } else if (p.getPieceType() == Piece.PieceType.PAWN && p.getPieceColor() == Piece.ColorType.BLACK) {
                if ((Math.abs(tempRow - XcaptCoords.get(i)) == 2 || Math.abs(tempRow - XcaptCoords.get(i)) == -2)
                        && Math.abs(tempCol - YcaptCoords.get(i)) == 2) {
                    moreJumps = true;
                    break;
                }
            } else if (p.getPieceType() == Piece.PieceType.KING) {
                if ((Math.abs(tempRow - XcaptCoords.get(i)) == -2 || Math.abs(tempRow - XcaptCoords.get(i)) == 2) &&
                        (Math.abs(tempCol - YcaptCoords.get(i)) == -2 || Math.abs(tempCol - YcaptCoords.get(i)) == 2)) {
                    moreJumps = true;
                    break;
                }
            }
        }

        // if there are more jumps available for the same player
        if (moreJumps) {
            // highlight the piece they tapped
            state.setHighlight(tempRow, tempCol);

            // display all positions in arraylist as dots on the board
            state.setCircles(XcaptCoords, YcaptCoords);
        }
        state.setPiece(tempRow, tempCol, p);
    }

    /**
     * Finds all of the positions the Piece p can move to with normal movements
     *
     * @param state the current state of the game
     * @param p     the piece that is currently selected
     */
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
            // Get all possible captures for king
            for (int j = 0; j < king.getXAttack().size(); j++) {
                initialMovementsX.add(king.getXAttack().get(j));
                initialMovementsY.add(king.getYAttack().get(j));
                XcaptCoords.add(king.getXAttack().get(j));
                YcaptCoords.add(king.getXAttack().get(j));
            }
        }
        // Change Red Pawn into a king
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // If the piece is on the back row, promote it to a king
                if (j == 0 && state.getPiece(i, j).getPieceColor() == Piece.ColorType.RED) {
                    state.setPiece(i, j, new Piece(Piece.PieceType.KING, Piece.ColorType.RED, i, j));
                }
            }
        }
    }

    /**
     * Determines if the current players piece is in danger
     * @return
     */
    public boolean checkForDanger() {
        return false;
    }

    /**
     * Looks through all movements of the piece selected and determines
     * if that movement causes the players piece to be in danger
     *
     * @param state     the current state of the game
     * @param color     the color of the player that is making a move
     * @param pieceType
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
                if (!checkForDanger()) {

                    // if the player is not in check add that movement to the new
                    // arraylist so it can be saved
                    newMovementsX.add(initialMovementsX.get(i));
                    newMovementsY.add(initialMovementsY.get(i));
                }
            } else if (color == Piece.ColorType.BLACK) {
                if (!checkForDanger()) {
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
     * @return tells whether the move was valid and happened
     */
    public boolean setMovement(CheckerState state, int row, int col, Piece.ColorType color) {
        // If they selected a dot/ring then move
        if (state.getDrawing(row, col) == 2 || state.getDrawing(row, col) == 4) {
            // Adds captured piece to captured pieces array
            if (state.getPiece(row, col).getPieceType() != Piece.PieceType.EMPTY) {
                if(state.getPiece(row, col).getPieceColor() == Piece.ColorType.BLACK) {
                    state.addRedCapturedPiece(state.getPiece(row, col));
                } else {
                    state.addBlackCapturedPiece(state.getPiece(row, col));
                }
            }
            // Check current piece if there is a capture available
            boolean take = false;
            for (int i = 0; i < state.getNewMovementsX().size(); i++) {
                if ((tempRow - state.getNewMovementsX().get(i) == 2 || tempRow - state.getNewMovementsX().get(i) == -2) &&
                        (tempCol - state.getNewMovementsY().get(i) == 2 || tempCol - state.getNewMovementsY().get(i) == -2)) {
                    take = true;
                    break;
                }
            }
            // For simultaneously capturing and promoting
            if (take == true && CheckerPromotionAction.isPromotion) {
                // First capture the piece
                state.setPiece(row, col, state.getPiece(tempRow, tempCol));
                capturePiece(state, row, col);
                // Then promote to king
                state.setPiece(promo.getRow(),promo.getCol(),promo.getPromotionPiece());
                CheckerPromotionAction.isPromotion = false;
                // Regular promotion
            } else if (CheckerPromotionAction.isPromotion) {
                state.setPiece(promo.getRow(),promo.getCol(),promo.getPromotionPiece());
                CheckerPromotionAction.isPromotion = false;
                // Capture a piece
            } else {
                state.setPiece(row, col, state.getPiece(tempRow, tempCol));
                capturePiece(state, row, col);
            }
            // change the piece at the selection to be an empty piece
            state.setPiece(tempRow, tempCol, state.emptyPiece);
            // remove all highlights
            state.removeHighlight();
            // reset temp values so only selections may occur
            tempRow = -1;
            tempCol = -1;
            // remove all the circles after moving
            state.removeCircle();

            winCondition = checkForWin(state);
            if (winCondition != null) checkIfGameOver();
            if (color == Piece.ColorType.BLACK) {
                if (checkForDanger()) {
                    checkIfGameOver();
                } else {
                    checkIfGameOver();
                }
            } else if (color == Piece.ColorType.RED) {
                if (checkForDanger()) {
                    checkIfGameOver();
                } else {
                    checkIfGameOver();
                }
            }
            return true;
        } else {
            // if they didn't select a dot they don't move
            return false;
        }
    }

    /**
     * Check for a win
     *
     * @param state the CheckerState
     * @return B for black win, R for red win
     */
    public String checkForWin(CheckerState state) {
        ArrayList<Piece> redPieces = new ArrayList<>();
        ArrayList<Piece> blackPieces = new ArrayList<>();
        // add all pieces to arraylist
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPiece(i, j).getPieceColor() == Piece.ColorType.BLACK) {
                    blackPieces.add(state.getPiece(i, j));
                } else if (state.getPiece(i, j).getPieceColor() == Piece.ColorType.RED) {
                    redPieces.add(state.getPiece(i, j));
                }
            }
        }

        //check if any red pieces left
        if (redPieces.size() == 0) {
            state.setGameOver(true);
            return "B";

        //check if any black pieces left
        } else if (blackPieces.size() == 0) {
            state.setGameOver(true);
            return "R";
        } else {
            return null;
        }
    }

    /**
     * Check if a piece can capture and update the CheckerState accordingly
     *
     * @param state the CheckerState
     * @param row the row of the square after capture
     * @param col the row of the square after capture
     */
    public void capturePiece(CheckerState state, int row, int col) {

        // Get distance between selected piece and new place
        int xdistance = (tempRow - row);
        int ydistance = (tempCol - col);

        // Capturing top right diagonal
        if (xdistance == -2 && ydistance == 2) {
            Piece piece = state.getPiece((tempRow + 1), (tempCol - 1));
            piece.setPieceType(Piece.PieceType.EMPTY);
            piece.setColorType(Piece.ColorType.EMPTY);
        }
        // Capturing top left diagonal
        if (xdistance == 2 && ydistance == 2) {
            Piece piece = state.getPiece((tempRow - 1), (tempCol - 1));
            piece.setPieceType(Piece.PieceType.EMPTY);
            piece.setColorType(Piece.ColorType.EMPTY);
        }
        // Capturing bottom right diagonal
        if (xdistance == -2 && ydistance == -2) {
            Piece piece = state.getPiece((tempRow + 1), (tempCol + 1));
            piece.setPieceType(Piece.PieceType.EMPTY);
            piece.setColorType(Piece.ColorType.EMPTY);
        }
        // Capturing bottom left diagonal
        if (xdistance == 2 && ydistance == -2) {
            Piece piece = state.getPiece((tempRow - 1), (tempCol + 1));
            piece.setPieceType(Piece.PieceType.EMPTY);
            piece.setColorType(Piece.ColorType.EMPTY);
        }
    }

    /* unit testing
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
    }*/
}
