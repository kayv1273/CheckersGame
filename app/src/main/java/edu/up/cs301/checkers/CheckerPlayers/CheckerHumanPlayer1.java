package edu.up.cs301.checkers.CheckerPlayers;

import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.up.cs301.checkers.CheckerState;
import edu.up.cs301.checkers.Views.CheckerView;
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
    private static final String TAG = "CheckersHumanPlayer1";

    // the surface view
    private CheckerView surfaceView;
    private CheckerView checkerBoardView;
    private Button resetButton;

    //names
    private TextView player1name;
    public boolean promotion;
    private int savedX = 0;
    private int savedY = 0;

    // the ID for the layout to use
    private int layoutId;

    private CheckerState state;
    private boolean start;
    private int x = 8;
    private int y = 8;

    /**
     * constructor
     *
     * @param name
     * 		the player's name
     * @param layoutId
     *      the id of the layout to use
     */
    public CheckerHumanPlayer1(String name, int layoutId, CheckerState state) {
        super(name);
        this.layoutId = layoutId;
        start = true;
        promotion = false;
        this.state = state;
    }

    /**
     * Callback method, called when player gets a message
     *
     * @param info
     * 		the message
     */
    @Override
    public void receiveInfo(GameInfo info) {

        if (checkerBoardView == null) return;

        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            // if the move was out of turn or otherwise illegal, flash the screen
            checkerBoardView.flash(Color.RED, 50);
        }
        else if (!(info instanceof CheckerState))
            // if we do not have a TTTState, ignore
            return;
        else {
            checkerBoardView.setState((CheckerState)info);
            checkerBoardView.invalidate();
            Logger.log(TAG, "receiving");
        }
    }

    /**
     * sets the current player as the activity's GUI
     */
    public void setAsGui(GameMainActivity activity) {

        // Load the layout resource for the new configuration
        activity.setContentView(layoutId);

        // set the surfaceView instance variable
        surfaceView = (CheckerView)myActivity.findViewById(R.id.checkerBoardView);
        Logger.log("set listener","OnTouch");
        surfaceView.setOnTouchListener(this);

        //player name
        player1name = myActivity.findViewById(R.id.name);

        //reset button
        resetButton = myActivity.findViewById(R.id.reset);
        resetButton.setOnTouchListener(this);

    }

    /**
     * returns the GUI's top view
     *
     * @return
     * 		the GUI's top view
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
        myActivity.setTitle("Checkers: "+allPlayerNames[0]+" vs. "+allPlayerNames[1]);
    }

    /**
     * callback method when the screen it touched. We're
     * looking for a screen touch (which we'll detect on
     * the "up" movement" onto a tic-tac-tie square
     *
     * @param event
     * 		the motion event that was detected
     */
    public boolean onTouch(View v, MotionEvent event) {
        // ignore if not an "up" event
        if (event.getAction() != MotionEvent.ACTION_UP) return true;
        // get the x and y coordinates of the touch-location;
        // convert them to square coordinates (where both
        // values are in the range 0..2)
        int x = (int) event.getX();
        int y = (int) event.getY();
        Point p = surfaceView.mapPixelToSquare(x, y);

        // if the location did not map to a legal square, flash
        // the screen; otherwise, create and send an action to
        // the game
        if (p == null) {
            surfaceView.flash(Color.RED, 50);
        } else {
            TTTMoveAction action = new TTTMoveAction(this, p.y, p.x);
            Logger.log("onTouch", "Human player sending TTTMA ...");
            game.sendAction(action);
            surfaceView.invalidate();
        }

        // register that we have handled the event
        return true;

    }
}
