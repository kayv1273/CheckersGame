package edu.up.cs301.checkers.Pawn;

import java.util.ArrayList;

import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.checkers.Views.Pieces;

public class Pawn {
    private ArrayList<Integer> xMoves;
    private ArrayList<Integer> yMoves;
    private ArrayList<Integer> xCaptures;
    private ArrayList<Integer> yCaptures;
    int row;
    int col;

    public Pawn(Pieces piece, CheckerState state, Pieces.Colors color) {
        row = piece.getX();
        col = piece.getY();
        xMoves = new ArrayList<>();
        yMoves = new ArrayList<>();
        xCaptures = new ArrayList<>();
        yCaptures = new ArrayList<>();

        if (color == Pieces.Colors.RED) {
            moveRedPawn(state);
        }
        else {
            moveBlackPawn(state);
        }
    }

    public void moveRedPawn(CheckerState state) {

        //check left border
        if (col == 0 && row > 0) {
            if (state.getPiece(row - 1, col + 1).getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col + 1);
            }
        }

        //check right border
        if (col == 7 && row > 0) {
            if (state.getPiece(row - 1, col - 1).getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col - 1);
            }
        }

        //anywhere else on the board
        if ((col > 0 && col < 7) && row > 0) {
            if (state.getPiece(row - 1, col + 1).getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col + 1);
            }
            if (state.getPiece(row - 1, col - 1).getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col - 1);
            }
        }


        //check captures
        if (col < 2 && row > 2) {
            if (state.getPiece(row - 2, col + 2).getColors() == Pieces.Colors.EMPTY && state.getPiece(row - 1, col + 1).getColors() == Pieces.Colors.BLACK) {
                xCaptures.add(row - 2);
                yCaptures.add(col + 2);
            }
        }

        if (col > 5 && row > 2) {
            if (state.getPiece(row - 2, col - 2).getColors() == Pieces.Colors.EMPTY && state.getPiece(row - 1, col - 1).getColors() == Pieces.Colors.BLACK) {
                xCaptures.add(row - 2);
                yCaptures.add(col - 2);
            }
        }

        if (col >= 2 && col <= 5 && row > 2) {
            if (state.getPiece(row - 2, col - 2).getColors() == Pieces.Colors.EMPTY && state.getPiece(row - 1, col - 1).getColors() == Pieces.Colors.BLACK) {
                xCaptures.add(row - 2);
                yCaptures.add(col - 2);
            }
            if (state.getPiece(row - 2, col + 2).getColors() == Pieces.Colors.EMPTY && state.getPiece(row - 1, col + 1).getColors() == Pieces.Colors.BLACK) {
                xCaptures.add(row - 2);
                yCaptures.add(col + 2);
            }
        }
    }

    public void moveBlackPawn(CheckerState state) {

        //check left border please
        if (col == 0 && row < 7) {
            if (state.getPiece(row + 1, col + 1).getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row + 1);
                yMoves.add(col + 1);
            }
        }

        //check right border
        if (col == 7 && row < 7) {
            if (state.getPiece(row + 1, col - 1).getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row + 1);
                yMoves.add(col - 1);
            }
        }

        if ((col > 0 && col < 7) && row < 7) {
            if (state.getPiece(row + 1, col - 1).getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row + 1);
                yMoves.add(col - 1);
            }
            if (state.getPiece(row + 1, col + 1).getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row + 1);
                yMoves.add(col + 1);
            }
        }

        // check capture from left column
        if (row < 6 && (col == 0 || col == 1)) {
            if (state.getPiece(row + 2, col + 2).getColors() == Pieces.Colors.EMPTY) {
                if (state.getPiece(row + 1, col + 1).getColors() == Pieces.Colors.RED) {
                    xMoves.add(row + 2);
                    yMoves.add(col + 2);
                }
            }
        }

        if (row < 6 && (col == 6 || col == 7)) {
            if (state.getPiece(row + 2, col - 2).getColors() == Pieces.Colors.EMPTY) {
                if (state.getPiece(row + 1, col - 1).getColors() == Pieces.Colors.RED) {
                    xMoves.add(row + 2);
                    yMoves.add(col - 2);
                }
            }
        }

        if (row < 6 && (col > 1 && col < 6)) {
            if (state.getPiece(row + 2, col + 2).getColors() == Pieces.Colors.EMPTY) {
                if (state.getPiece(row + 1, col + 1).getColors() == Pieces.Colors.RED) {
                    xMoves.add(row + 2);
                    yMoves.add(col + 2);
                }
            }

            if (state.getPiece(row + 2, col - 2).getColors() == Pieces.Colors.EMPTY) {
                if (state.getPiece(row + 1, col - 1).getColors() == Pieces.Colors.RED) {
                    xMoves.add(row + 2);
                    yMoves.add(col - 2);
                }
            }
        }
    }

    public ArrayList<Integer> getX() {
        return xMoves;
    }

    public ArrayList<Integer> getY() {
        return yMoves;
    }

    public ArrayList<Integer> getXCaptures() {
        return xCaptures;
    }

    public ArrayList<Integer> getYCaptures() {
        return yCaptures;
    }

}

