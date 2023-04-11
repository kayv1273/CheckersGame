package edu.up.cs301.checkers;

import java.io.Serializable;
import java.util.ArrayList;

import edu.up.cs301.checkers.Views.Pieces;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;

public class CheckerState extends GameState implements Serializable {
    //Tag for logging
    private static final String TAG = "CheckerState";
    private static final long serialVersionUID = 7552321013488624386L;


    // array that holds the pieces of the board
    private Pieces[][] pieces;
    private int[][] board;

    // an int that tells whose move it is
    // 0: player - red
    // 1: AI - black
    private int playerToMove;

    // boolean to see if game is over
    private boolean isGameOver;

    // ArrayList for all the captured pieces
    private ArrayList<Pieces> redCapturedPieces;
    private ArrayList<Pieces> blackCapturedPieces;

    private boolean canMove;
    public Pieces emptyPiece;

    private ArrayList<Integer> newXMoves;
    private ArrayList<Integer> newYMoves;

    /**
     * Constructor for objects of class TTTState
     */
    public CheckerState() {
        // initialize the state to be a brand new game
        pieces = new Pieces[8][8];
        board = new int[8][8];
        redCapturedPieces = new ArrayList<>();
        blackCapturedPieces = new ArrayList<>();
        canMove = false;
        isGameOver = false;

        newXMoves = new ArrayList<>();
        newYMoves = new ArrayList<>();

        // Set initial positions of all the pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
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

        emptyPiece = new Pieces(0, Pieces.Colors.EMPTY, 0,0);
        // make it player 0's move
        playerToMove = 0;
    }// constructor

    /**
     * Copy constructor for class CheckerState
     *
     * @param original
     * 		the CheckerState object that we want to clong
     */
    public CheckerState(CheckerState original)
    {
        // create a new 8x8 array, and copy the values from
        // the original
        pieces = new Pieces[8][8];
        board = new int[8][8];
        canMove = original.canMove;
        isGameOver = original.isGameOver;

        //copy captured pieces
        redCapturedPieces = new ArrayList<>();
        blackCapturedPieces = new ArrayList<>();
        for (int i = 0; i < redCapturedPieces.size(); i++) {
            redCapturedPieces.add(original.redCapturedPieces.get(i));
        }
        for (int i = 0; i < blackCapturedPieces.size(); i++) {
            blackCapturedPieces.add(original.blackCapturedPieces.get(i));
        }

        //copy pieces
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                int tempType = original.pieces[i][j].getType();
                Pieces.Colors tempColor = original.pieces[i][j].getColors();
                int tempX = original.pieces[i][j].getX();
                int tempY = original.pieces[i][j].getY();
                pieces[i][j] = new Pieces(tempType, tempColor, tempX, tempY);
            }
        }

        //copy what needs to be drawn
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++){
                board[i][j] = original.board[i][j];
            }
        }

        //copy movements
        newXMoves = new ArrayList<>();
        newYMoves = new ArrayList<>();
        for (int i = 0; i < newXMoves.size(); i++) {
            newXMoves.add(original.newXMoves.get(i));
        }
        for (int i = 0; i < newYMoves.size(); i++) {
            newYMoves.add(original.newYMoves.get(i));
        }

        //copy empty piece
        int emptyType = original.emptyPiece.getType();
        Pieces.Colors emptyColor = original.emptyPiece.getColors();
        int emptyX = original.emptyPiece.getX();
        int emptyY = original.emptyPiece.getY();
        emptyPiece = new Pieces(emptyType, emptyColor, emptyX, emptyY);

        // copy the player-to-move information
        playerToMove = original.playerToMove;

    }

    /**
     * Find out which piece is on a square
     *
     * @param row
     *		the row being queried
     * @param col
     * 		the column being queried
     * @return
     * 		the piece at the given square; ' ' if no piece there;
     * 		'?' if it is an illegal square
     */
    public Pieces getPiece(int row, int col) {

        // return the character that is in the proper position
        return pieces[row][col];
    }

    /**
     * Sets a piece on a square
     *
     * @param row
     * 		the row being queried
     * @param
     * 		col the column being queried
     * @param
     * 		piece the piece to place
     */
    public void setPiece(int row, int col, Pieces piece) {
        // if we're out of bounds or anything, return;
        if (pieces == null || row < 0 || col < 0) return;
        if (row >= pieces.length || col >= pieces[row].length) return;

        // return the character that is in the proper position
        pieces[row][col] = piece;
    }

    /**
     * Tells whose move it is.
     *
     * @return the index (0 or 1) of the player whose move it is.
     */
    public int getWhoseMove() {
        return playerToMove;
    }

    /**
     * set whose move it is
     * @param id
     * 		the player we want to set as to whose move it is
     */
    public void setWhoseMove(int id) {
        playerToMove = id;
    }

    public boolean equals(Object object){
        if(! (object instanceof CheckerState)) return false;
        CheckerState checkerState = (CheckerState) object;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(this.pieces[i][j] != checkerState.pieces[i][j]){
                    return false;
                }
            }
        }

        if (this.playerToMove != checkerState.playerToMove || this.numSetupTurns != checkerState.numSetupTurns || this.currentSetupTurn != checkerState.currentSetupTurn){
            return false;
        }
        return true;
    }
}
