package edu.up.cs301.checkers.CheckerPlayers;

import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.up.cs301.checkers.CheckerActionMessage.CheckerMoveAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerPromotionAction;
import edu.up.cs301.checkers.CheckerActionMessage.CheckerSelectAction;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.Views.CheckerView;
import edu.up.cs301.checkers.InfoMessage.Pieces;
import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.utilities.MessageBox;
import edu.up.cs301.game.R;

public class CheckerHumanPlayer1 extends GameHumanPlayer implements View.OnTouchListener{

    //Tag for logging
    private static final String TAG = "CheckerHumanPlayer";

    // the surface view
    private CheckerView surfaceView;
    public TextView movesLog;
    private CheckerView surfaceViewCheckerBoard;

    //names
    private TextView player1name;
    private TextView player2name;

    public Button queenPromo;
    public Button resetButton;
    public Button homeButton;
    public boolean isPromotion;
    public Pieces currPiece = new Pieces(0, Pieces.Colors.RED, 0, 0);
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
    public CheckerHumanPlayer1(String name, int layoutId, CheckerState state) {
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
            //surfaceViewCheckerBoard.flash(Color.RED, 50);
        } else if (!(info instanceof CheckerState)) {
            // if we do not have a state, ignore
            return;
        } else {
            surfaceViewCheckerBoard.setState((CheckerState) info);
            surfaceViewCheckerBoard.invalidate();
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
        surfaceView = (CheckerView) myActivity.findViewById(R.id.board);
        surfaceView.setOnTouchListener(this);

        //moves log
        //movesLog = myActivity.findViewById(R.id.movesLog);
        surfaceViewCheckerBoard = (CheckerView) myActivity.findViewById(R.id.board);

        /**
        //player names
        player1name = myActivity.findViewById(R.id.nameBlack);
        player2name = myActivity.findViewById(R.id.nameWhite);


        queenPromo = myActivity.findViewById(R.id.queenPromo);
         **/

        undisplay();
        queenPromo.setOnTouchListener(this);
        surfaceViewCheckerBoard.setOnTouchListener(this);
        resetButton.setOnTouchListener(this);
    }


    /**
     * returns the GUI's top view
     *
     * @return the GUI's top view
     */
    @Override
    public View getTopView() {
        return myActivity.findViewById(R.id.board);
    }

    /**
     * perform any initialization that needs to be done after the player
     * knows what their game-position and opponents' names are.
     */
    protected void initAfterReady() {
        myActivity.setTitle("Checkers: " + allPlayerNames[0] + " vs. " + allPlayerNames[1]);
        if(allPlayerNames.length == 2) {
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
        if (view.getId() == resetButton.getId()) {
            MessageBox.popUpMessage("Return to Home Screen", myActivity);
            CountDownTimer cdt = new CountDownTimer(3000, 10) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    myActivity.finishAffinity();
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

        if (!isPromotion) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (motionEvent.getX() > 20 + (i * 115) && motionEvent.getX() < 175 + (i * 115)) {
                        if (motionEvent.getY() > 20 + (j * 115) && motionEvent.getY() < 175 + (j * 115)) {

                            // create the select action
                            if (state.getPiece(i, j).getColors() == Pieces.Colors.RED && state.getWhoseMove() == 0) {
                                CheckerSelectAction select = new CheckerSelectAction(this, i, j);
                                currPiece = state.getPiece(i, j);
                                game.sendAction(select);
                            } else if (state.getPiece(i, j).getColors() == Pieces.Colors.BLACK && state.getWhoseMove() == 1) {
                                CheckerSelectAction select = new CheckerSelectAction(this, i, j);
                                currPiece = state.getPiece(i, j);
                                game.sendAction(select);
                            } else if (state.getPiece(i, j).getColors() != Pieces.Colors.RED && state.getWhoseMove() == 0) {
                                if (j == 0 && currPiece.getType() == 0 && state.getWhoseMove() == this.playerNum) {
                                    if (!validPawnMove(i, j, currPiece)) {
                                        CheckerMoveAction move = new CheckerMoveAction(this, i, j);
                                        game.sendAction(move);
                                        break;
                                    }
                                    promptForPromotion(i, j);
                                    break;
                                }
                                CheckerMoveAction move = new CheckerMoveAction(this, i, j);
                                game.sendAction(move);
                            } else if (state.getPiece(i, j).getColors() != Pieces.Colors.BLACK && state.getWhoseMove() == 1) {
                                if (j == 7 && currPiece.getType() == 1 && state.getWhoseMove() == this.playerNum) {
                                    if (!validPawnMove(i,j,currPiece)) {
                                        CheckerMoveAction move = new CheckerMoveAction(this, i, j);
                                        game.sendAction(move);
                                        break;
                                    }
                                    promptForPromotion(i, j);
                                    break;
                                }
                                CheckerMoveAction move = new CheckerMoveAction(this, i, j);
                                game.sendAction(move);
                            }
                            surfaceViewCheckerBoard.invalidate();
                        }
                    }
                }
                if (isPromotion) {
                    break;
                }
            }
        } else {
            if (view.getId() == queenPromo.getId()) {
                makePromotion(0);
            }
            return true;
        }

        // register that we have handled the event
        return true;
    }

    /**
    public void displayMovesLog(int currRow, int currCol, int tempRow, CheckerState state, boolean isCapture) {
        if (state == null) return;
        Pieces.PieceType currPiece = state.getPiece(currRow, currCol).getType();
        String toReturn = "";
        if (justStarted) {
            movesLog.append("\n");
            justStarted = false;
        }
        boolean whitesTurn = state.getWhoseMove() == 0;
        if (whitesTurn) {
            toReturn += numTurns + ")";
        }
        if (currPiece.getType() == 0) {
            toReturn += "P";
        } else if (currPiece.getType() == 1) {
            toReturn += "K";
        }
        if (isCapture && currPiece == Pieces.PieceType.PAWN) {
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
    **/
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

    public void undisplay() {
        queenPromo.setVisibility(View.INVISIBLE);
    }

    public void display() {
        queenPromo.setVisibility(View.VISIBLE);
    }

    public void makePromotion(int type) {
        Pieces.Colors currColor = state.getWhoseMove() == 0 ? Pieces.Colors.RED : Pieces.Colors.BLACK;
        Pieces set = new Pieces(type, currColor, savedX, savedY);
        CheckerPromotionAction promo = new CheckerPromotionAction(this,set,savedX,savedY);
        game.sendAction(promo);
        CheckerMoveAction move = new CheckerMoveAction(this, savedX, savedY);
        game.sendAction(move);
        isPromotion = false;
        undisplay();
    }

    public void promptForPromotion(int i, int j) {
        isPromotion = true;
        savedX = i;
        savedY = j;
        MessageBox.popUpMessage("You have promoted a pawn to king!", myActivity);
        display();
    }


    public boolean validPawnMove(int row, int col, Pieces currPiece) {
        if(currPiece.getY() > col + 1){
            return false;
        }
        if (currPiece.getX() != row && currPiece.getX() != row - 1 && currPiece.getX() != row + 1) {
            return false;
        }
        if (currPiece.getX() == row) {
            if (state.getPiece(row, col).getColors()  != Pieces.Colors.EMPTY) {
                return false;
            }
        }

        if (currPiece.getX() == row + 1 || currPiece.getX() == row - 1) {
            if (state.getPiece(row, col).getColors() == Pieces.Colors.EMPTY) {
                return false;
            }
        }
        return true;
    }

}
