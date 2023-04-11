package edu.up.cs301.checkers.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;

import edu.up.cs301.checkers.Pieces;
import edu.up.cs301.game.R;

/**
 * @author Griselda
 * @author Katherine
 * @author Ruth
 * @author Nick
 * @author Ethan
 * @version 4.6.2023
 */

public class CheckerView extends SurfaceView implements View.OnTouchListener {
    //paint variables
    protected Paint imagePaint;
    private Paint squareColor;
    private Paint highLight;
    private Paint dotPaint;

    //variables for creating board with dimensions
    private float top;
    private float left;
    private float bottom;
    private float right;
    private float size;

    //variables for piece images
    protected Bitmap blackPawn;
    protected Bitmap redPawn;
    protected Bitmap blackKing;
    protected Bitmap redKing;

    //Variables to manipulate board
    private Pieces[][] pieces;
    private int[][] board;

    //Arraylists to store possible moves
    private ArrayList<Integer> xMoves = new ArrayList<>();
    private ArrayList<Integer> yMoves = new ArrayList<>();
    //Arraylists to store AI's possible moves
    private ArrayList<Integer> aixMoves = new ArrayList<>();
    private ArrayList<Integer> aiyMoves = new ArrayList<>();
    //Arraylists to store current AI Position
    private ArrayList<Integer> AIX = new ArrayList<>();
    private ArrayList<Integer> AIY = new ArrayList<>();

    //dimensions
    private int row = 7;
    private int col = 7;

    public CheckerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        pieces = new Pieces[8][8];
        board = new int[8][8];

        size = 115;
        left = top = 40;
        right = left + size;
        bottom = top + size;

        squareColor = new Paint();
        squareColor.setColor(Color.BLACK);
        highLight = new Paint();
        highLight.setColor(Color.YELLOW);
        dotPaint = new Paint();
        dotPaint.setColor(Color.GREEN);

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

