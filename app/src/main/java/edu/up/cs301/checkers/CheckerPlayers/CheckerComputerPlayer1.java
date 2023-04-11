package edu.up.cs301.checkers.CheckerPlayers;

import java.util.ArrayList;
import java.util.Collections;

import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.Views.Pieces;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.tictactoe.tttActionMessage.TTTMoveAction;

public class CheckerComputerPlayer1 extends GameComputerPlayer {

    private Pieces selection;
    private ArrayList<Pieces> availablePieces;
    private ArrayList<Integer> ints;

    /*
     * Constructor for the CheckerComputerPlayer1 class
     */
    public CheckerComputerPlayer1(String name) {
        // invoke superclass constructor
        super(name); // invoke superclass constructor
    }

    /**
     * Called when the player receives a game-state (or other info) from the
     * game.
     *
     * @param info
     * 		the message from the game
     */
    @Override
    protected void receiveInfo(GameInfo info) {

        // if it was a "not your turn" message, just ignore it
        if (info instanceof NotYourTurnInfo) return;
        // ignore illegel move info
        if (info instanceof IllegalMoveInfo) return;
        // ignore any other types
        if (!(info instanceof CheckerState)) {
            return;
        }

        CheckerState checkerState = new CheckerState((CheckerState) info);

        // check move
        if (checkerState.getWhoseMove() == 1 && playerNum == 0) {
            return;
        }
        if (checkerState.getWhoseMove() == 0 && playerNum == 1) {
            return;
        }

        // pieces that can move for ai
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkerState.getDrawing(i, j) == 1) {
                    return;
                }
                if (checkerState.getDrawing(i, j) == 3) {
                    sleep(1);
                }
                Pieces piece = checkerState.getPiece(i, j);
                if (playerNum == 0 && piece.getColors() == Pieces.Colors.RED) {
                    availablePieces.add(piece);
                }
                else if (playerNum == 1 && piece.getColors() == Pieces.Colors.BLACK) {
                    availablePieces.add(piece);
                }
            }
        }

        // shuffle all the pieces in the availablePieces ArrayList
        Collections.shuffle(availablePieces);
        selection = availablePieces.get(0);

        // variables that hold coords of selected position
        int xVal = selection.getX();
        int yVal = selection.getY();

        // call selection game action
        game.sendAction(new CheckerSelectAction(this,xVal,yVal));

        // check if piece can move
        CheckerState checkerState2 = (CheckerState) game.getGameState();
        for (int i = 1; i < availablePieces.size(); i++) {
            if (!checkerState2.getCanMove()) {
                selection = availablePieces.get(i);
                xVal = selection.getX();
                yVal = selection.getY();
                game.sendAction(new CheckerSelectAction(this, xVal, yVal));
            }
            else {
                break;
            }
        }
        // delay for a second to make opponent think we're thinking
        sleep(1);

        //check if game is over
        if (checkerState2.getGameOver()) {
            return;
        }

        // ArrayList holds index values of movements
        ArrayList<Integer> index = new ArrayList<>();

        // add all of the indexes
        for (int i = 0; i < checkerState2.getNewXMoves().size(); i++) {
            index.add(i);
        }

        // shuffle the indexes to obtain random values
        Collections.shuffle(index);

        // set x and y to the new movements at the given index
        xVal = checkerState2.getNewXMoves().get(index.get(0));
        yVal = checkerState2.getNewYMoves().get(index.get(0));

        // check for promotion
        if (selection.getType() == 0) {
            if (selection.getColors() == Pieces.Colors.BLACK){
                if (xVal == 7) {
                    sendPromotionAction(xVal, yVal, Pieces.Colors.BLACK);
                }
            } else if (selection.getColors() == Pieces.Colors.RED) {
                if (xVal == 0) {
                    sendPromotionAction(xVal,yVal, Pieces.Colors.RED);
                }
            }
        }
        // Submit our move to the game object. We haven't even checked it it's
        // our turn, or that that position is unoccupied. If it was not our turn,
        // we'll get a message back that we'll ignore. If it was an illegal move,
        // we'll end up here again (and possibly again, and again). At some point,
        // we'll end up randomly pick a move that is legal.
        Logger.log("CheckerComputer", "Sending move");
        game.sendAction(new TTTMoveAction(this, xVal, yVal));

    }

    public void sendPromotionAction(int xVal, int yVal, Pieces.Colors color) {
        game.sendAction(new CheckerPromotionAction(this, new Pieces(1, color, xVal, yVal), xVal, yVal));
    }
}
