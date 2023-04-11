package edu.up.cs301.checkers.CheckerPlayers;

import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.up.cs301.checkers.CheckerState;
import edu.up.cs301.checkers.Views.CheckerView;
import edu.up.cs301.checkers.Views.Pieces;
import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.game.R;
import edu.up.cs301.tictactoe.infoMessage.TTTState;
import edu.up.cs301.tictactoe.tttActionMessage.TTTMoveAction;
import edu.up.cs301.tictactoe.views.TTTSurfaceView;

public class CheckerHumanPlayer1 extends GameHumanPlayer implements View.OnTouchListener{
    //Tag for logging
    private static final String TAG = "CheckerHumanPlayer";

    // the surface view
    private CheckerView surfaceView;
    public TextView movesLog;
    private CheckerView surfaceViewChessBoard;
    private Button resignButton;

    //captures surfaceview
    private BlackCaptureSurfaceView surfaceViewBlackCapture;
    private WhiteCaptureSurfaceView surfaceViewWhiteCapture;

    //names
    private TextView player1name;
    private TextView player2name;

    public Button queenPromo;
    public Button bishopPromo;
    public Button rookPromo;
    public Button knightPromo;
    public boolean isPromotion;
    public Pieces currPiece = new Pieces(Pieces.PieceType.QUEEN, Piece.ColorType.WHITE, 0, 0);
    private int savedX = 0;
    private int savedY = 0;

    // the ID for the layout to use
    private int layoutId;

