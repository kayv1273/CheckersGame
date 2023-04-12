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

    protected CheckerState state;

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
        setBackgroundColor(backgroundColor());
    }

    public void setState(CheckerState state) {
        this.state = state;
    }

    public int blackSquare() {
        return Color.BLACK;
    }

    public int whiteSquare() {
        return Color.WHITE;
    }

    public int textPaint() {
        return Color.WHITE;
    }

    public int highlightedSquare() {
        return Color.YELLOW;
    }

    public int checkedSquare() {
        return Color.RED;
    }

    public int dot() {
        return Color.LTGRAY;
    }

    public int backgroundColor() {
        return Color.LTGRAY;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw the board
        Paint paint = new Paint();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i % 2 == 0 && j % 2 != 0) || (j % 2 == 0 && i % 2 != 0)) {
                    paint.setColor(blackSquare());
                } else {
                    paint.setColor(whiteSquare());
                }
                canvas.drawRect(left + (right - left) * i, top + (bottom - top) * j,
                        right + (right - left) * i, bottom + (bottom - top) * j, paint);
            }
        }

        if (state == null) {
            return;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                drawGraphics(canvas, state.getDrawing(row, col), row, col);
                drawPiece(canvas, state.getPiece(row, col), row, col);
            }
        }

    }


    protected void drawGraphics(Canvas canvas, int num, int col, int row) {
        float leftLoc = left + size * col;
        float topLoc = top + size * row;
        float rightLoc = right + size * col;
        float bottomLoc = bottom + size * row;

        float centerX = left + (size / 2) + size * col;
        float centerY = top + (size / 2) + size * row;
        float radius = (right - left) / 5;

        Paint highlightPaint = new Paint();
        if (num == 1) {
            highlightPaint.setColor(highlightedSquare());
        } else if (num == 3) {
            highlightPaint.setColor(checkedSquare());
        }
        Paint dotPaint = new Paint();
        dotPaint.setColor(dot());
        Paint backgroundPaint = new Paint();
        Paint ringPaint = new Paint();
        ringPaint.setColor(Color.RED);

        if (num == 1) {
            canvas.drawRect(leftLoc, topLoc, rightLoc, bottomLoc, highlightPaint);
        } else if (num == 2) {
            canvas.drawCircle(centerX, centerY, radius, dotPaint);
        } else if (num == 3) {
            canvas.drawRect(leftLoc, topLoc, rightLoc, bottomLoc, highlightPaint);
        } else if (num == 4) {
            canvas.drawCircle(centerX, centerY, (right - left) / 2, dotPaint);
            if ((row % 2 == 0 && col % 2 != 0) || (col % 2 == 0 && row % 2 != 0)) {
                backgroundPaint.setColor(Color.rgb(1, 100, 32));
            } else {
                backgroundPaint.setColor(Color.WHITE);
            }
            canvas.drawCircle(centerX, centerY, (right - left) / 3, backgroundPaint);
        }
    }

    protected void drawPiece(Canvas canvas, Pieces piece, int row, int col) {
        float xCoord = left + (row * size);
        float yCoord = top + (col * size);

        Paint paint = new Paint();

        if(piece.getColors() == Pieces.Colors.RED) {
            if (piece.getType() == 1) {
                canvas.drawBitmap(redKing, xCoord, yCoord, paint);
            }
            else if(piece.getType() == 0){
                canvas.drawBitmap(redPawn, xCoord, yCoord, paint);
            }
        }
        else if (piece.getColors() == Pieces.Colors.BLACK) {
            if (piece.getType() == 1) {
                canvas.drawBitmap(blackKing, xCoord, yCoord, paint);
            }
            else if (piece.getType() ==0){
                canvas.drawBitmap(blackPawn, xCoord, yCoord, paint);
            }
        }
    }

}
