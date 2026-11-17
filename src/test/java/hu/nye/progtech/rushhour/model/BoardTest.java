package hu.nye.progtech.rushhour.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
