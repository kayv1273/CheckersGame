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

    /**
     * enum for the type of piece
     */
    public enum PieceType {
        PAWN, KING, EMPTY
    }

    /**
     * enum for the color of the piece
     */
    public enum ColorType {
        BLACK, RED, EMPTY
    }


    private PieceType pieceType; //type of piece
    private ColorType pieceColor;//color of piece
    private int x; //x coord of piece
    private int y; //y coord of piece

    /**
     * Constructor for Piece
     *
     * @param pieceType the type of piece
     * @param pieceColor the color of piece
     * @param x the x coord of piece
     * @param y the y coord of piece
     */
    public Piece(PieceType pieceType, ColorType pieceColor,int x, int y) {
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        this.x = x;
        this.y = y;
    }

    /**
     * Deep copy constructor for piece
     *
     * @param other the piece being copied
     */
    public Piece(Piece other){
        this.pieceType = other.pieceType;
        this.pieceColor = other.pieceColor;
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Get the type of piece
     * @return type
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Set the type of piece, usually from pawn to king or either to empty
     *
     * @param type the type of piece
     */
    public void setPieceType(PieceType type){this.pieceType = type;}

    /**
     * Get the color of the piece
     *
     * @return color
     */
    public ColorType getPieceColor() {
        return pieceColor;
    }

    /**
     * Get the x coord
     * @return x coord
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coord
     * @return y coord
     */
    public int getY() {
        return y;
    }

    /**
     * Set the x coord
     * @param newX the new x coord
     */
    public void setX(int newX) {
        x = newX;
    }

    /**
     * Set the y coord
     * @param newY the new y coord
     */
    public void setY(int newY) {
        y = newY;
    }

    /**
     * Set the color of the piece
     * @param type the new color
     */
    public void setColorType(ColorType type){this.pieceColor = type;}

    /**
     * Get the color of the piece
     * @return color
     */
    public ColorType getColorType(){return this.pieceColor;}
}