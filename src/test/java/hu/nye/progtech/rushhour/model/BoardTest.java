package hu.nye.progtech.rushhour.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void shouldCreateBoard() {
        Board board = new Board(6, 6);

        assertEquals(6, board.getRows());
        assertEquals(6, board.getColumns());
        assertTrue(board.getVehicles().isEmpty());
    }

    @Test
    void shouldRejectInvalidBoardSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new Board(0, 6));
    }

    @Test
    void shouldAddVehicle() {
        Board board = new Board(6, 6);
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        board.addVehicle(vehicle);

        assertEquals(1, board.getVehicles().size());
        assertSame(vehicle, board.getVehicleAt(2, 0));
        assertSame(vehicle, board.getVehicleAt(2, 1));
    }

    @Test
    void shouldRejectOverlappingVehicles() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        assertThrows(IllegalArgumentException.class,
                () -> board.addVehicle(new Vehicle(
                        "B", 3, Orientation.VERTICAL, 1, 1)));
    }

    @Test
    void shouldRejectDuplicateVehicleId() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        assertThrows(IllegalArgumentException.class,
                () -> board.addVehicle(new Vehicle(
                        "A", 2, Orientation.HORIZONTAL, 4, 0)));
    }

    @Test
    void shouldMoveVehicle() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        assertTrue(board.moveVehicle("A", 2));

        assertNull(board.getVehicleAt(2, 0));
        assertNotNull(board.getVehicleAt(2, 2));
        assertNotNull(board.getVehicleAt(2, 3));
    }

    @Test
    void shouldRejectMoveOutsideBoard() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        assertFalse(board.moveVehicle("A", -1));
    }

    @Test
    void shouldRejectBlockedMove() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));
        board.addVehicle(new Vehicle(
                "B", 3, Orientation.VERTICAL, 1, 3));

        assertFalse(board.moveVehicle("A", 2));
    }

    @Test
    void shouldRejectZeroStepMove() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        assertFalse(board.moveVehicle("A", 0));
    }

    @Test
    void shouldDetectHumanVictory() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 4, true));

        assertTrue(board.exitVehicle("A", true));
        assertTrue(board.isGameOver());
        assertEquals("A", board.getWinnerId());
    }

    @Test
    void shouldRejectExitBeforeReachingEdge() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0, true));

        assertFalse(board.exitVehicle("A", true));
        assertFalse(board.isGameOver());
    }

    @Test
    void shouldDetectComputerVictory() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.HORIZONTAL, 4, 0, true));

        assertTrue(board.exitVehicle("C", false));
        assertEquals("C", board.getWinnerId());
        assertTrue(board.isGameOver());
    }

    @Test
    void shouldMoveHorizontalVehicleLeft() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 3));

        assertTrue(board.moveVehicle("A", -2));

        assertEquals("A", board.getVehicleAt(2, 1).getId());
        assertEquals("A", board.getVehicleAt(2, 2).getId());
        assertNull(board.getVehicleAt(2, 3));
        assertNull(board.getVehicleAt(2, 4));
    }

    @Test
    void shouldMoveVerticalVehicleUp() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.VERTICAL, 3, 2));

        assertTrue(board.moveVehicle("B", -2));

        assertEquals("B", board.getVehicleAt(1, 2).getId());
        assertEquals("B", board.getVehicleAt(2, 2).getId());
        assertNull(board.getVehicleAt(3, 2));
        assertNull(board.getVehicleAt(4, 2));
    }

    @Test
    void shouldMoveVerticalVehicleDown() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.VERTICAL, 0, 2));

        assertTrue(board.moveVehicle("B", 2));

        assertNull(board.getVehicleAt(0, 2));
        assertNull(board.getVehicleAt(1, 2));
        assertEquals("B", board.getVehicleAt(2, 2).getId());
        assertEquals("B", board.getVehicleAt(3, 2).getId());
    }

    @Test
    void shouldRejectBlockedLeftMove() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 3));
        board.addVehicle(new Vehicle(
                "B", 3, Orientation.VERTICAL, 1, 2));

        assertFalse(board.moveVehicle("A", -2));

        assertEquals("A", board.getVehicleAt(2, 3).getId());
        assertEquals("A", board.getVehicleAt(2, 4).getId());
    }

    @Test
    void shouldRejectBlockedUpwardMove() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.VERTICAL, 3, 2));
        board.addVehicle(new Vehicle(
                "D", 2, Orientation.HORIZONTAL, 2, 1));

        assertFalse(board.moveVehicle("B", -2));

        assertEquals("B", board.getVehicleAt(3, 2).getId());
        assertEquals("B", board.getVehicleAt(4, 2).getId());
    }

    @Test
    void shouldRejectMoveBeyondTopEdge() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.VERTICAL, 0, 2));

        assertFalse(board.moveVehicle("B", -1));
        assertEquals("B", board.getVehicleAt(0, 2).getId());
    }

    @Test
    void shouldRejectMoveBeyondRightEdge() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 4));

        assertFalse(board.moveVehicle("A", 1));
        assertEquals("A", board.getVehicleAt(2, 4).getId());
    }

    @Test
    void shouldRejectMoveBeyondBottomEdge() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.VERTICAL, 4, 2));

        assertFalse(board.moveVehicle("B", 1));
        assertEquals("B", board.getVehicleAt(4, 2).getId());
    }

    @Test
    void shouldAcceptLowercaseVehicleId() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        assertTrue(board.moveVehicle("a", 2));
        assertEquals("A", board.getVehicleAt(2, 2).getId());
    }

    @Test
    void shouldAllowComputerToReachLeftExitAndWin() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.HORIZONTAL, 4, 3, true));

        assertTrue(board.moveVehicle("C", -3));
        assertEquals("C", board.getVehicleAt(4, 0).getId());

        assertTrue(board.exitVehicle("C", false));
        assertTrue(board.isGameOver());
        assertEquals("C", board.getWinnerId());
        assertTrue(board.getVehicles().isEmpty());
    }
    @Test
    void shouldBeEqualWithSameDimensions() {
        Board first = new Board(6, 6);
        Board second = new Board(6, 6);

        assertEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWithDifferentRows() {
        Board first = new Board(6, 6);
        Board second = new Board(8, 6);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWithDifferentColumns() {
        Board first = new Board(6, 6);
        Board second = new Board(6, 8);

        assertNotEquals(first, second);
    }

    @Test
    void shouldBeEqualWithSameVehicles() {
        Board first = new Board(6, 6);
        Board second = new Board(6, 6);

        first.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        second.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        assertEquals(first, second);
        assertEquals(second, first);
    }

    @Test
    void shouldNotBeEqualWithDifferentVehicles() {
        Board first = new Board(6, 6);
        Board second = new Board(6, 6);

        first.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        second.addVehicle(new Vehicle(
                "B", 2, Orientation.HORIZONTAL, 2, 0));

        assertNotEquals(first, second);
    }

    @Test
    void shouldBeEqualRegardlessOfVehicleOrder() {
        Board first = new Board(6, 6);
        Board second = new Board(6, 6);

        Vehicle a = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        Vehicle b = new Vehicle(
                "B", 2, Orientation.VERTICAL, 0, 4);

        first.addVehicle(a);
        first.addVehicle(b);

        second.addVehicle(b);
        second.addVehicle(a);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldBeEqualToItself() {
        Board board = new Board(6, 6);

        assertTrue(board.equals(board));
    }

    @Test
    void shouldNotBeEqualToNull() {
        Board board = new Board(6, 6);

        assertFalse(board.equals(null));
    }

    @Test
    void shouldNotBeEqualToDifferentObjectType() {
        Board board = new Board(6, 6);

        assertFalse(board.equals("Board"));
    }

    @Test
    void shouldHaveSameHashCodeForEqualBoards() {
        Board first = new Board(6, 6);
        Board second = new Board(6, 6);

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldCreateEqualButIndependentCopy() {
        Board original = new Board(6, 6);

        original.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        Board copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());

        assertTrue(copy.moveVehicle("A", 2));

        assertNotEquals(original, copy);

        assertEquals(0,
                original.getVehicleById("A").getColumn());

        assertEquals(2,
                copy.getVehicleById("A").getColumn());
    }

    @Test
    void shouldNotBeEqualWithDifferentWinnerState() {
        Board first = new Board(6, 6);
        Board second = new Board(6, 6);

        first.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 4, true));

        assertTrue(first.exitVehicle("A", true));

        assertNotEquals(first, second);
    }

    @Test
    void shouldPreserveWinnerWhenCopied() {
        Board original = new Board(6, 6);

        original.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 4, true));

        assertTrue(original.exitVehicle("A", true));

        Board copy = original.copy();

        assertEquals(original, copy);
        assertTrue(copy.isGameOver());
        assertEquals("A", copy.getWinnerId());
    }

    @Test
    void shouldReturnBoardAsString() {
        Board board = new Board(6, 8);

        String result = board.toString();

        assertTrue(result.contains("rows=6"));
        assertTrue(result.contains("columns=8"));
        assertTrue(result.contains("vehicles="));
        assertTrue(result.contains("winnerId="));
    }

    @Test
    void shouldReturnUnmodifiableVehicleList() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        assertThrows(UnsupportedOperationException.class,
                () -> board.getVehicles().clear());

        assertEquals(1, board.getVehicles().size());
    }
}