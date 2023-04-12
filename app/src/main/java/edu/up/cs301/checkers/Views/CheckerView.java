package edu.up.cs301.checkers.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.InfoMessage.Pieces;
import edu.up.cs301.game.GameFramework.utilities.FlashSurfaceView;
import edu.up.cs301.game.R;

/**
 * @author Griselda
 * @author Katherine
 * @author Ruth
 * @author Nick
 * @author Ethan
 * @version 4.11.2023
 */

public class CheckerView extends FlashSurfaceView {

    //variables for creating board with dimensions
    private float top = 40;
    private float left = 40;
    private float size = 115;
    private float bottom = top + size;
    private float right = left + size;


    //variables for piece images
    protected Bitmap blackPawn;
    protected Bitmap redPawn;
    protected Bitmap blackKing;
    protected Bitmap redKing;

    protected CheckerState checkerState;

    public CheckerView(Context context) {
        super(context);
        init();
    }

    public CheckerView(Context context, AttributeSet attrs) {
        super(context, attrs);


        //decode resources of piece images
        blackPawn = BitmapFactory.decodeResource(getResources(), R.drawable.bp);
        blackKing = BitmapFactory.decodeResource(getResources(), R.drawable.bk);
        redPawn = BitmapFactory.decodeResource(getResources(), R.drawable.rp);
        redKing = BitmapFactory.decodeResource(getResources(), R.drawable.rk);

        //scale the images of the pieces
        blackPawn = Bitmap.createScaledBitmap(blackPawn, 115, 115, false);
        blackKing = Bitmap.createScaledBitmap(blackKing, 115, 115, false);
        redPawn = Bitmap.createScaledBitmap(redPawn, 115, 115, false);
        redKing = Bitmap.createScaledBitmap(redKing, 115, 115, false);


        init();
    }

    private void init() {
        setBackgroundColor(Color.LTGRAY);
    }

    public void setState(CheckerState state){
        checkerState = state;
    }

    public int blackSquare() {
        return Color.BLACK;
    }

    public int whiteSquare() {
        return Color.WHITE;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //board initialization
        Paint squareColor = new Paint();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                //alternate colors of squares to create checkerboard pattern
                if ((i % 2 == 0 && j % 2 != 0) || (j % 2 == 0 && i % 2 != 0)) {
                    squareColor.setColor(Color.BLACK);
                } else {
                    squareColor.setColor(Color.WHITE);
                }

                //draw rectangle
                canvas.drawRect(left + (right - left) * i, top + (bottom - top) * j, right +
                        (right - left) * i, bottom + (bottom - top) * j, squareColor);
            }
        }

        if (checkerState == null) {
            return;
        }

        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++) {
                drawPiece(canvas, checkerState.getPiece(row,col), row, col);
            }
        }
    }

    protected void drawPiece(Canvas canvas, Pieces piece, int row, int col) {
        float xCoord = left + (col * size);
        float yCoord = top + (row * size);

        Paint paint = new Paint();

        if(piece.getColors() == Pieces.Colors.RED) {
            if (piece.getType() == 1) {
                canvas.drawBitmap(redKing, xCoord, yCoord, paint);
            }
            else {
                canvas.drawBitmap(redPawn, xCoord, yCoord, paint);
            }
        }
        else if (piece.getColors() == Pieces.Colors.BLACK) {
            if (piece.getType() == 1) {
                canvas.drawBitmap(blackKing, xCoord, yCoord, paint);
            }
            else {
                canvas.drawBitmap(blackPawn, xCoord, yCoord, paint);
            }
        }
    }

}
