package edu.up.cs301.checkers.InfoMessage;

import java.io.Serializable;
import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
/**
 * @author Griselda
 * @author Katherine
 * @author Ruth
 * @author Nick
 * @author Ethan
 * @version 4.26.2023
 */


//info
public class CheckerState extends GameState implements Serializable {
    private Piece[][] pieces; // An array that holds all of the pieces and their position
    private int[][] board; // An array that determines what kind of drawing should be made

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

    /**
     * Constructor for CheckerState
     */
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

                //fill first and third rows with black pieces alternating
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

        //create new empty piece
        emptyPiece = new Piece(Piece.PieceType.EMPTY, Piece.ColorType.EMPTY, 0, 0);

        //set all spaces in board array to 0
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = 0;
            }
        }

        //set player to move to human player
        playerToMove = 0;

    }

    /**
     * Deep Copy Constructor
     *
     * @param other the CheckerState being copied
     */
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

        //copy empties and X, Y
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

    /**
     * Getter method to get piece at a certain coordinate
     *
     * @param row the row index
     * @param col the col index
     * @return piece at the row, col index
     */
    public Piece getPiece(int row, int col) {
        return pieces[row][col];
    }

    /**
     * Setter method to set piece at a certain coordinate
     *
     * @param row the row index
     * @param col the col index
     * @param piece the new piece to be set
     */
    public void setPiece(int row, int col, Piece piece) {
        piece.setY(col);
        piece.setX(row);
        pieces[row][col] = piece;
    }

    /**
     * Setter to set if a move can be made
     * @param b true/false to change canMove
     */
    public void setCanMove(boolean b) {
        canMove = b;
    }

    /**
     * Getter method to check if move can be made
     * @return true/false depending on canMove
     */
    public boolean getCanMove() {
        return canMove;
    }

    /**
     * Set the current game status
     * @param b true/false depending on if game is over or not
     */
    public void setGameOver(boolean b) {
        isGameOver = b;
    }

    /**
     * Get current game status
     * @return true/false depending on state of the game (win/loss)
     */
    public boolean getGameOver() {
        return isGameOver;
    }

    /**
     * Set the arraylist for newMovementsX
     * @param newMovementsX new ArrayList to be set
     */
    public void setNewMovementsX(ArrayList<Integer> newMovementsX) {
        this.newMovementsX = newMovementsX;
    }

    /**
     * Get the newMovementsX arraylist
     * @return newMovementsX arrayList
     */
    public ArrayList<Integer> getNewMovementsX() {
        return newMovementsX;
    }

    /**
     * Set the arraylist for newMovementsY
     * @param newMovementsY new ArrayList to be set
     */
    public void setNewMovementsY(ArrayList<Integer> newMovementsY) {
        this.newMovementsY = newMovementsY;
    }

    /**
     * Get the newMovementsY arraylist
     * @return newMovementsY arrayList
     */
    public ArrayList<Integer> getNewMovementsY() {
        return newMovementsY;
    }


    /**
     * Mark a highlight in the board array for reference
     *
     * @param row current row
     * @param col current col
     */
    public void setHighlight(int row, int col) {
        board[row][col] = 1;
    }


    /**
     * Iterate through arrays to mark where circles should be placed on board array
     *
     * @param row arraylist row
     * @param col arraylist col
     */
    public void setCircles(ArrayList<Integer> row, ArrayList<Integer> col) {
        for (int i = 0; i < row.size(); i++) {
            if (getPiece(row.get(i), col.get(i)).getPieceColor() != Piece.ColorType.EMPTY) {
                board[row.get(i)][col.get(i)] = 4;
            } else {
                board[row.get(i)][col.get(i)] = 2;
            }
        }
    }

    /**
     * Removes all highlights from the board
     */
    public void removeHighlight() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 1) {
                    board[i][j] = 0;
                }
            }
        }
    }

    /**
     * Removes all circles from the board
     */
    public void removeCircle() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 2 || board[i][j] == 4) {
                    board[i][j] = 0;
                }
            }
        }
    }

    /**
     * Get integer indicator from board array
     *
     * @param row current row
     * @param col current col
     * @return 0,1,2,3 depending on if there is a piece, highligh, circle
     */
    public int getDrawing(int row, int col) {
        return board[row][col];
    }

    /**
     * Get whose move it is
     * @return 0 or 1 depending on human or ai
     */
    public int getWhoseMove() {
        return playerToMove;
    }

    /**
     * Set move
     * @param id either player or ai
     */
    public void setWhoseMove(int id) {
        playerToMove = id;
    }

    /**
     * Get arraylist of all pieces captured by red
     * @return arraylist of captured pieces
     */
    public ArrayList<Piece> getRedCapturedPieces() {
        return this.redCapturedPieces;
    }

    /**
     * Get arraylist of all pieces captured by black
     * @return arraylist of captured pieces
     */
    public ArrayList<Piece> getBlackCapturedPieces() {
        return this.blackCapturedPieces;
    }

    /**
     * Adds a piece that was captured to redCapturedPieces
     * @param p the piece being added to the arraylist
     */
    public void addRedCapturedPiece(Piece p) {
        redCapturedPieces.add(p);
    }

    /**
     * Adds a piece that was captured to blackCapturedPieces
     * @param p the piece being added to the arraylist
     */
    public void addBlackCapturedPiece(Piece p) {
        blackCapturedPieces.add(p);
    }

    private boolean hasCaptured;

    // method to set hasCaptured to true
    public void setCaptured() {
        hasCaptured = true;
    }

    // method to reset hasCaptured to false
    public void resetCaptured() {
        hasCaptured = false;
    }

    // method to check if a capture has been made
    public boolean justCaptured() { return hasCaptured; }
}

