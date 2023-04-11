package edu.up.cs301.checkers.Pawn;

import java.util.ArrayList;

import edu.up.cs301.checkers.CheckerState;
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

    public void movePawn() {
        //check pawn on left most side of the board
        if (col == 0 && row > 0) {
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.EMPTY && pieces[row][col].getColors() == Pieces.Colors.RED) {
                xMoves.add(row - 1);
                yMoves.add(col + 1);
            }

            //check if left column can capture a piece
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col + 2].getColors() == Pieces.Colors.EMPTY && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {
                xMoves.add(row - 2);
                yMoves.add(col + 2);
                pieces[row - 1][col + 1].setColor(Pieces.Colors.EMPTY);
            }
        }

        //check pawn on right most side of the board
        else if (col == 7 && row > 0 && pieces[row][col].getColors() == Pieces.Colors.RED) {
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col - 1);
            }

            //check if right column can capture a piece
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col - 2].getColors() == Pieces.Colors.EMPTY && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {
                xMoves.add(row - 2);
                yMoves.add(col - 2);
                pieces[row - 1][col - 1].setColor(Pieces.Colors.EMPTY);
            }
        }

        //check captures for column 1
        else if (col == 1 && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col + 2].getColors() == Pieces.Colors.EMPTY && row > 1) {
                xMoves.add(row - 2);
                yMoves.add(col + 2);
                pieces[row - 1][col + 1].setColor(Pieces.Colors.EMPTY);
            }
        }


        //pawn is not on the border of the board
        else if (row > 0 && pieces[row][col].getColors() == Pieces.Colors.RED) {
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col - 1);
            }
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.EMPTY) {
                xMoves.add(row - 1);
                yMoves.add(col + 1);
            }
        }

        //check captures for column 6
        else if (col == 6 && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col - 2].getColors() == Pieces.Colors.EMPTY && row > 1) {
                xMoves.add(row - 2);
                yMoves.add(col - 2);
                pieces[row - 1][col - 1].setColor(Pieces.Colors.EMPTY);
            }
        }

        //check capture for columns 2-5
        else if ((col > 1 && col < 6) && row > 1 && pieces[row][col].getColors() == Pieces.Colors.RED) {

            //check diagonal left
            if (pieces[row - 1][col - 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col - 2].getColors() == Pieces.Colors.EMPTY && row > 1) {
                xMoves.add(row - 2);
                yMoves.add(col - 2);
                pieces[row - 1][col - 1].setColor(Pieces.Colors.EMPTY);

                //check diagonal right
            }
            if (pieces[row - 1][col + 1].getColors() == Pieces.Colors.BLACK && pieces[row - 2][col + 2].getColors() == Pieces.Colors.EMPTY && row > 1) {
                xMoves.add(row - 2);
                yMoves.add(col + 2);
                pieces[row - 1][col + 1].setColor(Pieces.Colors.EMPTY);
            }
        }
    }
}
