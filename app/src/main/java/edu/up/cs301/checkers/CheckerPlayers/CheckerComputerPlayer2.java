package edu.up.cs301.checkers.CheckerPlayers;

import java.util.ArrayList;
import java.util.Collections;

import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.InfoMessage.Pieces;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.utilities.Logger;

public class CheckerComputerPlayer2 extends GameComputerPlayer {

    private Pieces selection;
    private ArrayList<Pieces> availablePieces;
    private ArrayList<Integer> ints;

    /*
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

        availablePieces = new ArrayList<>();
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

        int maxScore = 0;
        Pieces bestPiece = null;

        for (Pieces piece : availablePieces) {
            int score = getMoveScore(piece, checkerState);
            if (score > maxScore) {
                maxScore = score;
                bestPiece = piece;
            }
        }

        if (bestPiece != null) {
            // variables that hold coords of selected position
            int xVal = bestPiece.getX();
            int yVal = bestPiece.getY();

            // call selection game action
            game.sendAction(new CheckerSelectAction(this, xVal, yVal));

            // delay for a second to make opponent think we're thinking
            sleep(1);

            // check for promotion
            CheckerState checkerState2 = (CheckerState) game.getGameState();
            xVal = checkerState2.getNewXMoves().get(ints.get(0));
            yVal = checkerState2.getNewYMoves().get(ints.get(0));
            if (bestPiece.getType() == 0) {
                if (bestPiece.getColors() == Pieces.Colors.BLACK){
                    if (xVal == 7) {
                        sendPromotionAction(xVal, yVal, Pieces.Colors.BLACK);
                    }
                } else if (bestPiece.getColors() == Pieces.Colors.RED) {
                    if (xVal == 0) {
                        sendPromotionAction(xVal,yVal, Pieces.Colors.RED);
                    }
                }
            }

            Logger.log("CheckerComputer", "Sending move");
            game.sendAction(new CheckerMoveAction(this, xVal, yVal));
        }
    }
    public int getMoveScore(Pieces piece, CheckerState checkerState) {
        int score = 0;

        // perform some calculations to determine the score for the given move
        // ...

        return score;
    }

    public void sendPromotionAction(int xVal, int yVal, Pieces.Colors color) {
        game.sendAction(new CheckerPromotionAction(this, new Pieces(1, color, xVal, yVal), xVal, yVal));
    }


}
