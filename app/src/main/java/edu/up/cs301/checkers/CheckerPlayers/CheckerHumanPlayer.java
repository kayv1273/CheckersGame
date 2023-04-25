package edu.up.cs301.checkers.CheckerPlayers;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.Views.BlackCaptureSurfaceView;
import edu.up.cs301.checkers.Views.CheckerBoardSurfaceView;
import edu.up.cs301.checkers.InfoMessage.Piece;
import edu.up.cs301.checkers.Views.RedCaptureSurfaceView;
import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.utilities.MessageBox;
import edu.up.cs301.game.R;

/**
 * @author Griselda
 * @author Katherine
 * @author Ruth
 * @author Nick
 * @author Ethan
 * @version 4.13.2023
 */

public class CheckerHumanPlayer extends GameHumanPlayer implements View.OnTouchListener{

    //Tag for logging
    private static final String TAG = "CheckerHumanPlayer";

    // the surface view
    private CheckerBoardSurfaceView surfaceView;
    public TextView movesLog;
    private CheckerBoardSurfaceView surfaceViewCheckerBoard;
    private Button resignButton;

    //captures surfaceview
    private BlackCaptureSurfaceView surfaceViewBlackCapture;
    private RedCaptureSurfaceView surfaceViewWhiteCapture;

    //names
    private TextView player1name;
    private TextView player2name;

    public boolean isPromotion;
    public Piece currPiece = new Piece(Piece.PieceType.KING, Piece.ColorType.RED, 0, 0);
    private int savedX = 0;
    private int savedY = 0;

    // the ID for the layout to use
    private int layoutId;

    private CheckerState state;
    private int numTurns;
    private boolean justStarted;
    private int x = 8;
    private int y = 8;

    /**
     * constructor
     *
     * @param name the name of the player
     */
    public CheckerHumanPlayer(String name, int layoutId, CheckerState state) {
        super(name);
        this.layoutId = layoutId;
        numTurns = 1;
        justStarted = true;
        isPromotion = false;
        this.state = state;
    }

    public void setState(CheckerState state) {
        this.state = state;
    }

