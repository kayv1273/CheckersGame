package edu.up.cs301.checkers.CheckerPlayers;

import java.util.ArrayList;
import java.util.Collections;

import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.InfoMessage.Piece;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;

/**
 * @author Griselda
 * @author Katherine
 * @author Ruth
 * @author Nick
 * @author Ethan
 * @version 4.26.2023
 */


public class CheckerComputerPlayer1 extends GameComputerPlayer {

    private Piece selection;
    private ArrayList<Piece> availablePieces;

    /**
     * Constructor for the CheckerComputerPlayer1 class
     * @param name name of dumb ai
     */
    public CheckerComputerPlayer1(String name) {
        // invoke superclass constructor
        super(name);
    }

    /**
     * Called when the player receives a game-state (or other info) from the
     * game.
     *
     * @param info the message from the game
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        //if it was a "not your turn" message, just ignore it
        if (info instanceof NotYourTurnInfo) return;
        //ignore illegal move info too
        if (info instanceof IllegalMoveInfo) return;
        //just in case there is any other types of info, ignore it
        if(!(info instanceof CheckerState)){
            return;
        }

        //change info to type CheckerState
        CheckerState checkerState = new CheckerState((CheckerState) info);

        //if not turn, return
        if (checkerState.getWhoseMove() == 1 && playerNum == 0) return;
        if (checkerState.getWhoseMove() == 0 && playerNum == 1) return;

        //get all of the pieces that can move on the computers side
        availablePieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int k = 0; k < 8; k++) {
                if (checkerState.getDrawing(i, k) == 1) {
                    return;
                }
                if (checkerState.getDrawing(i, k) == 3) {
                    sleep(1);
                }

                //get the piece
                Piece p = checkerState.getPiece(i, k);
                if (playerNum == 0 && p.getPieceColor() == Piece.ColorType.RED) {
                    availablePieces.add(p);
                } else if (playerNum == 1 && p.getPieceColor() == Piece.ColorType.BLACK) {
                    availablePieces.add(p);
                }
            }
        }

        // randomly shuffle the pieces in the array
        Collections.shuffle(availablePieces);

        // Returns if there are no more pieces left
        if (availablePieces.isEmpty()) return;
        selection = availablePieces.get(0); // select a random piece

        //create variables to hold the x and y of the position selected
        int xVal = selection.getX();
        int yVal = selection.getY();

        //call the selection game action
        game.sendAction(new CheckerSelectAction(this, xVal, yVal));

        //check if the piece is one that can move
        CheckerState checkerState2 = (CheckerState) game.getGameState();
        for (int i = 1; i < availablePieces.size(); i++) {
            if (!checkerState2.getCanMove()) {
                selection = availablePieces.get(i);
                xVal = selection.getX();
                yVal = selection.getY();
                game.sendAction(new CheckerSelectAction(this, xVal, yVal));
            } else {
                break;
            }
        }
        sleep(1);

        //check if game is over and return
        if(checkerState2.getGameOver()) {
            return;
        }
        // an arraylist that holds the index values of the two movement arraylists (x and y)
        ArrayList<Integer> index = new ArrayList<>();

        // add all of the indexes into the ints value
        for (int i = 0; i < checkerState2.getNewMovementsX().size(); i++) {
            index.add(i);
        }
        // shuffle the indexes so a random x and y value can be taken
        Collections.shuffle(index);

        // set the x and y values to the new movements array at the index
        xVal = checkerState2.getNewMovementsX().get(index.get(0));
        yVal = checkerState2.getNewMovementsY().get(index.get(0));

        // if the piece is a pawn look for promotion
        if (selection.getPieceType() == Piece.PieceType.PAWN) {
            if (selection.getPieceColor() == Piece.ColorType.BLACK) {
                if (yVal == 7) {
                    sendPromotionAction(xVal, yVal, Piece.ColorType.BLACK);
                }
            } else if (selection.getPieceColor() == Piece.ColorType.RED) {
                if (yVal == 0) {
                    sendPromotionAction(xVal, yVal, Piece.ColorType.RED);
                }
            }
        }


        // send the new move action
        game.sendAction(new CheckerMoveAction(this, xVal, yVal));

        // logic for if the AI wins
        // assume true
        boolean gameOver = true;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if (checkerState2.getPiece(i,j).getPieceColor() == Piece.ColorType.RED) {
                    // if any black pieces remain, the game is not over
                    gameOver = false;
                }
            }
        }

        if(gameOver) {
            //line used to debug and ensure proper functionality
            System.out.println("test");
        }
    }

    /**
     * Helper method that calls promotion when a piece reaches the end of the board
     *
     * @param xVal current x value
     * @param yVal current y value
     * @param type the current type of the piece
     */
    public void sendPromotionAction(int xVal, int yVal, Piece.ColorType type) {
        game.sendAction(new CheckerPromotionAction(this,
                new Piece(Piece.PieceType.KING, type, xVal, yVal), xVal, yVal));
    }
}
