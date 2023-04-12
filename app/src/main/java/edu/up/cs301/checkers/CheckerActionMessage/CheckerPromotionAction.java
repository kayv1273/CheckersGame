package edu.up.cs301.checkers.CheckerActionMessage;

import edu.up.cs301.checkers.InfoMessage.Pieces;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

public class CheckerPromotionAction extends GameAction {

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    private Pieces promotionPiece;
    private int row;
    private int col;
    public static boolean isPromotion;

    public CheckerPromotionAction(GamePlayer player, Pieces promotionPiece, int row, int col) {
        super(player);
        this.promotionPiece = promotionPiece;
        this.row = row;
        this.col = col;
        isPromotion = false;
    }

    public int getRow(){return this.row;}
    public int getCol(){return this.col;}
    public Pieces getPromotionPiece(){return this.promotionPiece;}
}
