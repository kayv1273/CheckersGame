package edu.up.cs301.checkers.AllPieces;

import java.util.ArrayList;

import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.InfoMessage.Piece;
/**
 * @author Griselda
 * @author Katherine
 * @author Ruth
 * @author Nick
 * @author Ethan
 * @version 4.13.2023
 */


public class King {

    // instance variables to store possible moves and captures
    private ArrayList<Integer> xMovement;
    private ArrayList<Integer> yMovement;

    private ArrayList<Integer> xMovementAttack;
    private ArrayList<Integer> yMovementAttack;
    Piece.PieceType EMPTY = Piece.PieceType.EMPTY;


    // x and y coords of a given piece
    private int x;
    private int y;

    private Piece.ColorType colorInverse;

    // constructor for a king piece
    public King(Piece piece, CheckerState state, Piece.ColorType color) {
        x = piece.getX();
        y = piece.getY();
        xMovement = new ArrayList<>();
        yMovement = new ArrayList<>();
        xMovementAttack = new ArrayList<>();
        yMovementAttack = new ArrayList<>();
        kingMovement(state, color);
    }

    // gets all possible moves for a given king piece
    public void kingMovement(CheckerState state, Piece.ColorType color) {
        if(x > 0) {
            if(state.getPiece(x - 1, y).getPieceColor() != color) {
                xMovement.add(x - 1);
                yMovement.add(y);
            }
            xMovementAttack.add(x - 1);
            yMovementAttack.add(y);
        }
        if (x < 7) {
            if (state.getPiece(x + 1, y).getPieceColor() != color) {
                xMovement.add(x + 1);
                yMovement.add(y);
            }
            xMovementAttack.add(x + 1);
            yMovementAttack.add(y);
        }
        if (y > 0) {
            if(state.getPiece(x, y - 1).getPieceColor() != color) {
                xMovement.add(x);
                yMovement.add(y - 1);
            }
            xMovementAttack.add(x);
            yMovementAttack.add(y - 1);
        }
        if (y < 7) {
            if (state.getPiece(x, y + 1).getPieceColor() != color) {
                xMovement.add(x);
                yMovement.add(y + 1);
            }
            xMovementAttack.add(x);
            yMovementAttack.add(y + 1);
        }
        if (x > 0 && y > 0) {
            if(state.getPiece(x - 1, y - 1).getPieceColor() != color) {
                xMovement.add(x - 1);
                yMovement.add(y - 1);
            }
            xMovementAttack.add(x - 1);
            yMovementAttack.add(y - 1);
        }
        if (x > 0 && y < 7) {
            if(state.getPiece(x - 1, y + 1).getPieceColor() != color) {
                xMovement.add(x - 1);
                yMovement.add(y + 1);
            }
            xMovementAttack.add(x - 1);
            yMovementAttack.add(y + 1);
        }
        if (x < 7 && y > 0) {
            if(state.getPiece(x + 1, y - 1).getPieceColor() != color) {
                xMovement.add(x + 1);
                yMovement.add(y - 1);
            }
            xMovementAttack.add(x + 1);
            yMovementAttack.add(y - 1);
        }
        if (x < 7 && y < 7) {
            if(state.getPiece(x + 1, y + 1).getPieceColor() != color) {
                xMovement.add(x + 1);
                yMovement.add(y + 1);
            }
            xMovementAttack.add(x + 1);
            yMovementAttack.add(y + 1);
        }

    }

    public ArrayList<Integer> getX() {
        return xMovement;
    }

    public ArrayList<Integer> getY() {
        return yMovement;
    }

    public ArrayList<Integer> getXAttack() {
        return xMovementAttack;
    }

    public ArrayList<Integer> getYAttack() {
        return yMovementAttack;
    }
}