    @Override
    public void receiveInfo(GameInfo info) {
        if (surfaceViewCheckerBoard == null) {
            return;
        }

        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            // if the move was out of turn or otherwise illegal, flash the screen
            surfaceViewCheckerBoard.flash(Color.RED, 50);
        } else if (!(info instanceof CheckerState)) {
            // if we do not have a state, ignore
            return;
        } else {
            surfaceViewCheckerBoard.setState((CheckerState) info);
            surfaceViewCheckerBoard.invalidate();

            surfaceViewWhiteCapture.setState(state);
            surfaceViewBlackCapture.setState(state);

            surfaceViewBlackCapture.invalidate();
        }

    }

    /**
     * sets the current player as the activity's GUI
     */
    @Override
    public void setAsGui(GameMainActivity activity) {
        // Load the layout resource for the new configuration
        activity.setContentView(layoutId);

        // set the surfaceView instance variable
        surfaceView = (CheckerBoardSurfaceView) myActivity.findViewById(R.id.checkerBoard);
        surfaceView.setOnTouchListener(this);

        //moves log
        movesLog = myActivity.findViewById(R.id.movesLog);
        surfaceViewCheckerBoard = (CheckerBoardSurfaceView) myActivity.findViewById(R.id.checkerBoard);

        //player names
        player1name = myActivity.findViewById(R.id.nameBlack);
        player2name = myActivity.findViewById(R.id.nameWhite);

        //resignation
        resignButton = myActivity.findViewById(R.id.homeButton);

        //captures
        surfaceViewWhiteCapture = (RedCaptureSurfaceView) myActivity.findViewById(R.id.whiteCaptures);
        surfaceViewBlackCapture = (BlackCaptureSurfaceView) myActivity.findViewById(R.id.blackCaptures);


        surfaceViewCheckerBoard.setOnTouchListener(this);
        resignButton.setOnTouchListener(this);
    }


    /**
     * returns the GUI's top view
     *
     * @return the GUI's top view
     */
    @Override
    public View getTopView() {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * perform any initialization that needs to be done after the player
     * knows what their game-position and opponents' names are.
     */
    protected void initAfterReady() {
        myActivity.setTitle("Checker: " + allPlayerNames[0] + " vs. " + allPlayerNames[1]);
        if (allPlayerNames.length == 2) {
            player1name.setText(allPlayerNames[0]);
            player2name.setText(allPlayerNames[1]);
        }
    }


    /**
     * callback method when the screen it touched. We're
     * looking for a screen touch (which we'll detect on
     * the "up" movement" onto a tic-tac-tie square
     *
     * @param motionEvent the motion event that was detected
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == resignButton.getId()) {
            MessageBox.popUpMessage("You are exiting the game:\n Returning to Home Screen", myActivity);
            CountDownTimer cdt = new CountDownTimer(3000, 10) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    myActivity.recreate();
                }
            };
            cdt.start();
        }

        // ignore if not an "down" event
        if (motionEvent.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        // loop through all of the locations on the board and compare
        // the location pressed to the pixels on the screen to find
        // the exact location of the click according to the b oard
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!isPromotion) {
                    if (motionEvent.getX() > 20 + (i * 115) && motionEvent.getX() < 175 + (i * 115)) {
                        if (motionEvent.getY() > 20 + (j * 115) && motionEvent.getY() < 175 + (j * 115)) {
                                // create the select action
                            if (state.getPiece(i, j).getPieceColor() == Piece.ColorType.RED && state.getWhoseMove() == 0) {
                                CheckerSelectAction select = new CheckerSelectAction(this, i, j);
                                currPiece = state.getPiece(i, j);
                                game.sendAction(select);
                            } else if (state.getPiece(i, j).getPieceColor() == Piece.ColorType.BLACK && state.getWhoseMove() == 1) {
                                CheckerSelectAction select = new CheckerSelectAction(this, i, j);
                                currPiece = state.getPiece(i, j);
                                game.sendAction(select);
                            } else if (state.getPiece(i, j).getPieceColor() != Piece.ColorType.RED && state.getWhoseMove() == 0) {
                                if (j == 0 && currPiece.getPieceType() == Piece.PieceType.PAWN && state.getWhoseMove() == this.playerNum) {
                                    if (!validPawnMove(i, j, currPiece)) {
                                        CheckerMoveAction move = new CheckerMoveAction(this, i, j);
                                        game.sendAction(move);
                                        break;
                                    }
                                    break;
                                }
                                CheckerMoveAction move = new CheckerMoveAction(this, i, j);
                                game.sendAction(move);
                            } else if (state.getPiece(i, j).getPieceColor() != Piece.ColorType.BLACK && state.getWhoseMove() == 1) {
                                if (j == 7 && currPiece.getPieceType() == Piece.PieceType.PAWN && state.getWhoseMove() == this.playerNum) {
                                    if (!validPawnMove(i, j, currPiece)) {
                                        CheckerMoveAction move = new CheckerMoveAction(this, i, j);
                                        game.sendAction(move);
                                        break;
                                    }
                                    break;
                                }
                                CheckerMoveAction move = new CheckerMoveAction(this, i, j);
                                game.sendAction(move);
                            }
                            surfaceViewCheckerBoard.invalidate();
                            // surfaceViewWhiteCapture.invalidate();
                            // surfaceViewBlackCapture.invalidate();
                        }
                    }
                    if (isPromotion) {
                        sendPromotionAction(i, j, Piece.ColorType.RED);
                        surfaceViewCheckerBoard.invalidate();
                    }
                }
            }
        }
        // register that we have handled the event
        return true;
    }

/**
    public void displayMovesLog ( int currRow, int currCol, int tempRow, CheckerState state,
                                  boolean isCapture){
        if (state == null) return;
        Piece.PieceType currPiece = state.getPiece(currRow, currCol).getPieceType();
        String toReturn = "";
        if (justStarted) {
            movesLog.append("\n");
            justStarted = false;
        }
        boolean whitesTurn = state.getWhoseMove() == 0;
        if (whitesTurn) {
            toReturn += numTurns + ")";
        }
        if (currPiece == Piece.PieceType.KING) {
            toReturn += "K";
            if (isCapture && currPiece == Piece.PieceType.PAWN) {
                toReturn += determineRow(tempRow);
                toReturn += "x";
            } else if (isCapture) {
                toReturn += "x";
            }
            toReturn += determineRow(currRow);
            currCol = 8 - currCol;
            toReturn += currCol + " ";
            if (!whitesTurn) {
                numTurns++;
                toReturn += "\n";
            }
            movesLog.append(toReturn);

        }
    }

    private char determineRow(int row) {
        switch (row) {
            case (0):
                return 'a';
            case (1):
                return 'b';
            case (2):
                return 'c';
            case (3):
                return 'd';
            case (4):
                return 'e';
            case (5):
                return 'f';
            case (6):
                return 'g';
            case (7):
                return 'h';
        }
        return 'q';
    }

**/

    public void sendPromotionAction(int xVal, int yVal, Piece.ColorType type) {
        game.sendAction(new CheckerPromotionAction(this,
                new Piece(Piece.PieceType.KING, type, xVal, yVal), xVal, yVal));
    }
    /**
    public void makePromotion(Piece.PieceType type) {
        Piece.ColorType currColor = state.getWhoseMove() == 0 ? Piece.ColorType.RED : Piece.ColorType.BLACK;
        Piece set = new Piece(type, currColor, savedX, savedY);
        CheckerPromotionAction promo = new CheckerPromotionAction(this, set ,savedX,savedY);
        game.sendAction(promo);
        CheckerMoveAction move = new CheckerMoveAction(this, savedX, savedY);
        game.sendAction(move);
        isPromotion = false;

    }
    **/

    public void checkBoardPromotion(CheckerState state) {
        for (int i = 0; i < 8; i++) {
            if (state.getPiece(i,0).getColorType() == Piece.ColorType.RED) {
                  sendPromotionAction(i,0, Piece.ColorType.RED);
                  surfaceViewCheckerBoard.invalidate();
            }
        }
    }

    public boolean validPawnMove(int row, int col, Piece currPiece) {
        if(currPiece.getY() > col + 1){
            return false;
        }
        if (currPiece.getX() != row && currPiece.getX() != row - 1 && currPiece.getX() != row + 1) {
            return false;
        }
        if (currPiece.getX() == row) {
            if (state.getPiece(row, col).getPieceType() != Piece.PieceType.EMPTY) {
                return false;
            }
        }

        if (currPiece.getX() == row + 1 || currPiece.getX() == row - 1) {
            if (state.getPiece(row, col).getPieceType() == Piece.PieceType.EMPTY) {
                return false;
            }
        }
        return true;
    }
}
