package edu.up.cs301.checkers.InfoMessage;

import java.io.Serializable;
import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.infoMessage.GameState;

//info
public class CheckerState extends GameState implements Serializable {
    private Piece[][] pieces; // An array that holds all of the pieces and their position
    private int[][] board; // An array that determines what kind of drawing should be made
    private int turnCount; //holds num of turns
    private Piece kingBlack; //special piece for black king
    private Piece kingWhite; //special piece for white king

    private boolean isCheck; //boolean that holds if a king is in check
    private boolean isGameOver; //boolean that holds if the game is over (checkmate)

    private ArrayList<Piece> whiteCapturedPieces;
    private ArrayList<Piece> blackCapturedPieces;

    private boolean canMove;

    public Piece emptyPiece;

    //0: white
    //1: black
    private int playerToMove;

    //keeps track of whether certain pieces have moved for castling
    private boolean whiteKingHasMoved;
    private boolean whiteRook1HasMoved;
    private boolean whiteRook2HasMoved;
    private boolean blackKingHasMoved;
    private boolean blackRook1HasMoved;
    private boolean blackRook2HasMoved;

    private boolean kingInCheck;

    private ArrayList<Integer> newMovementsX;
    private ArrayList<Integer> newMovementsY;

    public CheckerState() {
        pieces = new Piece[8][8];
        board = new int[8][8];
        whiteCapturedPieces = new ArrayList<>();
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
        kingWhite = new Piece(Piece.PieceType.KING, Piece.ColorType.RED, 4, 7);
        kingBlack = new Piece(Piece.PieceType.KING, Piece.ColorType.BLACK, 4, 0);
        emptyPiece = new Piece(Piece.PieceType.EMPTY, Piece.ColorType.EMPTY, 0, 0);

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = 0;
            }
        }
        playerToMove = 0;
        turnCount = 0;
        isCheck = false;

        whiteKingHasMoved = false;
        whiteRook1HasMoved = false;
        whiteRook2HasMoved = false;
        blackKingHasMoved = false;
        blackRook1HasMoved = false;
        blackRook2HasMoved = false;
    }

    // Copy Constructor
    public CheckerState(CheckerState other) {
        pieces = new Piece[8][8];
        board = new int[8][8];
        canMove = other.canMove;
        isGameOver = other.isGameOver;

        //copy captured pieces
        whiteCapturedPieces = new ArrayList<>();
        blackCapturedPieces = new ArrayList<>();
        for (int i = 0; i < whiteCapturedPieces.size(); i++) {
            whiteCapturedPieces.add(other.whiteCapturedPieces.get(i));
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

        Piece.PieceType kingWhiteTempPieceType = other.kingWhite.getPieceType();
        Piece.ColorType kingWhiteTempColorType = other.kingWhite.getPieceColor();
        int kingWhiteTempX = other.kingWhite.getX();
        int kingWhiteTempY = other.kingWhite.getY();
        kingWhite = new Piece(kingWhiteTempPieceType, kingWhiteTempColorType, kingWhiteTempX, kingWhiteTempY);

        Piece.PieceType kingBlackTempPieceType = other.kingBlack.getPieceType();
        Piece.ColorType kingBlackTempColorType = other.kingBlack.getPieceColor();
        int kingBlackTempX = other.kingBlack.getX();
        int kingBlackTempY = other.kingBlack.getY();
        kingBlack = new Piece(kingBlackTempPieceType, kingBlackTempColorType, kingBlackTempX, kingBlackTempY);

        Piece.PieceType emptyTempPieceType = other.emptyPiece.getPieceType();
        Piece.ColorType emptyTempColorType = other.emptyPiece.getPieceColor();
        int emptyTempX = other.emptyPiece.getX();
        int emptyTempY = other.emptyPiece.getY();
        emptyPiece = new Piece(emptyTempPieceType, emptyTempColorType, emptyTempX, emptyTempY);

        playerToMove = other.playerToMove;
        turnCount = other.turnCount;
        whiteKingHasMoved = other.whiteKingHasMoved;
        whiteRook1HasMoved = other.whiteRook1HasMoved;
        whiteRook2HasMoved = other.whiteRook2HasMoved;
        blackKingHasMoved = other.blackKingHasMoved;
        blackRook1HasMoved = other.blackRook1HasMoved;
        blackRook2HasMoved = other.blackRook2HasMoved;
        isCheck = other.isCheck;

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

    public void setCheck(boolean b) {
        isCheck = b;
    }

    public boolean getCheck() {
        return isCheck;
    }

    public void setKingWhite(int row, int col) {
        kingWhite.setX(row);
        kingWhite.setY(col);
    }

    public void setKingBlack(int row, int col) {
        kingBlack.setX(row);
        kingBlack.setY(col);
    }

    public Piece getKingWhite() {
        return kingWhite;
    }

    public Piece getKingBlack() {
        return kingBlack;
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

    public ArrayList<Piece> getWhiteCapturedPieces() {
        return this.whiteCapturedPieces;
    }

    public ArrayList<Piece> getBlackCapturedPieces() {
        return this.blackCapturedPieces;
    }

    public void addWhiteCapturedPiece(Piece p) {
        whiteCapturedPieces.add(p);
    }

    public void addBlackCapturedPiece(Piece p) {
        blackCapturedPieces.add(p);
    }


    //getters for the hasMoved variables
    public boolean getWhiteKingHasMoved(){return whiteKingHasMoved;}
    public boolean getWhiteRook1HasMoved(){return whiteRook1HasMoved;}
    public boolean getWhiteRook2HasMoved(){return whiteRook2HasMoved;}
    public boolean getBlackKingHasMoved(){return blackKingHasMoved;}
    public boolean getBlackRook1HasMoved(){return blackRook1HasMoved;}
    public boolean getBlackRook2HasMoved(){return blackRook2HasMoved;}
    //setters for the hasMoved variables
    public void setWhiteKingHasMoved(boolean hasMoved){whiteKingHasMoved = hasMoved;}
    public void setWhiteRook1HasMoved(boolean hasMoved){whiteRook1HasMoved = hasMoved;}
    public void setWhiteRook2HasMoved(boolean hasMoved){whiteRook2HasMoved = hasMoved;}
    public void setBlackKingHasMoved(boolean hasMoved){blackKingHasMoved = hasMoved;}
    public void setBlackRook1HasMoved(boolean hasMoved){blackRook1HasMoved = hasMoved;}
    public void setBlackRook2HasMoved(boolean hasMoved){blackRook2HasMoved = hasMoved;}

    //makes sure the king is not in check for castling
    public boolean getKingInCheck(){return kingInCheck;}
    public void setKingInCheck(boolean b){kingInCheck = b;}
}