        placePieces();
        setBackgroundColor(Color.LTGRAY);
    }


    public void placePieces() {
        for (int row = 0; row < pieces.length; row++) {
            for (int col = 0; col < pieces[row].length; col++) {
                //fill first and third row with black pieces
                if (row == 0 || row == 2) {
                    if (col % 2 == 0) {
                        pieces[row][col] = new Pieces(0, Pieces.Colors.BLACK, row, col);
                    }

                    //fill rest of first and third row with empty pieces
                    else {
                        pieces[row][col] = new Pieces(0, Pieces.Colors.EMPTY, row, col);
                    }
                }
                //fill second row with black pieces
                else if (row == 1) {
                    if (col % 2 != 0) {
                        pieces[row][col] = new Pieces(0, Pieces.Colors.BLACK, row, col);
                    }
                    //fill rest of second row with empty pieces
                    else {
                        pieces[row][col] = new Pieces(0, Pieces.Colors.EMPTY, row, col);
                    }
                }
                //fill sixth and eighth row with red pieces
                else if (row == 5 || row == 7) {
                    if (col % 2 != 0) {
                        pieces[row][col] = new Pieces(0, Pieces.Colors.RED, row, col);
                    }
                    //fill rest of sixth and eighth row with empty pieces
                    else {
                        pieces[row][col] = new Pieces(0, Pieces.Colors.EMPTY, row, col);
                    }
                }
                //fill seventh row with red pieces
                else if (row == 6) {
                    if (col % 2 == 0) {
                        pieces[row][col] = new Pieces(0, Pieces.Colors.RED, row, col);
                    }
                    //fill rest of seventh row with empty pieces
                    else {
                        pieces[row][col] = new Pieces(0, Pieces.Colors.EMPTY, row, col);
                    }
                }
                //fill rest of board with empty pieces
                else {
                    pieces[row][col] = new Pieces(0, Pieces.Colors.EMPTY, row, col);
                }
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //board initialization
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
        //draw all the pieces
        for (int row = 0; row < pieces.length; row++) {
            for (int col = 0; col < pieces[row].length; col++) {

                //draw black pawns
                if (pieces[row][col].getType() == 0 && pieces[row][col].getColors() == Pieces.Colors.BLACK) {
                    canvas.drawBitmap(blackPawn, 40 + (col * 115), 40 + (row * 115), imagePaint);
                }

                //draw black kings
                if (pieces[row][col].getType() == 1 && pieces[row][col].getColors() == Pieces.Colors.BLACK) {
                    canvas.drawBitmap(blackKing, 40 + (col * 115), 40 + (row * 115), imagePaint);
                }

                //draw red pawns
                if (pieces[row][col].getType() == 0 && pieces[row][col].getColors() == Pieces.Colors.RED) {
                    canvas.drawBitmap(redPawn, 40 + (col * 115), 40 + (row * 115), imagePaint);
                }

                //draw red kings
                if (pieces[row][col].getType() == 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {
                    canvas.drawBitmap(redKing, 40 + (col * 115), 40 + (row * 115), imagePaint);
                }
            }
        }
    }

    /**
     * Method to determine what kind of move a piece should make based on its status
     */
    public void movePiece() {
        if (pieces[row][col].getType() == 0) {
            movePawn();
        } else {
            moveKing();
        }
    }

    public void RandomMove() {
        // Fill-in to make sure Arrays are never empty
        AIX.add(1);
        AIY.add(1);
        aixMoves.add(1);
        aiyMoves.add(1);
        for (int x = 0; x < board.length - 1; x++) {
            for (int y = 0; y < board[x].length - 1; y++) {
                //check pawn on left most side of the board
                if (y == 0) {
                    if (pieces[x + 1][y + 1].getColors() == Pieces.Colors.EMPTY && pieces[x][y].getColors() == Pieces.Colors.BLACK) {
                        AIX.add(0, x);
                        AIY.add(0, y);
                        aixMoves.add(0, x + 1);
                        aiyMoves.add(0, y + 1);
                    }
                }
                //check pawn on right most side of the board
                else if (y == 7) {
                    if (pieces[x + 1][y - 1].getColors() == Pieces.Colors.EMPTY && pieces[x][y].getColors() == Pieces.Colors.BLACK) {
                        AIX.add(0, x);
                        AIY.add(0, y);
                        aixMoves.add(0, x + 1);
                        aiyMoves.add(0, y - 1);
                    }
                }
                //pawn is not on the border of the board
                else {
                    if (pieces[x + 1][y - 1].getColors() == Pieces.Colors.EMPTY && pieces[x][y].getColors() == Pieces.Colors.BLACK) {
                        AIX.add(0, x);
                        AIY.add(0, y);
                        aixMoves.add(0, x + 1);
                        aiyMoves.add(0, y - 1);
                    }
                    if (pieces[x + 1][y + 1].getColors() == Pieces.Colors.EMPTY && pieces[x][y].getColors() == Pieces.Colors.BLACK) {
                        AIX.add(0, x);
                        AIY.add(0, y);
                        aixMoves.add(0, x + 1);
                        aiyMoves.add(0, y + 1);
                    }
                }
            }
        }
        // Get random index from Arraylists, move the piece, and replace with empty
        int randomIndex = (int) (Math.random() * AIX.size() - 1);
        int randomX = AIX.get(randomIndex);
        int randomY = AIY.get(randomIndex);
        int newRow = aixMoves.get(randomIndex);
        int newCol = aiyMoves.get(randomIndex);

        pieces[newRow][newCol] = pieces[randomX][randomY];
        pieces[randomX][randomY] = new Pieces(0, Pieces.Colors.EMPTY, randomX, randomY);
        board[randomX][randomY] = 0;
        // Back-up code for out of bounds error
        /**
         * if (AIX.size() != 0 || AIY.size() != 0) {
         *             int randomIndex = (int) Math.floor(Math.random() * AIX.size());
         *             int randomX = AIX.get(randomIndex);
         *             int randomY = AIY.get(randomIndex);
         *             int newRow = aixMoves.get(randomIndex);
         *             int newCol = aiyMoves.get(randomIndex);
         *
         *             pieces[newRow][newCol] = pieces[randomX][randomY];
         *             pieces[randomX][randomY] = new Pieces(0, Pieces.Colors.EMPTY, randomX, randomY);
         *             board[randomX][randomY] = 0;
         * }
         */

        for (int k = 0; k < aixMoves.size(); k++) {
            board[aiyMoves.get(k)][aixMoves.get(k)] = 0;
        }
    }

    /**
     * Method to move a regular pawn
     */
    public void movePawn() {
        //check pawn on left most side of the board
        if (col == 0 && row > 0) {
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.EMPTY && pieces[row][col].getColors() == Pieces.Colors.RED) {
                xMoves.add(row - 1);
                yMoves.add(col + 1);
            }

            //check if left column can capture a piece
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col + 2].getColors() == Pieces.Colors.EMPTY && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {
                xMoves.add(row - 2);
                yMoves.add(col + 2);
                pieces[row - 1][col + 1].setColor(Pieces.Colors.EMPTY);
            }
        }

        //check pawn on right most side of the board
        else if (col == 7 && row > 0 && pieces[row][col].getColors() == Pieces.Colors.RED) {
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col - 1);
            }

            //check if right column can capture a piece
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col - 2].getColors() == Pieces.Colors.EMPTY && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {
                xMoves.add(row - 2);
                yMoves.add(col - 2);
                pieces[row - 1][col - 1].setColor(Pieces.Colors.EMPTY);
            }
        }

        //check captures for column 1
        else if (col == 1 && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col + 2].getColors() == Pieces.Colors.EMPTY && row > 1) {
                xMoves.add(row - 2);
                yMoves.add(col + 2);
                pieces[row - 1][col + 1].setColor(Pieces.Colors.EMPTY);
            }
        }


        //pawn is not on the border of the board
        else if (row > 0 && pieces[row][col].getColors() == Pieces.Colors.RED) {
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col - 1);
            }
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col + 1);
            }
        }

        //check captures for column 6
        else if (col == 6 && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col - 2].getColors() == Pieces.Colors.EMPTY && row > 1) {
                xMoves.add(row - 2);
                yMoves.add(col - 2);
                pieces[row - 1][col - 1].setColor(Pieces.Colors.EMPTY);
            }
        }

        //check capture for columns 2-5
        else if ((col > 1 && col < 6) && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {

            //check diagonal left
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col - 2].getColors() == Pieces.Colors.EMPTY && row > 1) {
                xMoves.add(row - 2);
                yMoves.add(col - 2);
                pieces[row - 1][col - 1].setColor(Pieces.Colors.EMPTY);

                //check diagonal right
            }
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col + 2].getColors() == Pieces.Colors.EMPTY && row > 1) {
                xMoves.add(row - 2);
                yMoves.add(col + 2);
                pieces[row - 1][col + 1].setColor(Pieces.Colors.EMPTY);
            }
        }
    }

    //movement for kings
    public void moveKing() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {

        //check if screen was pressed
        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {

            //iterate through board
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {

                    //check if clicked area is within the bounds of the board
                    if (motionEvent.getX() > 20 + (i * 115) && motionEvent.getX() < 175 + (i * 115)) {
                        if (motionEvent.getY() > 20 + (j * 115) && motionEvent.getY() < 175 + (j * 115)) {

                            //move the piece and set old space to an empty piece
                            for (int index = 0; index < xMoves.size(); index++) {
                                if (xMoves.get(index) == j && yMoves.get(index) == i) {
                                    pieces[j][i] = pieces[row][col];
                                    pieces[row][col] = new Pieces(Pieces.Colors.EMPTY);
                                    board[row][col] = 0;
                                    for (int k = 0; k < xMoves.size(); k++) {
                                        board[yMoves.get(k)][xMoves.get(k)] = 0;
                                    }

                                    //clear possible moves arrayList and redraw board
                                    xMoves.clear();
                                    yMoves.clear();
                                    invalidate();

                                    //ai moves after player moves
                                    RandomMove();
                                    AIX.clear();
                                    AIY.clear();
                                    aixMoves.clear();
                                    aiyMoves.clear();
                                    invalidate();
                                    return true;
                                }
                            }

                            //reset after every touch
                            if (row != 7 && col != 7) {
                                board[row][col] = 0;
                                for (int index = 0; index < xMoves.size(); index++) {
                                    board[xMoves.get(index)][yMoves.get(index)] = 0;
                                }
                                row = 7;
                                col = 7;
                                xMoves.clear();
                                yMoves.clear();
                                invalidate();
                                if (pieces[j][i].getColors() == Pieces.Colors.BLACK || pieces[i][j].getColors() == Pieces.Colors.EMPTY) {
                                    return true;
                                }
                            }
                            if (pieces[j][i].getColors() == Pieces.Colors.BLACK || pieces[i][j].getColors() == Pieces.Colors.EMPTY) {
                                return true;
                            }
                            row = j;
                            col = i;
                            board[row][col] = 1;

                            //movePiece to re-update possible moves
                            movePiece();

                            for (int index = 0; index < xMoves.size(); index++) {
                                board[xMoves.get(index)][yMoves.get(index)] = 2;
                            }
                            invalidate();
                        }
                    }
                }
            }
        }
        return false;
    }


}
