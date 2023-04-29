package edu.up.cs301.checkers.InfoMessage;

import static org.junit.Assert.*;

import org.junit.Test;

public class CheckerStateTest {

    @Test
    public void getCanMove() {
        CheckerState state = new CheckerState();
        boolean b = state.getCanMove();
        assertEquals(false, b);
    }

    @Test
    public void getDrawing() {
        CheckerState state = new CheckerState();
        int test = state.getDrawing(0, 0);
        assertEquals(0, test);
    }

    @Test
    public void getWhoseMoveStart() {
        CheckerState state = new CheckerState();
        int test = state.getWhoseMove();
        assertEquals(0, test);
    }

    @Test
    public void getWhoseMoveOtherPlayer() {
        CheckerState state = new CheckerState();
        state.setWhoseMove(1);
        int test = state.getWhoseMove();
        assertEquals(1, test);
    }
}