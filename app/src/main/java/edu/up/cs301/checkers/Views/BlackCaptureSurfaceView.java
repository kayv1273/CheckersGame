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
/*
public class BlackCaptureSurfaceView extends FlashSurfaceView {
    protected Bitmap redPawnImage;
    private int width;
    private int height;
    private int xLoc;
    private int yLoc;
    private Paint paint;
    protected CheckerState checkerState;

    public BlackCaptureSurfaceView(Context context) {
        super(context);
        init();
    }

    public BlackCaptureSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        redPawnImage = BitmapFactory.decodeResource(getResources(), R.drawable.rp);
        redPawnImage = Bitmap.createScaledBitmap(redPawnImage, width, height, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (checkerState == null) return;

        //canvas.drawRect(10,10,30,30, paint);


        xLoc = yLoc = 5;
        for (Piece p : checkerState.getBlackCapturedPieces()) {
//            Log.d("Testing", p.getPieceType().toString());
            if (p.getPieceType() == Piece.PieceType.PAWN) {
                canvas.drawBitmap(redPawnImage, xLoc, yLoc, paint);
            }

        }
    }

    private void init(){
        width = height = 75;
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    public void setState(CheckerState state) {
        checkerState = state;
    }
}
*/