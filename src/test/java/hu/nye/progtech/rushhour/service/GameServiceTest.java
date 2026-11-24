package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Player;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameServiceTest {

    private GameService createGame(Board board) {
        Player human = new Player("TesztJatekos", false);
        Player computer = new Player("Computer", true);
        return new GameService(board, human, computer);
    }

    @Test
    void humanShouldStartFirst() {
        GameService game = createGame(new Board(6, 6));

        assertTrue(game.isHumanTurn());
        assertEquals("TesztJatekos",
                game.getCurrentPlayer().getName());
    }

    @Test
    void successfulHumanMoveShouldSwitchTurn() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        GameService game = createGame(board);

        assertTrue(game.humanMove("A", 1));
        assertFalse(game.isHumanTurn());
        assertEquals("Computer",
                game.getCurrentPlayer().getName());
    }

    @Test
    void invalidHumanMoveShouldNotSwitchTurn() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        GameService game = createGame(board);

        assertFalse(game.humanMove("A", -1));
        assertTrue(game.isHumanTurn());
    }

    @Test
    void computerCannotMoveDuringHumanTurn() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        GameService game = createGame(board);

        assertFalse(game.computerMove());
        assertTrue(game.isHumanTurn());
    }

    @Test
    void computerShouldReturnTurnToHuman() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        GameService game = createGame(board);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());
        assertTrue(game.isHumanTurn());
    }

    @Test
    void humanShouldWinWhenTargetExits() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 4, true));

        GameService game = createGame(board);

        assertTrue(game.humanExit());
        assertTrue(board.isGameOver());
        assertEquals("TesztJatekos",
                game.getWinner().getName());
    }

    @Test
    void shouldRejectHumanExitBeforeEdge() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, true));

        GameService game = createGame(board);

        assertFalse(game.humanExit());
        assertNull(game.getWinner());
    }

    @Test
    void shouldRejectInvalidPlayers() {
        Board board = new Board(6, 6);

        assertThrows(IllegalArgumentException.class,
                () -> new GameService(
                        board,
                        new Player("Gep1", true),
                        new Player("Gep2", true)));
    }

    @Test
    void computerShouldMoveTargetLeft() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, true));

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.HORIZONTAL,
                4, 3, true));

        GameService game = createGame(board);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertEquals(0,
                board.getVehicleById("C").getColumn());

        assertTrue(game.isHumanTurn());
    }

    @Test
    void computerShouldWinWhenTargetReachesExit() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, true));

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.HORIZONTAL,
                4, 3, true));

        GameService game = createGame(board);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertEquals(0,
                board.getVehicleById("C").getColumn());

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertTrue(board.isGameOver());
        assertEquals("C", board.getWinnerId());
        assertEquals("Computer",
                game.getWinner().getName());
    }

    @Test
    void computerShouldExitImmediatelyWhenPossible() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, true));

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.HORIZONTAL,
                4, 0, true));

        GameService game = createGame(board);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertTrue(board.isGameOver());
        assertEquals("C", board.getWinnerId());
        assertNull(board.getVehicleById("C"));
    }

    @Test
    void simulationShouldNotModifyOriginalBoard() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        Board simulation = board.copy();

        assertNotSame(board, simulation);
        assertTrue(simulation.moveVehicle("A", 2));

        assertEquals(0,
                board.getVehicleById("A").getColumn());

        assertEquals(2,
                simulation.getVehicleById("A").getColumn());
    }

    @Test
    void simulationExitShouldNotEndOriginalGame() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.HORIZONTAL,
                4, 0, true));

        Board simulation = board.copy();

        assertTrue(simulation.exitVehicle("C", false));

        assertTrue(simulation.isGameOver());
        assertFalse(board.isGameOver());
        assertNull(board.getWinnerId());

        assertEquals("C",
                board.getVehicleById("C").getId());
    }

    @Test
    void computerShouldNotModifyOtherVehiclesDuringSimulation() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, true));

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.VERTICAL, 0, 5));

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.HORIZONTAL,
                4, 3, true));

        GameService game = createGame(board);

        assertTrue(game.humanMove("A", 1));

        Vehicle originalA = board.getVehicleById("A");
        Vehicle originalB = board.getVehicleById("B");
        Vehicle originalC = board.getVehicleById("C");

        assertTrue(game.computerMove());

        assertEquals(originalA,
                board.getVehicleById("A"));

        assertEquals(originalB,
                board.getVehicleById("B"));

        assertEquals(3, originalC.getColumn());

        assertEquals(0,
                board.getVehicleById("C").getColumn());
    }

    @Test
    void computerShouldRespectBlockingVehicle() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, true));

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.VERTICAL, 3, 1));

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.HORIZONTAL,
                4, 3, true));

        GameService game = createGame(board);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertEquals(2,
                board.getVehicleById("C").getColumn());

        assertFalse(board.isGameOver());
    }

    @Test
    void shouldRejectMovesAfterGameOver() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 4, true));

        GameService game = createGame(board);

        assertTrue(game.humanExit());

        assertFalse(game.humanMove("A", -1));
        assertFalse(game.computerMove());
        assertFalse(game.humanExit());
    }
}