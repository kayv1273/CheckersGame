package edu.up.cs301.checkers;

import java.util.ArrayList;

import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.CheckerPlayers.CheckerHumanPlayer1;
import edu.up.cs301.checkers.Views.Pieces;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.tictactoe.tttActionMessage.TTTMoveAction;
public class CheckerLocalGame extends LocalGame {
    //Tag for logging
    private static final String TAG = "CheckerLocalGame";

    // piece that was selected by row and column
    private int tempRow;
    private int tempCol;

    // all of the initial movements of the piece selected
    private ArrayList<Integer> initialMovementsX = new ArrayList<>();
    private ArrayList<Integer> initialMovementsY = new ArrayList<>();

    // all of the valid movements so the king isn't in check
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

        // create a new, unfilled-in TTTState object
        super.state = new CheckerState();
        isPromotion = false;
    }

    /**
     * Constructor for the TTTLocalGame with loaded tttState
     * @param checkerState
     */
    public CheckerLocalGame(CheckerState checkerState){
        super();
        super.state = new CheckerState(checkerState);
    }

    /**
     *  This is where you should initialize anything specific to the
     *  number of players.  For example you may need to init your
     *  game state or part of it.  Loading data could also happen here.
     *
     * 	 @param players
     */
    @Override
    public void start(GamePlayer[] players)
    {
        super.start(players);
    }

    /**
     * Check if the game is over. It is over, return a string that tells
     * who the winner(s), if any, are. If the game is not over, return null;
     *
     * @return
     * 		a message that tells who has won the game, or null if the
     * 		game is not over
     */
    @Override
    protected String checkIfGameOver() {

        // the idea is that we simultaneously look at a row, column and
        // a diagonal, using the variables 'rowToken', 'colToken' and
        // 'diagToken'; we do this three times so that we get all three
        // rows, all three columns, and both diagonals.  (The way the
        // math works out, one of the diagonal tests tests the middle
        // column.)  The respective variables get set to ' ' unless
        // all characters in the line that have currently been seen are
        // identical; in this case the variable contains that character

        // the character that will eventually contain an 'X' or 'O' if we
        // find a winner
        if (winCondition == null) {
            return null;
        } else if (winCondition.equals("B")) {
            return "Black Wins! ";
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
     * @param p
     * 			the player to notify
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
     * @param playerIdx
     * 		the player's player-number (ID)
     * @return
     * 		true iff the player is allowed to move
     */
    protected boolean canMove(int playerIdx) {
        return playerIdx == ((CheckerState)state).getWhoseMove();
    }

    /**
     * Makes a move on behalf of a player.
     *
     * @param action
     * 			The move that the player has sent to the game
     * @return
     * 			Tells whether the move was a legal one.
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

            // remove the red highlight from being in check
            state.removeHighlightCheck();

            // highlight the piece they tapped
            state.setHighlight(row, col);

            // save temps for the row and col for movement later
            tempRow = row;
            tempCol = col;

            // the selected piece
            Pieces p = state.getPiece(row, col);

            // find all movements of piece selected
            findMovement(state, p);

            // make fake movements and determine if that movement allows the
            // players own king be in check
            moveToNotBeInCheck(state, p.getPieceColor(), p.getPieceType());

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

            //very specific castling case- the selected piece and piece being moved to are the same color
            if(state.getPiece(tempRow, tempCol).getPieceColor() == state.getPiece(row, col).getPieceColor()){
                if(tempRow == 4 && tempCol == 7 && row == 7 && col == 7){

                }
            }

            //updates potential hasMoved variables
            if(state.getPiece(tempRow, tempCol).getPieceColor() == Pieces.Colors.RED){
                if(state.getPiece(tempRow, tempCol).getPieceType() == Piece.PieceType.KING) {
                    state.setWhiteKingHasMoved(true);
                }
                else if(state.getPiece(tempRow, tempCol).getPieceType() == Piece.PieceType.ROOK && tempCol == 7){
                    state.setWhiteRook1HasMoved(true);
                }
                else if(state.getPiece(tempRow, tempCol).getPieceType() == Piece.PieceType.ROOK && tempCol == 0){
                    state.setWhiteRook2HasMoved(true);
                }
            }
            else if(state.getPiece(tempRow, tempCol).getPieceColor() == Piece.ColorType.BLACK){
                if(state.getPiece(tempRow, tempCol).getPieceType() == Piece.PieceType.KING) {
                    state.setBlackKingHasMoved(true);
                }
                else if(state.getPiece(tempRow, tempCol).getPieceType() == Piece.PieceType.ROOK && tempCol == 7){
                    state.setBlackRook1HasMoved(true);
                }
                else if(state.getPiece(tempRow, tempCol).getPieceType() == Piece.PieceType.ROOK && tempCol == 0){
                    state.setBlackRook2HasMoved(true);
                }
            }

            Pieces tempP = state.getPiece(tempRow, tempCol);

            // determine what team is moving (white/black) and move the piece
            if (tempP.getPieceColor() == Piece.ColorType.WHITE) {
                if (!setMovement(state, row, col, Piece.ColorType.WHITE)) {
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

    //TESTING

    public int whoWon(){
        String gameOver = checkIfGameOver();
        if(gameOver == null || gameOver.equals("Stalemate.")) return -1;
        if(gameOver.equals(playerNames[0]+" is the winner.")) return 0;
        return 1;
    }
    public boolean checkPromotion(Pieces piece, int col, CheckerHumanPlayer1 chp){
        if(piece.getType() != 0) {return false;}
        if(piece.getColors() == Pieces.Colors.RED && col == 0){
            //return new Piece(Piece.PieceType.QUEEN, Piece.ColorType.WHITE, piece.getX(), 0);
            return true;
        } else if(piece.getColors() == Pieces.Colors.BLACK && col == 7) {
            return true;
        }
        return false;
    }
}
