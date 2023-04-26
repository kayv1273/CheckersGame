package edu.up.cs301.checkers.CheckerActionMessage;

import edu.up.cs301.checkers.InfoMessage.Piece;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

public class CheckerPromotionAction extends GameAction {
    private Piece promotionPiece;
    private int row;
    private int col;
    public static boolean isPromotion;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     * @param promotionPiece the piece to be promoted
     * @param row the selected row
     * @param col the selected col
     */

    public CheckerPromotionAction(GamePlayer player, Piece promotionPiece, int row, int col) {
        super(player);
        this.promotionPiece = promotionPiece;
        this.row = row;
        this.col = col;
        isPromotion = false;
    }

    /**
     * get the object's row
     *
     * @return the row selected
     */
    public int getRow(){return this.row;}

    /**
     * get the object's col
     *
     * @return the col selected
     */
    public int getCol(){return this.col;}

    /**
     * get the promoted piece
     *
     * @return the promoted piece
     */
    public Piece getPromotionPiece(){return this.promotionPiece;}
}