    private ChessState state;
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
        if (surfaceViewChessBoard == null) {
            return;
        }

        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            // if the move was out of turn or otherwise illegal, flash the screen
            surfaceViewChessBoard.flash(Color.RED, 50);
        } else if (!(info instanceof ChessState)) {
            // if we do not have a state, ignore
            return;
        } else {
            surfaceViewChessBoard.setState((ChessState) info);
            surfaceViewChessBoard.invalidate();

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
        surfaceView = (ChessBoardSurfaceView) myActivity.findViewById(R.id.chessBoard);
        surfaceView.setOnTouchListener(this);

        //moves log
        movesLog = myActivity.findViewById(R.id.movesLog);
        surfaceViewChessBoard = (ChessBoardSurfaceView) myActivity.findViewById(R.id.chessBoard);

        //player names
        player1name = myActivity.findViewById(R.id.nameBlack);
        player2name = myActivity.findViewById(R.id.nameWhite);

        //resignation
        resignButton = myActivity.findViewById(R.id.surrenderButton);

        //captures
        surfaceViewWhiteCapture = (WhiteCaptureSurfaceView) myActivity.findViewById(R.id.whiteCaptures);
        surfaceViewBlackCapture = (BlackCaptureSurfaceView) myActivity.findViewById(R.id.blackCaptures);

        queenPromo = myActivity.findViewById(R.id.queenPromo);
        bishopPromo = myActivity.findViewById(R.id.bishopPromo);
        knightPromo = myActivity.findViewById(R.id.knightPromo);
        rookPromo = myActivity.findViewById(R.id.rookPromo);
        undisplay();
        queenPromo.setOnTouchListener(this);
        bishopPromo.setOnTouchListener(this);
        knightPromo.setOnTouchListener(this);
        rookPromo.setOnTouchListener(this);
        surfaceViewChessBoard.setOnTouchListener(this);
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
        myActivity.setTitle("Chess: " + allPlayerNames[0] + " vs. " + allPlayerNames[1]);
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
        if (view.getId() == resignButton.getId()) {
            MessageBox.popUpMessage("Game Over!\n You have resigned", myActivity);
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
                            if (state.getPiece(i, j).getPieceColor() == Piece.ColorType.WHITE && state.getWhoseMove() == 0) {
                                ChessSelectAction select = new ChessSelectAction(this, i, j);
                                currPiece = state.getPiece(i, j);
                                game.sendAction(select);
                            } else if (state.getPiece(i, j).getPieceColor() == Piece.ColorType.BLACK && state.getWhoseMove() == 1) {
                                ChessSelectAction select = new ChessSelectAction(this, i, j);
                                currPiece = state.getPiece(i, j);
                                game.sendAction(select);
                            } else if (state.getPiece(i, j).getPieceColor() != Piece.ColorType.WHITE && state.getWhoseMove() == 0) {
                                if (j == 0 && currPiece.getPieceType() == Piece.PieceType.PAWN && state.getWhoseMove() == this.playerNum) {
                                    if (!validPawnMove(i, j, currPiece)) {
                                        ChessMoveAction move = new ChessMoveAction(this, i, j);
                                        game.sendAction(move);
                                        break;
                                    }
                                    promptForPromotion(i, j);
                                    break;
                                }
                                ChessMoveAction move = new ChessMoveAction(this, i, j);
                                game.sendAction(move);
                            } else if (state.getPiece(i, j).getPieceColor() != Piece.ColorType.BLACK && state.getWhoseMove() == 1) {
                                if (j == 7 && currPiece.getPieceType() == Piece.PieceType.PAWN && state.getWhoseMove() == this.playerNum) {
                                    if (!validPawnMove(i,j,currPiece)) {
                                        ChessMoveAction move = new ChessMoveAction(this, i, j);
                                        game.sendAction(move);
                                        break;
                                    }
                                    promptForPromotion(i, j);
                                    break;
                                }
                                ChessMoveAction move = new ChessMoveAction(this, i, j);
                                game.sendAction(move);
                            }
                            surfaceViewChessBoard.invalidate();
                            surfaceViewWhiteCapture.invalidate();
                            surfaceViewBlackCapture.invalidate();
                        }
                    }
                }
                if (isPromotion) {
                    break;
                }
            }
        } else {
            if (view.getId() == queenPromo.getId()) {
                makePromotion(Piece.PieceType.QUEEN);
            } else if (view.getId() == bishopPromo.getId()) {
                makePromotion(Piece.PieceType.BISHOP);
            } else if (view.getId() == knightPromo.getId()) {
                makePromotion(Piece.PieceType.KNIGHT);
            } else if (view.getId() == rookPromo.getId()) {
                makePromotion(Piece.PieceType.ROOK);
            }
            return true;
        }

        // register that we have handled the event
        return true;
    }

    public void displayMovesLog(int currRow, int currCol, int tempRow, ChessState state, boolean isCapture) {
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
        } else if (currPiece == Piece.PieceType.QUEEN) {
            toReturn += "Q";
        } else if (currPiece == Piece.PieceType.BISHOP) {
            toReturn += "B";
        } else if (currPiece == Piece.PieceType.KNIGHT) {
            toReturn += "N";
        } else if (currPiece == Piece.PieceType.ROOK) {
            toReturn += "R";
        }
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
        bishopPromo.setVisibility(View.INVISIBLE);
        rookPromo.setVisibility(View.INVISIBLE);
        knightPromo.setVisibility(View.INVISIBLE);
    }

    public void display() {
        queenPromo.setVisibility(View.VISIBLE);
        bishopPromo.setVisibility(View.VISIBLE);
        knightPromo.setVisibility(View.VISIBLE);
        rookPromo.setVisibility(View.VISIBLE);
    }

    public void makePromotion(Piece.PieceType type) {
        Piece.ColorType currColor = state.getWhoseMove() == 0 ? Piece.ColorType.WHITE : Piece.ColorType.BLACK;
        Piece set = new Piece(type, currColor, savedX, savedY);
        ChessPromotionAction promo = new ChessPromotionAction(this,set,savedX,savedY);
        game.sendAction(promo);
        ChessMoveAction move = new ChessMoveAction(this, savedX, savedY);
        game.sendAction(move);
        isPromotion = false;
        undisplay();
    }

    public void promptForPromotion(int i, int j) {
        isPromotion = true;
        savedX = i;
        savedY = j;
        MessageBox.popUpMessage("Pick a promotion piece", myActivity);
        display();
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
