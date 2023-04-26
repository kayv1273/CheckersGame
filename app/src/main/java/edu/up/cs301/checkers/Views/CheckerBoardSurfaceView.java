package edu.up.cs301.checkers.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.InfoMessage.Piece;
import edu.up.cs301.game.GameFramework.utilities.FlashSurfaceView;
import edu.up.cs301.game.R;

/**
 * @author Griselda
 * @author Katherine
 * @author Ruth
 * @author Nick
 * @author Ethan
 * @version 4.13.2023
 */

public class CheckerBoardSurfaceView extends FlashSurfaceView {

    //variables for creating board
    private float top = 40;
    private float left = 40;
    private float size = 120;
    private float bottom = top + size;
    private float right = left + size;

    protected CheckerState state;

    //images for chess pieces
    protected Bitmap redPawnImage;
    protected Bitmap redKingImage;
    protected Bitmap blackPawnImage;
    protected Bitmap blackKingImage;

    public CheckerBoardSurfaceView(Context context) {
        super(context);
        init();
    }

    public CheckerBoardSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        redPawnImage = BitmapFactory.decodeResource(getResources(), R.drawable.rp);

        redKingImage = BitmapFactory.decodeResource(getResources(), R.drawable.rk);

        blackPawnImage = BitmapFactory.decodeResource(getResources(), R.drawable.bp);

        blackKingImage = BitmapFactory.decodeResource(getResources(), R.drawable.bk);

        redPawnImage = Bitmap.createScaledBitmap(redPawnImage, 120, 120, false);

        redKingImage = Bitmap.createScaledBitmap(redKingImage, 120, 120, false);

        blackPawnImage = Bitmap.createScaledBitmap(blackPawnImage, 120, 120, false);

        blackKingImage = Bitmap.createScaledBitmap(blackKingImage, 120, 120, false);

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

        if (state == null) return;

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

    protected void drawPiece(Canvas canvas, Piece piece, int col, int row) {
        float xLoc = left + (col * size);
        float yLoc = top + (row * size);

        Paint paint = new Paint();
        if (piece.getPieceColor() == Piece.ColorType.RED) {
            if (piece.getPieceType() == Piece.PieceType.PAWN) {
                canvas.drawBitmap(redPawnImage, xLoc, yLoc, paint);
            } else if (piece.getPieceType() == Piece.PieceType.KING) {
                canvas.drawBitmap(redKingImage, xLoc, yLoc, paint);
            }
        } else if (piece.getPieceColor() == Piece.ColorType.BLACK) {
            if (piece.getPieceType() == Piece.PieceType.PAWN) {
                canvas.drawBitmap(blackPawnImage, xLoc, yLoc, paint);
            } else if (piece.getPieceType() == Piece.PieceType.KING) {
                canvas.drawBitmap(blackKingImage, xLoc, yLoc, paint);
            }
        }
    }
}