package edu.up.cs301.checkers;


import java.util.ArrayList;

import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.CheckerPlayers.CheckerHumanPlayer1;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.Pawn.Pawn;
import edu.up.cs301.checkers.InfoMessage.Pieces;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

public class CheckerLocalGame extends LocalGame {

    //Tag for logging
    private static final String TAG = "CheckerLocalGame";
    // piece that was selected by row and column
    private int tempRow;
    private int tempCol;

    // all of the initial movements of the piece selected
    private ArrayList<Integer> initialMovementsX = new ArrayList<>();
    private ArrayList<Integer> initialMovementsY = new ArrayList<>();


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
     * Constructor for the CheckerLocalGame with loaded checkerState
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
     * @return true iff the player is allowed to move
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

            // save temps for the row and col for movement later
            tempRow = row;
            tempCol = col;

            // the selected piece
            Pieces p = state.getPiece(row, col);

            // find all movements of piece selected
            findMovement(state, p);


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

            Pieces tempP = state.getPiece(tempRow, tempCol);

            // determine what team is moving (white/black) and move the piece
            if (tempP.getColors() == Pieces.Colors.RED) {
                if (!setMovement(state, row, col, Pieces.Colors.RED)) {
                    return false;
                }
            } else if (tempP.getColors() == Pieces.Colors.BLACK) {
                if (!setMovement(state, row, col, Pieces.Colors.BLACK)) {
                    return false;
                }
            }
            // make it the other player's turn
            state.setWhoseMove(1 - whoseMove);

            // return true, indicating the it was a legal move
            return true;
        } else if (action instanceof CheckerPromotionAction) {
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
    public void findMovement(CheckerState state, Pieces p) {
        // make sure the arraylists are empty before they are filled
        initialMovementsX.clear();
        initialMovementsY.clear();

        // search through each type of piece and generate all of the movements
        // of that piece and add them to the initialMovement arraylists.
        if (p.getType() == 0) {
            Pawn pawn = new Pawn(p, state, p.getColors());
            for (int i = 0; i < pawn.getX().size(); i++) {
                initialMovementsX.add(pawn.getX().get(i));
                initialMovementsY.add(pawn.getY().get(i));
            }
        }
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
        public boolean setMovement (CheckerState state,int row, int col, Pieces.Colors color){
            // if they selected a dot/ring then move
            if (state.getDrawing(row, col) == 2 || state.getDrawing(row, col) == 4) {

                //adds captured piece to captured pieces array t
                if (state.getPiece(row, col).getColors() != Pieces.Colors.EMPTY) {
                    if (state.getPiece(row, col).getColors() == Pieces.Colors.BLACK) {
                        state.addRedCapturedPiece(state.getPiece(row, col));
                    } else {
                        state.addBlackCapturedPiece(state.getPiece(row, col));
                    }
                }


                Pieces tempPiece = state.getPiece(tempRow, tempCol);


                // set the new position to be the piece they originally selected
                boolean isCapture = state.getPiece(row, col).getColors() != Pieces.Colors.EMPTY;
                CheckerHumanPlayer1 chp = players[0] instanceof CheckerHumanPlayer1 ?
                        (CheckerHumanPlayer1) players[0] : (CheckerHumanPlayer1) players[1];
                if (CheckerPromotionAction.isPromotion) {
                    state.setPiece(promo.getRow(), promo.getCol(), promo.getPromotionPiece());
                    CheckerPromotionAction.isPromotion = false;
                } else {
                    state.setPiece(row, col, state.getPiece(tempRow, tempCol));
                }
                // change the piece at the selection to be an empty piece
                state.setPiece(tempRow, tempCol, state.emptyPiece);
                // reset temp values so only selections may occur
                tempRow = -1;
                tempCol = -1;
                /**
                if (color == Pieces.Colors.BLACK) {
                    if (checkForCheck(state, Pieces.Colors.RED, color)) {
                        state.setHighlightCheck(state.getKingWhite().getX(), state.getKingWhite().getY());
                        winCondition = checkForCheckmate(state);
                        checkIfGameOver();
                    } else {
                        winCondition = checkForStalemate(state);
                        checkIfGameOver();
                    }
                } else if (color == Pieces.Colors.RED) {
                    if (checkForCheck(state, Pieces.Colors.BLACK, color)) {
                        state.setHighlightCheck(state.getKingBlack().getX(), state.getKingBlack().getY());
                        winCondition = checkForCheckmate(state);
                        checkIfGameOver();
                    } else {
                        winCondition = checkForStalemate(state);
                        checkIfGameOver();
                    }
                }
                **/
                return true;
            } else {
                // if they didn't select a dot they don't move
                return false;
            }
        }

        public String checkForStalemate (CheckerState state){
            Pieces.Colors color = null;
            // find what color has moved to put the other player in checkmate
            if (state.getWhoseMove() == 0) {
                // if it is now whites turn that means to look for if black is in stalemate
                color = Pieces.Colors.BLACK;
            } else {
                // if it is now blacks turn that means to look for if white is in stalemate
                color = Pieces.Colors.RED;
            }

            // arraylist that adds all enemy pieces
            ArrayList<Pieces> pieces = new ArrayList<>();
            // add all pieces to arraylist
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (state.getPiece(i, j).getColors() == color) {
                        pieces.add(state.getPiece(i, j));
                    }
                }
            }

            // create fake selections and check if there are any possible
            // movement for that selection. If there is a movement then
            // the player is not in stalemate
            for (int i = 0; i < pieces.size(); i++) {
                tempRow = pieces.get(i).getX();
                tempCol = pieces.get(i).getY();
                findMovement(state, pieces.get(i));
            }
            //there are no possible moves
            state.setGameOver(true);
            return "S";
        }

        public int whoWon () {
            String gameOver = checkIfGameOver();
            if (gameOver == null || gameOver.equals("It's a cat's game.")) return -1;
            if (gameOver.equals(playerNames[0] + " is the winner.")) return 0;
            return 1;
        }

        public boolean checkPromotion (Pieces piece,int col, CheckerHumanPlayer1 chp){
            if (piece.getType() != 0) { return false; }
            if (piece.getColors() == Pieces.Colors.RED && col == 0) {
                //return new Piece(Piece.PieceType.QUEEN, Piece.ColorType.WHITE, piece.getX(), 0);
                return true;
            }else if(piece.getColors() == Pieces.Colors.BLACK && col == 7){
                return true;
            }
            return false;
        }
}
