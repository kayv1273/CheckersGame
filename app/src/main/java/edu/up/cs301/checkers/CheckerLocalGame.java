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

    private static final String TAG = "CheckerLocalGame";
    // Pieces that was selected by row and column
    private int tempRow;
    private int tempCol;

    // all of the initial movements of the Pieces selected
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
     * Constructor for the CheckerLocalGame with loaded CheckerState
     *
     * @param CheckerState
     */
    public CheckerLocalGame(CheckerState CheckerState) {
        super();
        super.state = new CheckerState(CheckerState);
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

        // Checker
        // determine if a player is put into check, if the player is in
        // check then go through every Pieces that player has on the board
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
        } else if (winCondition.equals("W")) {
            return "RED Wins! ";
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

            // highlight the Pieces they tapped
            state.setHighlight(row, col);

            // save temps for the row and col for movement later
            tempRow = row;
            tempCol = col;

            // the selected Pieces
            Pieces p = state.getPiece(row, col);

            // find all movements of Pieces selected
            findMovement(state, p);

            // make fake movements and determine if that movement allows the
            // players own king be in check
            moveToNotBeInDanger(state, p.getColors(), p.getType());

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

            // if they have no selected Pieces movement shouldn't occur
            if (tempRow == -1 || tempCol == -1) {
                return false;
            }

            //very specific castling case- the selected Pieces and Pieces being moved to are the same color
            if(state.getPiece(tempRow, tempCol).getColors() == state.getPiece(row, col).getColors()){
                if(tempRow == 4 && tempCol == 7 && row == 7 && col == 7){

                }
            }



            Pieces tempP = state.getPiece(tempRow, tempCol);

            // determine what team is moving (RED/black) and move the Pieces
            if (tempP.getColors() == Pieces.Colors.RED) {
                if (!setMovement(state, row, col, Pieces.Colors.RED)) {
                    state.removeHighlight();
                    state.removeCircle();
                    return false;
                }
            } else if (tempP.getColors() == Pieces.Colors.BLACK) {
                if (!setMovement(state, row, col, Pieces.Colors.BLACK)) {
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
     * Finds all of the positions the Pieces p can move to with normal movements
     *
     * @param state the current state of the game
     * @param p     the Pieces that is currently selected
     */
    public void findMovement(CheckerState state, Pieces p) {
        // make sure the arraylists are empty before they are filled
        initialMovementsX.clear();
        initialMovementsY.clear();

        // search through each type of Pieces and generate all of the movements
        // of that Pieces and add them to the initialMovement arraylists.
        if (p.getType() == 0) {
            Pawn pawn = new Pawn(p, state, p.getColors());
            for (int i = 0; i < pawn.getX().size(); i++) {
                initialMovementsX.add(pawn.getX().get(i));
                initialMovementsY.add(pawn.getY().get(i));
            }
        } else if (p.getType() == 1) {
            Pawn king = new Pawn(p, state, p.getColors());
            for (int i = 0; i < king.getX().size(); i++) {
                initialMovementsX.add(king.getX().get(i));
                initialMovementsY.add(king.getY().get(i));
            }
        }
    }

    /**
     * Determines if the current players king is in check with a certain
     * Pieces movement
     *
     * @param state      the copied state displaying a movement
     * @param teamColor  the color the player that is making a movement
     * @param enemyColor the color of the other player
     * @return Determines if a king is in check
     */
    public boolean checkDanger(CheckerState state, Pieces.Colors teamColor, Pieces.Colors enemyColor) {
        // search through every Pieces of the enemy and generate its general movement
        // with its position on the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Pieces Pieces = state.getPiece(row, col);
                if (Pieces.getColors() == enemyColor) {
                    if (Pieces.getType() == 0) {
                        Pawn pawn = new Pawn(Pieces, state, enemyColor);
                        state.setCircles(pawn.getXCaptures(), pawn.getYCaptures());
                    }  else if (Pieces.getType() == 1) {
                        Pawn king = new Pawn(Pieces, state, enemyColor);
                        state.setCircles(king.getXCaptures(), king.getYCaptures());
                    }
                }
            }
        }

        // determine what king will be in check with the movements generated
        Pieces king = null;

        if (king != null) {
            // if the king can be attacked by the enemy it means the king is in check
            if (state.getDrawing(king.getX(), king.getY()) == 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looks through all movements of the Pieces selected and determines
     * if that movement causes the players king to be in check
     *
     * @param state the current state of the game
     * @param color the color of the player that is making a move
     * @param pieceType the PieceType of the selected Pieces
     */
    public void moveToNotBeInDanger(CheckerState state, Pieces.Colors color, int pieceType) {
        // make sure the arraylists are empty
        newMovementsX.clear();
        newMovementsY.clear();

        // iterate through all of the initial movements of the selected Pieces
        for (int i = 0; i < initialMovementsX.size(); i++) {

            // create a copied state so the current state is not affected yet
            CheckerState copyState = new CheckerState(state);

            // make one of the initial movements on the copied state
            makeTempMovement(copyState, initialMovementsX.get(i), initialMovementsY.get(i));

            // determine if the player is RED or black so that can be passed
            // in as a parameter
            if (color == Pieces.Colors.RED) {

                // determine if the movement causes the players king to be in check
                if (!checkDanger(copyState, color, Pieces.Colors.BLACK)) {

                    // if the player is not in check add that movement to the new
                    // arraylist so it can be saved
                    newMovementsX.add(initialMovementsX.get(i));
                    newMovementsY.add(initialMovementsY.get(i));
                }
            } else if (color == Pieces.Colors.BLACK) {
                if (!checkDanger(copyState, color, Pieces.Colors.RED)) {
                    newMovementsX.add(initialMovementsX.get(i));
                    newMovementsY.add(initialMovementsY.get(i));
                }
            }
            state.setNewXMoves(newMovementsX);
            state.setNewYMoves(newMovementsY);
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
        //for RED
        if(state.getPiece(4, 7).getType() == 1 &&
                state.getPiece(4, 7).getColors() == Pieces.Colors.RED && state.getWhoseMove() == 0) {
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
        else if(state.getPiece(4, 0).getType() == 1 &&
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
     * @param row   the row position of the selected Pieces
     * @param col   the column position of the selected Pieces
     */
    public void makeTempMovement(CheckerState state, int row, int col) {
        // create the temp Pieces with the selected position (tempRow, tempCol)
        Pieces tempPiece = state.getPiece(tempRow, tempCol);

        // if they are moving a king determine who's king (RED/black) and
        // update the position of the king so when it checks for if the king
        // is in check it knows the new position
        if (tempPiece.getType() == 1) {
            if (tempPiece.getColors() == Pieces.Colors.RED) {
                state.setPiece(row, col, tempPiece);
            } else if (tempPiece.getColors() == Pieces.Colors.BLACK) {
                state.setPiece(row, col, tempPiece);
            }
        }
        // make the location the Pieces is moving to become the selected Pieces
        state.setPiece(row, col, tempPiece);

        // make the selected Pieces become empty since the Pieces has moved
        state.setPiece(tempRow, tempCol, state.emptyPiece);
    }

    /**
     * Move the Pieces that was selected to the new position
     * that the player wants to move to
     *
     * @param state the current state of the game
     * @param row   the row of the position the player is moving to
     * @param col   the column of the position the player is moving to
     * @param color the color of the Pieces they selected previously
     * @return tells weather the move was valid and happened
     */
    public boolean setMovement(CheckerState state, int row, int col, Pieces.Colors color) {
        // if they selected a dot/ring then move
        if (state.getDrawing(row, col) == 2 || state.getDrawing(row, col) == 4) {

            //adds captured Pieces to captured pieces array t
            if (state.getPiece(row, col).getColors() != Pieces.Colors.EMPTY) {
                if(state.getPiece(row, col).getColors() == Pieces.Colors.BLACK) {
                    state.addRedCapturedPiece(state.getPiece(row, col));
                } else {
                    state.addBlackCapturedPiece(state.getPiece(row, col));
                }
            }

//            for (Pieces p : state.getWhiteCapturedPieces()) {
//                Log.d("Testing", p.getType().toString());
//            }

            Pieces tempPiece = state.getPiece(tempRow, tempCol);
            Pieces castlingTempPiece = state.getPiece(row, col);

            //very specific castling case- the selected Pieces is a king and moving two squares
            if(state.getPiece(tempRow, tempCol).getType() == 1 && (Math.abs(row-tempRow) == 2)){
                // for each sub case- this takes care of moving the rook, the king then moves normally
                if(tempRow == 4 && tempCol == 7 && row == 6 && col == 7){
                    state.setPiece(5,7, state.getPiece(7,7));
                    state.setPiece(7,7,state.emptyPiece);
                }
                else if(tempRow == 4 && tempCol == 7 && row == 2 && col == 7){
                    state.setPiece(3,7, state.getPiece(0,7));
                    state.setPiece(0,7,state.emptyPiece);
                }
                else if(tempRow == 4 && tempCol == 0 && row == 6 && col == 0){
                    state.setPiece(5,0, state.getPiece(7,0));
                    state.setPiece(7,0,state.emptyPiece);
                }
                else if(tempRow == 4 && tempCol == 0 && row == 2 && col == 0){
                    state.setPiece(3,0, state.getPiece(0,0));
                    state.setPiece(0,0,state.emptyPiece);
                }

            }


            // change the location of the king to be at the new square if it is going to be moved
            if (tempPiece.getType() == 1) {
                if (tempPiece.getColors() == Pieces.Colors.RED) {
                    state.setPiece(row, col, tempPiece);
                } else if (tempPiece.getColors() == Pieces.Colors.BLACK) {
                    state.setPiece(row, col, tempPiece);
                }
            }

            // set the new position to be the Pieces they originally selected
            boolean isCapture = state.getPiece(row,col).getColors() != Pieces.Colors.EMPTY;
            CheckerHumanPlayer1 chp = players[0] instanceof CheckerHumanPlayer1 ?
                    (CheckerHumanPlayer1) players[0] : (CheckerHumanPlayer1) players[1];
            if(CheckerPromotionAction.isPromotion){
                state.setPiece(promo.getRow(),promo.getCol(),promo.getPromotionPiece());
                CheckerPromotionAction.isPromotion = false;
            }else {
                state.setPiece(row, col, state.getPiece(tempRow, tempCol));
            }

            // change the Pieces at the selection to be an empty Pieces
            state.setPiece(tempRow, tempCol, state.emptyPiece);

            // remove all highlights
            state.removeHighlight();

            // reset temp values so only selections may occur
            tempRow = -1;
            tempCol = -1;

            // remove all the circles after moving
            state.removeCircle();


            if (color == Pieces.Colors.BLACK) {
                if (checkDanger(state, Pieces.Colors.RED, color)) {

                    winCondition = checkDangermate(state);
                    checkIfGameOver();
                } else {
                    winCondition = checkForStalemate(state);
                    checkIfGameOver();
                }
            } else if (color == Pieces.Colors.RED) {
                if (checkDanger(state, Pieces.Colors.BLACK, color)) {

                    winCondition = checkDangermate(state);
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
        Pieces.Colors color = null;
        // find what color has moved to put the other player in checkmate
        if(state.getWhoseMove() == 0) {
            // if it is now whites turn that means to look for if black is in stalemate
            color = Pieces.Colors.BLACK;
        } else {
            // if it is now blacks turn that means to look for if RED is in stalemate
            color = Pieces.Colors.RED;
        }

        // arraylist that adds all enemy pieces
        ArrayList<Pieces> pieces = new ArrayList<>();
        // add all pieces to arraylist
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(state.getPiece(i, j).getColors() == color) {
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
            moveToNotBeInDanger(state, color, state.getPiece(tempRow, tempCol).getType());
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

    public String checkDangermate(CheckerState state) {
        // if a player is not in check then then there is no checkmate yet

        Pieces.Colors color = null;
        // find what color has moved to put the other player in checkmate
        if(state.getWhoseMove() == 0) {
            // if it is now whites turn that means black put RED in checkmate
            color = Pieces.Colors.BLACK;
        } else if (state.getWhoseMove() == 1) {
            // if it is now blacks turn that means RED put black in checkmate
            color = Pieces.Colors.RED;
        }

        // arraylist that holds all pieces of player that is in check
        ArrayList<Pieces> pieces = new ArrayList<>();
        // add all pieces to arraylist
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(state.getPiece(i, j).getColors() == color) {
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
            moveToNotBeInDanger(state, color, state.getPiece(tempRow, tempCol).getType());
            if(newMovementsX.size() > 0) {
                return null; // no winner yet
            }
        }
        // since a return was never made that means the player has
        // no possible movements and is in checkmate, so the player
        // who put the other player in check is now the winner.
        if (color == Pieces.Colors.RED) {
            state.setGameOver(true);
            return "B";
        } else if (color == Pieces.Colors.BLACK) {
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

    public boolean checkPromotion(Pieces p, int col,CheckerHumanPlayer1 chp){
        if(p.getType() != 1){return false;}
        if(p.getColors() == Pieces.Colors.RED && col == 0){
            //return new Pieces(Pieces.PieceType.QUEEN, Pieces.Colors.RED, Pieces.getX(), 0);
            return true;
        }else if(p.getColors() == Pieces.Colors.BLACK && col == 7){
            return true;
        }
        return false;
    }
}
