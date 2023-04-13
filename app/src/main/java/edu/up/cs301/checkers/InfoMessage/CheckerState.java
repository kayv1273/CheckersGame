package edu.up.cs301.checkers.InfoMessage;

import java.io.Serializable;
import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.infoMessage.GameState;

//info
public class CheckerState extends GameState implements Serializable {
    private Piece[][] pieces; // An array that holds all of the pieces and their position
    private int[][] board; // An array that determines what kind of drawing should be made
    private int turnCount; //holds num of turns

    private boolean isGameOver; //boolean that holds if the game is over (checkmate)

    private ArrayList<Piece> redCapturedPieces;
    private ArrayList<Piece> blackCapturedPieces;

    private boolean canMove;

    public Piece emptyPiece;

    //0: red
    //1: black
    private int playerToMove;

    private ArrayList<Integer> newMovementsX;
    private ArrayList<Integer> newMovementsY;

    public CheckerState() {
        pieces = new Piece[8][8];
        board = new int[8][8];
        redCapturedPieces = new ArrayList<>();
        blackCapturedPieces = new ArrayList<>();
        canMove = false;
        isGameOver = false;

        newMovementsX = new ArrayList<>();
        newMovementsY = new ArrayList<>();

        // Setting the initial position of all of the pieces
        for (int row = 0; row < pieces.length; row++) {
            for (int col = 0; col < pieces[row].length; col++) {
                if (col == 0 || col == 2) {
                    if (row % 2 == 0) {
                        pieces[row][col] = new Piece(Piece.PieceType.PAWN, Piece.ColorType.BLACK, row, col);
                    }

                    //fill rest of first and third row with empty pieces
                    else {
                        pieces[row][col] = new Piece(Piece.PieceType.PAWN, Piece.ColorType.EMPTY, row, col);
                    }
                }
                //fill second row with black pieces
                else if (col == 1) {
                    if (row % 2 != 0) {
                        pieces[row][col] = new Piece(Piece.PieceType.PAWN, Piece.ColorType.BLACK, row, col);
                    }
                    //fill rest of second row with empty pieces
                    else {
                        pieces[row][col] = new Piece(Piece.PieceType.PAWN, Piece.ColorType.EMPTY, row, col);
                    }
                }
                //fill sixth and eighth row with red pieces
                else if (col == 5 || col == 7) {
                    if (row % 2 != 0) {
                        pieces[row][col] = new Piece(Piece.PieceType.PAWN, Piece.ColorType.RED, row, col);
                    }
                    //fill rest of sixth and eighth row with empty pieces
                    else {
                        pieces[row][col] = new Piece(Piece.PieceType.PAWN, Piece.ColorType.EMPTY, row, col);
                    }
                }
                //fill seventh row with red pieces
                else if (col == 6) {
                    if (row % 2 == 0) {
                        pieces[row][col] = new Piece(Piece.PieceType.PAWN, Piece.ColorType.RED, row, col);
                    }
                    //fill rest of seventh row with empty pieces
                    else {
                        pieces[row][col] = new Piece(Piece.PieceType.PAWN, Piece.ColorType.EMPTY, row, col);
                    }
                }
                //fill rest of board with empty pieces
                else {
                    pieces[row][col] = new Piece(Piece.PieceType.PAWN, Piece.ColorType.EMPTY, row, col);
                }
            }
        }

        emptyPiece = new Piece(Piece.PieceType.EMPTY, Piece.ColorType.EMPTY, 0, 0);

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = 0;
            }
        }
        playerToMove = 0;

    }

    // Copy Constructor
    public CheckerState(CheckerState other) {
        pieces = new Piece[8][8];
        board = new int[8][8];
        canMove = other.canMove;
        isGameOver = other.isGameOver;

        //copy captured pieces
        redCapturedPieces = new ArrayList<>();
        blackCapturedPieces = new ArrayList<>();
        for (int i = 0; i < redCapturedPieces.size(); i++) {
            redCapturedPieces.add(other.redCapturedPieces.get(i));
        }
        for (int i = 0; i < blackCapturedPieces.size(); i++) {
            blackCapturedPieces.add(other.blackCapturedPieces.get(i));
        }

        //copies pieces into copy
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                Piece.PieceType tempPieceType = other.pieces[i][j].getPieceType();
                Piece.ColorType tempColorType = other.pieces[i][j].getPieceColor();
                int tempX = other.pieces[i][j].getX();
                int tempY = other.pieces[i][j].getY();
                pieces[i][j] = new Piece(tempPieceType, tempColorType, tempX, tempY);
            }
        }

        //copies what needs to be drawn on the board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = other.board[i][j];
            }
        }


        Piece.PieceType emptyTempPieceType = other.emptyPiece.getPieceType();
        Piece.ColorType emptyTempColorType = other.emptyPiece.getPieceColor();
        int emptyTempX = other.emptyPiece.getX();
        int emptyTempY = other.emptyPiece.getY();
        emptyPiece = new Piece(emptyTempPieceType, emptyTempColorType, emptyTempX, emptyTempY);

        playerToMove = other.playerToMove;


        //copy movements
        newMovementsX = new ArrayList<>();
        newMovementsY = new ArrayList<>();
        for (int i = 0; i < newMovementsX.size(); i++) {
            newMovementsX.add(other.newMovementsX.get(i));
        }
        for (int i = 0; i < newMovementsY.size(); i++) {
            newMovementsY.add(other.newMovementsY.get(i));
        }
    }

    public Piece getPiece(int row, int col) {
        return pieces[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        piece.setY(col);
        piece.setX(row);
        pieces[row][col] = piece;
    }

    public void setCanMove(boolean b) {
        canMove = b;
    }

    public boolean getCanMove() {
        return canMove;
    }

    public void setGameOver(boolean b) {
        isGameOver = b;
    }

    public boolean getGameOver() {
        return isGameOver;
    }

    public void setNewMovementsX(ArrayList<Integer> newMovementsX) {
        this.newMovementsX = newMovementsX;
    }

    public ArrayList<Integer> getNewMovementsX() {
        return newMovementsX;
    }

    public void setNewMovementsY(ArrayList<Integer> newMovementsY) {
        this.newMovementsY = newMovementsY;
    }

    public ArrayList<Integer> getNewMovementsY() {
        return newMovementsY;
    }


    public void setHighlightCheck(int row, int col) {
        board[row][col] = 3;
    }

    public void setHighlight(int row, int col) {
        board[row][col] = 1;
    }

    //iterate through x and y value arrays to find where to put circles on the board
    public void setCircles(ArrayList<Integer> row, ArrayList<Integer> col) {
        for (int i = 0; i < row.size(); i++) {
            if (getPiece(row.get(i), col.get(i)).getPieceColor() != Piece.ColorType.EMPTY) {
                board[row.get(i)][col.get(i)] = 4;
            } else {
                board[row.get(i)][col.get(i)] = 2;
            }
        }
    }

    //removes all highlights from the board
    public void removeHighlight() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 1) {
                    board[i][j] = 0;
                }
            }
        }
    }

    //removes all circles from the board
    public void removeCircle() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 2 || board[i][j] == 4) {
                    board[i][j] = 0;
                }
            }
        }
    }

    //removes the check highlight from the board
    public void removeHighlightCheck() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 3) {
                    board[i][j] = 0;
                }
            }
        }
    }

    //returns what is on a square on the board
    public int getDrawing(int row, int col) {
        return board[row][col];
    }

    public int getWhoseMove() {
        return playerToMove;
    }

    public void setWhoseMove(int id) {
        playerToMove = id;
    }

    public ArrayList<Piece> getRedCapturedPieces() {
        return this.redCapturedPieces;
    }

    public ArrayList<Piece> getBlackCapturedPieces() {
        return this.blackCapturedPieces;
    }

    public void addRedCapturedPiece(Piece p) {
        redCapturedPieces.add(p);
    }

    public void addBlackCapturedPiece(Piece p) {
        blackCapturedPieces.add(p);
    }


    //getters for the hasMoved variables
}
