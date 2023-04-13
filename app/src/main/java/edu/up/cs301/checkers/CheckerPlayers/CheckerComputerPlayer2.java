package edu.up.cs301.checkers.CheckerPlayers;

import java.util.ArrayList;
import java.util.Collections;

import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.InfoMessage.Piece;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;

public class CheckerComputerPlayer2 extends GameComputerPlayer {
    private Piece selection;
    private ArrayList<Piece> availablePieces;
    private ArrayList<Integer> ints;

    /**
     * Constructor for the CheckerComputerPlayer1 class
     */
    public CheckerComputerPlayer2(String name) {
        // invoke superclass constructor
        super(name); // invoke superclass constructor
    }

    /**
     * Called when the player receives a game-state (or other info) from the
     * game.
     *
     * @param info the message from the game
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        // if it was a "not your turn" message, just ignore it
        if (info instanceof NotYourTurnInfo) return;
        //Ignore illegal move info too
        if (info instanceof IllegalMoveInfo) return;
        CheckerState chessState = new CheckerState((CheckerState) info);
        //if(chessState.isPromoting){return;}
        if (chessState.getWhoseMove() == 1 && playerNum == 0) {
            return;
        }
        if (chessState.getWhoseMove() == 0 && playerNum == 1) {
            return;
        }

        // all of the pieces that can move on the computers side
        availablePieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int k = 0; k < 8; k++) {
                if (chessState.getDrawing(i, k) == 1) {
                    return;
                }
                if (chessState.getDrawing(i, k) == 3) {
                    sleep(1);
                }
                Piece p = chessState.getPiece(i, k);
                if (playerNum == 0 && p.getPieceColor() == Piece.ColorType.RED) {
                    availablePieces.add(p);
                } else if (playerNum == 1 && p.getPieceColor() == Piece.ColorType.BLACK) {
                    availablePieces.add(p);
                }
            }
        }
        // randomly shuffle the pieces in the array
        Collections.shuffle(availablePieces);
        selection = availablePieces.get(0);
        // create variables to hold the x and y of the position selected
        int xVal = selection.getX();
        int yVal = selection.getY();
        // call the selection game action
        game.sendAction(new CheckerSelectAction(this, xVal, yVal));
        // check if the piece is one that can move
        CheckerState chessState2 = (CheckerState) game.getGameState();
        for (int i = 1; i < availablePieces.size(); i++) {
            if (!chessState2.getCanMove()) {
                selection = availablePieces.get(i);
                xVal = selection.getX();
                yVal = selection.getY();
                game.sendAction(new CheckerSelectAction(this, xVal, yVal));
            } else {
                break;
            }
        }
        sleep(1);

        if(chessState2.getGameOver()) {
            return;
        }
        // an arraylist that holds the index values of the two movement arraylists (x and y)
        ArrayList<Integer> index = new ArrayList<>();
        // add all of the indexes into the ints value
        for (int i = 0; i < chessState2.getNewMovementsX().size(); i++) {
            index.add(i);
        }
        // shuffle the indexes so a random x and y value can be taken
        Collections.shuffle(index);
        // set the x and y values to the new movements array at the index
        for(int i = 0; i < index.size(); i++) {
            xVal = chessState2.getNewMovementsX().get(index.get(i));
            yVal = chessState2.getNewMovementsY().get(index.get(i));
            if (chessState2.getPiece(xVal, yVal).getPieceColor() != Piece.ColorType.EMPTY) {
                break;
            }
        }
        // if the piece is a pawn look for promotion
        if (selection.getPieceType() == Piece.PieceType.PAWN) {
            if (selection.getPieceColor() == Piece.ColorType.BLACK) {
                if (yVal == 7) {
                    game.sendAction(new CheckerPromotionAction(this,
                            new Piece(Piece.PieceType.KING,
                                    Piece.ColorType.BLACK, xVal, yVal), xVal, yVal));

                }
            } else if (selection.getPieceColor() == Piece.ColorType.RED) {
                if (yVal == 0) {
                    game.sendAction(new CheckerPromotionAction(this,
                            new Piece(Piece.PieceType.KING,
                                    Piece.ColorType.RED, xVal, yVal), xVal, yVal));

                }
            }
        }
        // send the new move action
        game.sendAction(new CheckerMoveAction(this, xVal, yVal));
    }
}