package edu.up.cs301.checkers.AllPieces;

import java.util.ArrayList;

import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.InfoMessage.Piece;

public class King {
    private ArrayList<Integer> xMovement;
    private ArrayList<Integer> yMovement;

    private ArrayList<Integer> xMovementAttack;
    private ArrayList<Integer> yMovementAttack;
    Piece.PieceType EMPTY = Piece.PieceType.EMPTY;

    private int x;
    private int y;

    private Piece.ColorType colorInverse;

    public King(Piece piece, CheckerState state, Piece.ColorType color) {
        x = piece.getX();
        y = piece.getY();
        xMovement = new ArrayList<>();
        yMovement = new ArrayList<>();
        xMovementAttack = new ArrayList<>();
        yMovementAttack = new ArrayList<>();
        kingMovement(state, color);
    }

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


        //Castling movements
        if(!state.getKingInCheck()) {
            if (color == Piece.ColorType.RED) {
                if (state.getPiece(5, 7).getPieceType() == EMPTY &&
                        state.getPiece(6, 7).getPieceType() == EMPTY) {
                    if (!state.getWhiteKingHasMoved() && !state.getWhiteRook1HasMoved()) {

                        xMovement.add(6);
                        yMovement.add(7);
                    }
                }
                if (state.getPiece(1, 7).getPieceType() == EMPTY &&
                        state.getPiece(2, 7).getPieceType() == EMPTY &&
                        state.getPiece(3, 7).getPieceType() == EMPTY) {
                    if (!state.getWhiteKingHasMoved() && !state.getWhiteRook2HasMoved()) {

                        xMovement.add(2);
                        yMovement.add(7);
                    }
                }
            }
            if (color == Piece.ColorType.BLACK) {
                if (state.getPiece(1, 0).getPieceType() == EMPTY &&
                        state.getPiece(2, 0).getPieceType() == EMPTY &&
                        state.getPiece(3, 0).getPieceType() == EMPTY) {
                    if (!state.getBlackKingHasMoved() && !state.getBlackRook1HasMoved()) {

                        xMovement.add(2);
                        yMovement.add(0);

                    }
                }
                if (state.getPiece(5, 0).getPieceType() == EMPTY &&
                        state.getPiece(6, 0).getPieceType() == EMPTY) {
                    if (!state.getBlackKingHasMoved() && !state.getBlackRook2HasMoved()) {

                        xMovement.add(6);
                        yMovement.add(0);
                    }
                }
            }
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
