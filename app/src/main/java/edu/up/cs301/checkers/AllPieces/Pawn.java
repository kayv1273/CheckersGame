package edu.up.cs301.checkers.AllPieces;

import java.util.ArrayList;

import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.InfoMessage.Piece;

public class Pawn {
    private ArrayList<Integer> xMovement;
    private ArrayList<Integer> yMovement;

    private ArrayList<Integer> xMovementAttack;
    private ArrayList<Integer> yMovementAttack;

    private int row;
    private int col;

    public Pawn(Piece piece, CheckerState state, Piece.ColorType color) {
        row = piece.getX();
        col = piece.getY();
        xMovement = new ArrayList<>();
        yMovement = new ArrayList<>();
        xMovementAttack = new ArrayList<>();
        yMovementAttack = new ArrayList<>();
        if(color == Piece.ColorType.RED) {
            pawnMovementWhite(state);
            pawnCaptureRed(state);
        } else if (color == Piece.ColorType.BLACK) {
            pawnMovementBlack(state);
            pawnCaptureBlack(state);
        }
    }

    public void pawnMovementWhite(CheckerState state) {
        //check left border
        if (row == 0 && col > 0) {
            if (state.getPiece(row + 1, col - 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row + 1);
                yMovement.add(col - 1);
            }
        }

        //check right border
        if (row == 7 && col > 0) {
            if (state.getPiece(row - 1, col - 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row - 1);
                yMovement.add(col - 1);
            }
        }

        //anywhere else on the board
        if ((row > 0 && row < 7) && col > 0) {
            if (state.getPiece(row + 1, col - 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row + 1);
                yMovement.add(col - 1);
            }
            if (state.getPiece(row - 1, col - 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row - 1);
                yMovement.add(col - 1);
            }
        }
    }

    public void pawnMovementBlack(CheckerState state) {
        //check left border please
        if (row == 0 && col < 7) {
            if (state.getPiece(row + 1, col + 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row + 1);
                yMovement.add(col + 1);
            }
        }

        //check right border
        if (row == 7 && col < 7) {
            if (state.getPiece(row - 1, col + 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row - 1);
                yMovement.add(col + 1);
            }
        }

        if ((row > 0 && row < 7) && col < 7) {
            if (state.getPiece(row - 1, col + 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row - 1);
                yMovement.add(col + 1);
            }
            if (state.getPiece(row + 1, col + 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row + 1);
                yMovement.add(col + 1);
            }
        }
    }

    public void pawnCaptureRed(CheckerState state) {
        //check left border
        if ((row == 0 || row == 1) && col > 1) {
            if (state.getPiece(row + 1, col - 1).getPieceColor() == Piece.ColorType.BLACK && state.getPiece(row + 2, col - 2).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovementAttack.add(row + 2);
                yMovementAttack.add(col - 2);
            }
        }

        //check right border
        if ((row == 7 || row == 6) && col > 1) {
            if (state.getPiece(row - 1, col - 1).getPieceColor() == Piece.ColorType.BLACK && state.getPiece(row - 2, col - 2).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovementAttack.add(row - 2);
                yMovementAttack.add(col - 2);
            }
        }

        //anywhere else on the board
        if ((row > 1 && row < 6) && col > 1) {
            if (state.getPiece(row + 1, col - 1).getPieceColor() == Piece.ColorType.BLACK && (state.getPiece(row + 2, col - 2).getPieceColor() == Piece.ColorType.EMPTY)) {
                xMovementAttack.add(row + 2);
                yMovementAttack.add(col - 2);
            }
            if (state.getPiece(row - 1, col - 1).getPieceColor() == Piece.ColorType.BLACK && (state.getPiece(row - 2, col - 2).getPieceColor() == Piece.ColorType.EMPTY)) {
                xMovementAttack.add(row - 2);
                yMovementAttack.add(col - 2);
            }
        }
    }

    public void pawnCaptureBlack(CheckerState state) {
        //check left border
        if ((row == 0 || row == 1) && col < 6) {
            if (state.getPiece(row + 1, col + 1).getPieceColor() == Piece.ColorType.RED && state.getPiece(row + 2, col + 2).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovementAttack.add(row + 2);
                yMovementAttack.add(col + 2);
            }
        }

        //check right border
        if ((row == 7 || row == 6) && col < 6) {
            if (state.getPiece(row - 1, col + 1).getPieceColor() == Piece.ColorType.RED && state.getPiece(row - 2, col + 2).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovementAttack.add(row - 2);
                yMovementAttack.add(col + 2);
            }
        }

        //anywhere else on the board
        if ((row > 1 && row < 6) && col < 6) {
            if (state.getPiece(row + 1, col + 1).getPieceColor() == Piece.ColorType.RED && (state.getPiece(row + 2, col + 2).getPieceColor() == Piece.ColorType.EMPTY)) {
                xMovementAttack.add(row + 2);
                yMovementAttack.add(col + 2);
            }
            if (state.getPiece(row - 1, col + 1).getPieceColor() == Piece.ColorType.RED && (state.getPiece(row - 2, col + 2).getPieceColor() == Piece.ColorType.EMPTY)) {
                xMovementAttack.add(row - 2);
                yMovementAttack.add(col + 2);
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

