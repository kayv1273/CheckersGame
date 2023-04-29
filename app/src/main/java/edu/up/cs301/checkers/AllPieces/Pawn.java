package edu.up.cs301.checkers.AllPieces;

import java.util.ArrayList;
import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.InfoMessage.Piece;

public class Pawn {

    // Instance variables to store all possible moves and captures
    private ArrayList<Integer> xMovement;
    private ArrayList<Integer> yMovement;

    private ArrayList<Integer> xMovementAttack;
    private ArrayList<Integer> yMovementAttack;
    private int row;
    private int col;

    /**
     * Constructor for Pawn
     *
     * @param piece the current piece
     * @param state the CheckerState
     * @param color the color of the piece
     */
    public Pawn(Piece piece, CheckerState state, Piece.ColorType color) {
        row = piece.getX();
        col = piece.getY();
        xMovement = new ArrayList<>();
        yMovement = new ArrayList<>();
        xMovementAttack = new ArrayList<>();
        yMovementAttack = new ArrayList<>();
        if(color == Piece.ColorType.RED) {
            pawnMovementRed(state);
            pawnCaptureRed(state);
        } else if (color == Piece.ColorType.BLACK) {
            pawnMovementBlack(state);
            pawnCaptureBlack(state);
        }
    }

    /**
     * checks for all possible moves for a red pawn
     *
     * @param state the CheckerState
     */
    public void pawnMovementRed(CheckerState state) {
        //check left border
        if (row == 0 && col > 0) {
            //top right of piece
            if (state.getPiece(row + 1, col - 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row + 1);
                yMovement.add(col - 1);
            }
        }

        //check right border
        if (row == 7 && col > 0) {
            //top left of piece
            if (state.getPiece(row - 1, col - 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row - 1);
                yMovement.add(col - 1);
            }
        }

        //check rest of the board
        if ((row > 0 && row < 7) && col > 0) {
            //top right of piece
            if (state.getPiece(row + 1, col - 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row + 1);
                yMovement.add(col - 1);
            }
            //top left of piece
            if (state.getPiece(row - 1, col - 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row - 1);
                yMovement.add(col - 1);
            }
        }
    }

    /**
     * checks for all possible moves for a black pawn
     *
     * @param state the CheckerState
     */
    public void pawnMovementBlack(CheckerState state) {
        //check left border
        if (row == 0 && col < 7) {
            //bottom right of piece
            if (state.getPiece(row + 1, col + 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row + 1);
                yMovement.add(col + 1);
            }
        }

        //check right border
        if (row == 7 && col < 7) {
            //bottom left of piece
            if (state.getPiece(row - 1, col + 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row - 1);
                yMovement.add(col + 1);
            }
        }

        //check rest of the board
        if ((row > 0 && row < 7) && col < 7) {
            //bottom left of piece
            if (state.getPiece(row - 1, col + 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row - 1);
                yMovement.add(col + 1);
            }
            //bottom right of piece
            if (state.getPiece(row + 1, col + 1).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovement.add(row + 1);
                yMovement.add(col + 1);
            }
        }
    }

    /**
     * checks for all possible captures for a red pawn
     *
     * @param state the CheckerState
     */
    public void pawnCaptureRed(CheckerState state) {
        //check left border
        if ((row == 0 || row == 1) && col > 1) {
            //top right of piece
            if (state.getPiece(row + 1, col - 1).getPieceColor() == Piece.ColorType.BLACK &&
                    state.getPiece(row + 2, col - 2).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovementAttack.add(row + 2);
                yMovementAttack.add(col - 2);
            }
        }

        //check right border
        if ((row == 7 || row == 6) && col > 1) {
            //top left of piece
            if (state.getPiece(row - 1, col - 1).getPieceColor() == Piece.ColorType.BLACK &&
                    state.getPiece(row - 2, col - 2).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovementAttack.add(row - 2);
                yMovementAttack.add(col - 2);
            }
        }

        //check rest of the board
        if ((row > 1 && row < 6) && col > 1) {
            //top right of piece
            if (state.getPiece(row + 1, col - 1).getPieceColor() == Piece.ColorType.BLACK &&
                    (state.getPiece(row + 2, col - 2).getPieceColor() == Piece.ColorType.EMPTY)) {
                xMovementAttack.add(row + 2);
                yMovementAttack.add(col - 2);
            }
            //top left of piece
            if (state.getPiece(row - 1, col - 1).getPieceColor() == Piece.ColorType.BLACK &&
                    (state.getPiece(row - 2, col - 2).getPieceColor() == Piece.ColorType.EMPTY)) {
                xMovementAttack.add(row - 2);
                yMovementAttack.add(col - 2);
            }
        }
    }

    /**
     * checks for all possible captures for a black pawn
     *
     * @param state the CheckerState
     */
    public void pawnCaptureBlack(CheckerState state) {
        //check left border
        if ((row == 0 || row == 1) && col < 6) {
            //bottom right of piece
            if (state.getPiece(row + 1, col + 1).getPieceColor() == Piece.ColorType.RED &&
                    state.getPiece(row + 2, col + 2).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovementAttack.add(row + 2);
                yMovementAttack.add(col + 2);
            }
        }

        //check right border
        if ((row == 7 || row == 6) && col < 6) {
            //bottom left of piece
            if (state.getPiece(row - 1, col + 1).getPieceColor() == Piece.ColorType.RED &&
                    state.getPiece(row - 2, col + 2).getPieceColor() == Piece.ColorType.EMPTY) {
                xMovementAttack.add(row - 2);
                yMovementAttack.add(col + 2);
            }
        }

        //check rest of the board
        if ((row > 1 && row < 6) && col < 6) {
            //bottom right of piece
            if (state.getPiece(row + 1, col + 1).getPieceColor() == Piece.ColorType.RED &&
                    (state.getPiece(row + 2, col + 2).getPieceColor() == Piece.ColorType.EMPTY)) {
                xMovementAttack.add(row + 2);
                yMovementAttack.add(col + 2);
            }
            //bottom left of piece
            if (state.getPiece(row - 1, col + 1).getPieceColor() == Piece.ColorType.RED && (
                    state.getPiece(row - 2, col + 2).getPieceColor() == Piece.ColorType.EMPTY)) {
                xMovementAttack.add(row - 2);
                yMovementAttack.add(col + 2);
            }
        }
    }


    /**
     * get the x-coords of the piece's possible moves
     *
     * @return the x-coords of pieces' possible moves
     */
    public ArrayList<Integer> getX() {
        return xMovement;
    }

    /**
     * get the y-coords of the piece's possible moves
     *
     * @return the y-coords of pieces' possible moves
     */
    public ArrayList<Integer> getY() {
        return yMovement;
    }

    /**
     * get the x-coords of the piece's possible captures
     *
     * @return the x-coords of pieces' possible captures
     */
    public ArrayList<Integer> getXAttack() {
        return xMovementAttack;
    }

    /**
     * get the x-coords of the piece's possible captures
     *
     * @return the x-coords of pieces' possible captures
     */
    public ArrayList<Integer> getYAttack() {
        return yMovementAttack;
    }
}


