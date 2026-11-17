package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Player;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
                "A", 2, Orientation.HORIZONTAL, 2, 4, true));

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
                "A", 2, Orientation.HORIZONTAL, 2, 0, true));

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
}
