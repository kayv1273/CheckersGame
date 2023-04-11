package edu.up.cs301.checkers.CheckerActionMessage;

import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

public class CheckerMoveAction extends GameAction {

    // instance variables: the selected row and column
    private int row;
    private int col;

    /**
     * Constructor for CheckerMoveAction
     *
     //@param source the player making the move
     * @param col the row of the square selected
     * @param row the column of the square selected
     */
    public CheckerMoveAction(GamePlayer player, int row, int col) {
        // invoke superclass constructor to set the player
        super(player);

        // set the row and column as passed to us
        this.row = row;
        this.col = col;
    }

    /**
     * get the object's row
     *
     * @return the row selected
     */
    public int getRow() { return row; }

    /**
     * get the object's column
     *
     * @return the column selected
     */
    public int getCol() { return col; }
}
