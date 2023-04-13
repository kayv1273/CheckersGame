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

public class RedCaptureSurfaceView extends FlashSurfaceView {

    protected Bitmap blackPawnImage;

    protected Bitmap blackKingImage;

    private int width;
    private int height;

    private int xLoc;
    private int yLoc;

    private Paint paint;

    protected CheckerState chessState;

    public RedCaptureSurfaceView(Context context) {
        super(context);
        init();
    }

    public RedCaptureSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        blackPawnImage = BitmapFactory.decodeResource(getResources(), R.drawable.bp);

        blackKingImage = BitmapFactory.decodeResource(getResources(), R.drawable.bk);

        blackPawnImage = Bitmap.createScaledBitmap(blackPawnImage, width, height, false);

        blackKingImage = Bitmap.createScaledBitmap(blackKingImage, width, height, false);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(chessState == null) return;

        //canvas.drawRect(10,10,30,30, paint);


        xLoc = yLoc = 5;
        for (Piece p : chessState.getWhiteCapturedPieces()) {
//            Log.d("Testing", p.getPieceType().toString());
            if (p.getPieceType() == Piece.PieceType.PAWN) {
                canvas.drawBitmap(blackPawnImage, xLoc, yLoc, paint);
                increment();

            } else if (p.getPieceType() == Piece.PieceType.KING) {
                canvas.drawBitmap(blackKingImage, xLoc, yLoc, paint);
                increment();
            }
        }

    }

    private void init(){
        width = height = 75;
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    public void setState(CheckerState state) {
        chessState = state;
    }

    public void increment(){
        xLoc += width;
        if(xLoc > width*11){
            xLoc = 5;
            yLoc = 5 + height;
        }
    }

}
