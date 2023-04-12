package edu.up.cs301.checkers;


import java.util.ArrayList;

import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.CheckerPlayers.CheckerHumanPlayer1;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.Views.Pieces;
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

        // CHESS
        // determine if a player is put into check, if the player is in
        // check then go through every piece that player has on the board
        // and generate all of its possible locations. If the arraylist
        // for its locations comes back empty then continue. As soon as
        // one arraylist comes back with at least one position then we
        // can return that the game is not over. If all pieces have been
        // searched through then we know the game is over due to a checkmate.
        // -----
        // more updates can be made once stalemate is implemented

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
            if(state.getPiece(tempRow, tempCol).getPieceColor() == Piece.Colors.RED){
                if(state.getPiece(tempRow, tempCol).getPieceType() == Piece.PieceType.KING) {
                    state.setWhiteKingHasMoved(true);
                }
            }
            else if(state.getPiece(tempRow, tempCol).getPieceColor() == Piece.Colors.BLACK){
                if(state.getPiece(tempRow, tempCol).getPieceType() == Piece.PieceType.KING) {
                    state.setBlackKingHasMoved(true);
                }
            }

            Piece tempP = state.getPiece(tempRow, tempCol);

            // determine what team is moving (white/black) and move the piece
            if (tempP.getPieceColor() == Pieces.Colors.RED) {
                if (!setMovement(state, row, col, Pieces.Colors.RED)) {
                    return false;
                }
            } else if (tempP.getPieceColor() == Pieces.Colors.BLACK) {
                if (!setMovement(state, row, col, Pieces.Colors.BLACK)) {
                    return false;
                }
            }
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
    public void findMovement(CheckerState state, Pieces p) {
        // make sure the arraylists are empty before they are filled
        initialMovementsX.clear();
        initialMovementsY.clear();

        // search through each type of piece and generate all of the movements
        // of that piece and add them to the initialMovement arraylists.
        if (p.getPieceType() == Pieces.PieceType.PAWN) {
            Pawn pawn = new Pawn(p, state, p.getPieceColor());
            for (int i = 0; i < pawn.getX().size(); i++) {
                initialMovementsX.add(pawn.getX().get(i));
                initialMovementsY.add(pawn.getY().get(i));
            }
        }
    }

    /**
     * Determines if the current players king is in check with a certain
     * piece movement
     *
     * @param state      the copied state displaying a movement
     * @param teamColor  the color the player that is making a movement
     * @param enemyColor the color of the other player
     * @return Determines if a king is in check
     */
    public boolean checkForCheck(CheckerState state, Piece.Colors teamColor, Piece.Colors enemyColor) {
        // search through every piece of the enemy and generate its general movement
        // with its position on the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = state.getPiece(row, col);
                if (piece.getPieceColor() == enemyColor) {
                    if (piece.getPieceType() == Piece.PieceType.PAWN) {
                        Pawn pawn = new Pawn(piece, state, enemyColor);
                        state.setCircles(pawn.getXAttack(), pawn.getYAttack());
                    }
                }
            }
        }

        // determine what king will be in check with the movements generated
        Pieces king = null;
        if (teamColor == Pieces.Colors.RED) {
            king = state.getKingWhite();
        } else if (teamColor == Pieces.Colors.BLACK) {
            king = state.getKingBlack();
        }
        if (king != null) {
            // if the king can be attacked by the enemy it means the king is in check
            if (state.getDrawing(king.getX(), king.getY()) == 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looks through all movements of the piece selected and determines
     * if that movement causes the players king to be in check
     *
     * @param state the current state of the game
     * @param color the color of the player that is making a move
     * @param pieceType the PieceType of the selected piece
     */
    public void moveToNotBeInCheck(CheckerState state, Pieces.Colors color, Pieces.PieceType pieceType) {
        // make sure the arraylists are empty
        newMovementsX.clear();
        newMovementsY.clear();

        // iterate through all of the initial movements of the selected piece
        for (int i = 0; i < initialMovementsX.size(); i++) {

            // create a copied state so the current state is not affected yet
            CheckerState copyState = new CheckerState(state);

            // make one of the initial movements on the copied state
            makeTempMovement(copyState, initialMovementsX.get(i), initialMovementsY.get(i));

            // determine if the player is white or black so that can be passed
            // in as a parameter
            if (color == Pieces.Colors.RED) {

                // determine if the movement causes the players king to be in check
                if (!checkForCheck(copyState, color, Pieces.Colors.BLACK)) {

                    // if the player is not in check add that movement to the new
                    // arraylist so it can be saved
                    newMovementsX.add(initialMovementsX.get(i));
                    newMovementsY.add(initialMovementsY.get(i));
                }
            } else if (color == Pieces.Colors.BLACK) {
                if (!checkForCheck(copyState, color, Pieces.Colors.RED)) {
                    newMovementsX.add(initialMovementsX.get(i));
                    newMovementsY.add(initialMovementsY.get(i));
                }
            }
            state.setNewMovementsX(newMovementsX);
            state.setNewMovementsY(newMovementsY);
        }
        if(pieceType == Pieces.PieceType.KING) {
            suitableCastle(state);
        }
    }

    /**
     * Makes sure a castle is legal by checking the other movement options of the king
     *
     * @param state the current state of the game
     */
    public void suitableCastle(CheckerState state){
        //loops for getting rid of crossing-check moves for castling
        boolean castle57Exists =false;
        boolean castle67Exists =false;
        boolean castle27Exists =false;
        boolean castle37Exists =false;
        boolean castle20Exists =false;
        boolean castle30Exists =false;
        boolean castle50Exists =false;
        boolean castle60Exists =false;
        int index1 = -1;
        int index2 = -1;
        //for white
        if(state.getPiece(4, 7).getPieceType() == Pieces.PieceType.KING &&
                state.getPiece(4, 7).getPieceColor() == Pieces.Colors.RED && state.getWhoseMove() == 0) {
            for (int i = 0; i < newMovementsX.size(); i++) {
                int x = newMovementsX.get(i);
                int y = newMovementsY.get(i);
                if (x == 5 && y == 7) {
                    castle57Exists = true;
                }
                if(x == 6 && y == 7){
                    castle67Exists = true;
                    index1 = i;
                }
            }
            if(!castle57Exists && castle67Exists){
                newMovementsX.remove(index1);
                newMovementsY.remove(index1);
            }
            for (int i = 0; i < newMovementsX.size(); i++) {
                int x = newMovementsX.get(i);
                int y = newMovementsY.get(i);
                if (x == 2 && y == 7) {
                    castle27Exists = true;
                    index2 = i;
                }
                if(x == 3 && y == 7){
                    castle37Exists = true;
                }
            }
            if(!castle37Exists && castle27Exists){
                newMovementsX.remove(index2);
                newMovementsY.remove(index2);
            }
        }
        //for black
        else if(state.getPiece(4, 0).getType() == 0 &&
                state.getPiece(4, 0).getColors() == Pieces.Colors.BLACK && state.getWhoseMove() == 1) {
            for (int i = 0; i < newMovementsX.size(); i++) {
                int x = newMovementsX.get(i);
                int y = newMovementsY.get(i);
                if (x == 5 && y == 0) {
                    castle50Exists = true;
                }
                if(x == 6 && y == 0){
                    castle60Exists = true;
                    index1 = i;
                }
            }
            if(!castle50Exists && castle60Exists){
                newMovementsX.remove(index1);
                newMovementsY.remove(index1);
            }
            for (int i = 0; i < newMovementsX.size(); i++) {
                int x = newMovementsX.get(i);
                int y = newMovementsY.get(i);
                if (x == 2 && y == 0) {
                    castle20Exists = true;
                    index2 = i;
                }
                if(x == 3 && y == 0){
                    castle30Exists = true;
                }
            }
            if(!castle30Exists && castle20Exists){
                newMovementsX.remove(index2);
                newMovementsY.remove(index2);
            }
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
        Pieces tempPiece = state.getPiece(tempRow, tempCol);

        // if they are moving a king determine who's king (white/black) and
        // update the position of the king so when it checks for if the king
        // is in check it knows the new position
        if (tempPiece.getType() == 0) {
            if (tempPiece.getColors() == Pieces.Colors.RED) {
                state.setKingWhite(row, col);
            } else if (tempPiece.getPieceColor() == Piece.Colors.BLACK) {
                state.setKingBlack(row, col);
            }
        }
        // make the location the piece is moving to become the selected piece
        state.setPiece(row, col, tempPiece);


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
    public boolean setMovement(CheckerState state, int row, int col, Pieces.ColorType color) {
        // if they selected a dot/ring then move
        if (state.getDrawing(row, col) == 2 || state.getDrawing(row, col) == 4) {

            //adds captured piece to captured pieces array t
            if (state.getPiece(row, col).getColors() != Pieces.Colors.EMPTY) {
                if(state.getPiece(row, col).getColors() == Pieces.ColorType.BLACK) {
                    state.addRedCapturedPiece(state.getPiece(row, col));
                } else {
                    state.addBlackCapturedPiece(state.getPiece(row, col));
                }
            }

//            for (Piece p : state.getWhiteCapturedPieces()) {
//                Log.d("Testing", p.getPieceType().toString());
//            }

            Pieces tempPiece = state.getPiece(tempRow, tempCol);






            // set the new position to be the piece they originally selected
            boolean isCapture = state.getPiece(row,col).getColors() != Pieces.Colors.EMPTY;
            CheckerHumanPlayer1 chp = players[0] instanceof CheckerHumanPlayer1 ?
                    (CheckerHumanPlayer1) players[0] : (CheckerHumanPlayer1) players[1];
            if(CheckerPromotionAction.isPromotion){
                state.setPiece(promo.getRow(),promo.getCol(),promo.getPromotionPiece());
                CheckerPromotionAction.isPromotion = false;
            }else {
                state.setPiece(row, col, state.getPiece(tempRow, tempCol));
            }
            chp.displayMovesLog(row,col,tempRow,state,isCapture);
            // change the piece at the selection to be an empty piece
            state.setPiece(tempRow, tempCol, state.emptyPiece);

            // remove all highlights
            state.removeHighlight();

            // reset temp values so only selections may occur
            tempRow = -1;
            tempCol = -1;

            // remove all the circles after moving
            state.removeCircle();
            state.setKingInCheck(false);

            if (color == Piece.Colors.BLACK) {
                if (checkForCheck(state, Piece.Colors.RED, color)) {
                    state.setHighlightCheck(state.getKingWhite().getX(), state.getKingWhite().getY());
                    state.setKingInCheck(true);
                    winCondition = checkForCheckmate(state);
                    checkIfGameOver();
                } else {
                    winCondition = checkForStalemate(state);
                    checkIfGameOver();
                }
            } else if (color == Piece.Colors.RED) {
                if (checkForCheck(state, Piece.Colors.BLACK, color)) {
                    state.setHighlightCheck(state.getKingBlack().getX(), state.getKingBlack().getY());
                    state.setKingInCheck(true);
                    winCondition = checkForCheckmate(state);
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
        Piece.Colors color = null;
        // find what color has moved to put the other player in checkmate
        if(state.getWhoseMove() == 0) {
            // if it is now whites turn that means to look for if black is in stalemate
            color = Piece.Colors.BLACK;
        } else {
            // if it is now blacks turn that means to look for if white is in stalemate
            color = Piece.Colors.RED;
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
            moveToNotBeInCheck(state, color, state.getPiece(tempRow, tempCol).getPieceType());
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

    public String checkForCheckmate(CheckerState state) {
        // if a player is not in check then then there is no checkmate yet

        Piece.Colors color = null;
        // find what color has moved to put the other player in checkmate
        if(state.getWhoseMove() == 0) {
            // if it is now whites turn that means black put white in checkmate
            color = Piece.Colors.BLACK;
        } else if (state.getWhoseMove() == 1) {
            // if it is now blacks turn that means white put black in checkmate
            color = Piece.Colors.RED;
        }

        // arraylist that holds all pieces of player that is in check
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
        // the player can get out of check and it is not a checkmate.
        // If all pieces have no possible movements then the player
        // is in checkmate.
        for(int i = 0; i < pieces.size(); i++) {
            tempRow = pieces.get(i).getX();
            tempCol = pieces.get(i).getY();
            findMovement(state, pieces.get(i));
            moveToNotBeInCheck(state, color, state.getPiece(tempRow, tempCol).getPieceType());
            if(newMovementsX.size() > 0) {
                return null; // no winner yet
            }
        }
        // since a return was never made that means the player has
        // no possible movements and is in checkmate, so the player
        // who put the other player in check is now the winner.
        if (color == Piece.Colors.RED) {
            state.setGameOver(true);
            return "B";
        } else if (color == Piece.Colors.BLACK) {
            state.setGameOver(true);
            return "W";
        }

        return null;
    }

    // unit testing
    public int whoWon(){
        String gameOver = checkIfGameOver();
        if(gameOver == null || gameOver.equals("It's a cat's game.")) return -1;
        if(gameOver.equals(playerNames[0]+" is the winner.")) return 0;
        return 1;
    }

    public boolean checkPromotion(Piece piece, int col,CheckerHumanPlayer chp){
        if(piece.getPieceType() != Piece.PieceType.PAWN){return false;}
        if(piece.getPieceColor() == Piece.Colors.RED && col == 0){
            //return new Piece(Piece.PieceType.QUEEN, Piece.Colors.RED, piece.getX(), 0);
    public boolean checkPromotion(Pieces piece, int col, CheckerHumanPlayer1 chp){
        if(piece.getType() != 0){return false;}
        if(piece.getColors() == Pieces.Colors.RED && col == 0){
            //return new Piece(Piece.PieceType.QUEEN, Piece.ColorType.WHITE, piece.getX(), 0);
            return true;
        }else if(piece.getPieceColor() == Piece.Colors.BLACK && col == 7){
        }else if(piece.getColors() == Pieces.Colors.BLACK && col == 7){
            return true;
        }
        return false;
    }
}
