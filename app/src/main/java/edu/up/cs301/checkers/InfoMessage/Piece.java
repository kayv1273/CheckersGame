package edu.up.cs301.checkers.InfoMessage;
/**
 * @author Griselda
 * @author Katherine
 * @author Ruth
 * @author Nick
 * @author Ethan
 * @version 4.13.2023
 */

public class Piece {

    // An enum for the different types of pieces
    public enum PieceType {
        PAWN, KING, EMPTY
    }

    // An enum for the color of the pieces
    public enum ColorType {
        BLACK, RED, EMPTY
    }

    private PieceType pieceType;
    private ColorType pieceColor;
    private int x;
    private int y;

    public Piece(PieceType pieceType, ColorType pieceColor,int x, int y) {
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        this.x = x;
        this.y = y;
    }

    public Piece(Piece other){
        this.pieceType = other.pieceType;
        this.pieceColor = other.pieceColor;
        this.x = other.x;
        this.y = other.y;
    }

    public PieceType getPieceType() {
        return pieceType;
    }
    public void setPieceType(PieceType type){this.pieceType = type;}

    public ColorType getPieceColor() {
        return pieceColor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int newX) {
        x = newX;
    }

    public void setY(int newY) {
        y = newY;
    }

    public void setColorType(ColorType type){this.pieceColor = type;}

    public ColorType getColorType(){return this.pieceColor;}
}