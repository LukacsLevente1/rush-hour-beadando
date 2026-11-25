package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.ExitEdge;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Player;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.model.VehicleOwner;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class GameServiceTest {

    private GameService createGame(Board board) {
        return createGame(board, new Random(42));
    }

    private GameService createGame(Board board, Random random) {
        Player human = new Player("TesztJatekos", false);
        Player computer = new Player("Computer", true);

        return new GameService(
                board, human, computer, random);
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
                2, 4, VehicleOwner.HUMAN, ExitEdge.RIGHT));

        GameService game = createGame(board);

        assertTrue(game.humanExit());
        assertTrue(board.isGameOver());
        assertEquals(VehicleOwner.HUMAN, board.getWinnerOwner());
        assertFalse(game.isHumanTurn());

        assertEquals("TesztJatekos",
                game.getWinner().getName());
    }

    @Test
    void shouldRejectHumanExitBeforeEdge() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, VehicleOwner.HUMAN, ExitEdge.RIGHT));

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
    void shouldRejectNullRandom() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameService(
                        new Board(6, 6),
                        new Player("Anna", false),
                        new Player("Computer", true),
                        null));
    }

    @Test
    void givenTwoVehicles_whenAiMoves_thenVehicleIsSelectedFirst() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, VehicleOwner.HUMAN, ExitEdge.RIGHT));

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.HORIZONTAL,
                4, 3, VehicleOwner.COMPUTER, ExitEdge.LEFT));

        Random random = mock(Random.class);

        when(random.nextInt(2)).thenReturn(1);
        when(random.nextInt(4)).thenReturn(1);

        GameService game = createGame(board, random);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertEquals("B",
                game.getLastComputerMove().vehicleId());

        assertEquals(-2,
                game.getLastComputerMove().steps());

        assertEquals(1,
                board.getVehicleById("B").getColumn());

        verify(random).nextInt(2);
        verify(random).nextInt(4);

        assertTrue(game.isHumanTurn());
    }

    @Test
    void computerShouldWinWhenTargetReachesExit() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, VehicleOwner.HUMAN, ExitEdge.RIGHT));

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.HORIZONTAL,
                4, 3, VehicleOwner.COMPUTER, ExitEdge.LEFT));

        Random random = mock(Random.class);

        when(random.nextInt(2)).thenReturn(1);
        when(random.nextInt(4)).thenReturn(0);

        GameService game = createGame(board, random);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertEquals(0,
                board.getVehicleById("B").getColumn());

        assertTrue(game.humanMove("A", 1));
        when(random.nextInt(5)).thenReturn(4);
        assertTrue(game.computerMove());

        assertTrue(board.isGameOver());
        assertTrue(game.getLastComputerMove().exit());
        assertEquals(VehicleOwner.COMPUTER, board.getWinnerOwner());
        assertEquals("B", board.getWinnerId());

        assertEquals("Computer",
                game.getWinner().getName());
    }

    @Test
    void computerShouldExitWhenRandomlySelected() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, VehicleOwner.HUMAN, ExitEdge.RIGHT));

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.HORIZONTAL,
                4, 0, VehicleOwner.COMPUTER, ExitEdge.LEFT));

        Random random = mock(Random.class);
        when(random.nextInt(2)).thenReturn(1);
        when(random.nextInt(5)).thenReturn(4);

        GameService game = createGame(board, random);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertTrue(board.isGameOver());
        assertTrue(game.getLastComputerMove().exit());
        assertEquals(VehicleOwner.COMPUTER, board.getWinnerOwner());
        assertEquals("B", board.getWinnerId());
        assertNull(board.getVehicleById("B"));
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
                "B", 2, Orientation.HORIZONTAL,
                4, 0, VehicleOwner.COMPUTER, ExitEdge.LEFT));

        Board simulation = board.copy();

        assertTrue(simulation.exitVehicle("B"));

        assertTrue(simulation.isGameOver());
        assertFalse(board.isGameOver());
        assertNull(board.getWinnerId());

        assertEquals("B",
                board.getVehicleById("B").getId());
    }

    @Test
    void computerShouldNotModifyOtherVehiclesDuringSimulation() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, VehicleOwner.HUMAN, ExitEdge.RIGHT));

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.VERTICAL, 0, 5));

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.HORIZONTAL,
                4, 3, VehicleOwner.COMPUTER, ExitEdge.LEFT));

        GameService game = createGame(board);

        assertTrue(game.humanMove("A", 1));

        Board expected = board.copy();

        assertTrue(game.computerMove());

        GameService.Move move = game.getLastComputerMove();

        assertTrue(expected.moveVehicle(
                move.vehicleId(), move.steps()));

        assertEquals(expected.getVehicles(),
                board.getVehicles());
    }

    @Test
    void computerShouldRespectBlockingVehicle() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, VehicleOwner.HUMAN, ExitEdge.RIGHT));

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.VERTICAL, 3, 1));

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.HORIZONTAL,
                4, 3, VehicleOwner.COMPUTER, ExitEdge.LEFT));

        Random random = mock(Random.class);

        when(random.nextInt(3)).thenReturn(2);
        when(random.nextInt(2)).thenReturn(0);

        GameService game = createGame(board, random);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertEquals("B",
                game.getLastComputerMove().vehicleId());

        assertEquals(2,
                board.getVehicleById("B").getColumn());

        assertFalse(board.isGameOver());
    }

    @Test
    void shouldRejectMovesAfterGameOver() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 4, VehicleOwner.HUMAN, ExitEdge.RIGHT));

        GameService game = createGame(board);

        assertTrue(game.humanExit());
        assertFalse(game.humanMove("A", -1));
        assertFalse(game.computerMove());
        assertFalse(game.humanExit());
    }

    @Test
    void givenOnlyOneMovablePiece_whenAiMoves_thenThatPieceMoves() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        board.addVehicle(new Vehicle(
                "D", 6, Orientation.HORIZONTAL, 0, 0));

        Random random = mock(Random.class);

        when(random.nextInt(2)).thenReturn(1);
        when(random.nextInt(1)).thenReturn(0);
        when(random.nextInt(4)).thenReturn(0);

        GameService game = createGame(board, random);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());

        assertEquals("A",
                game.getLastComputerMove().vehicleId());

        assertEquals(0,
                board.getVehicleById("D").getColumn());

        verify(random).nextInt(2);
        verify(random).nextInt(1);

        assertTrue(game.isHumanTurn());
    }

    @Test
    void givenSameSeed_whenAiMoves_thenSameMoveIsSelected() {
        Board firstBoard = new Board(6, 6);

        firstBoard.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0));

        firstBoard.addVehicle(new Vehicle(
                "D", 2, Orientation.VERTICAL, 0, 5));

        Board secondBoard = firstBoard.copy();

        GameService firstGame =
                createGame(firstBoard, new Random(42));

        GameService secondGame =
                createGame(secondBoard, new Random(42));

        assertTrue(firstGame.humanMove("A", 1));
        assertTrue(secondGame.humanMove("A", 1));

        assertTrue(firstGame.computerMove());
        assertTrue(secondGame.computerMove());

        assertEquals(firstGame.getLastComputerMove(),
                secondGame.getLastComputerMove());
    }

    @Test
    void givenNoLegalMoves_whenAiMoves_thenTurnEndsCleanly() {
        Board board = mock(Board.class);

        Vehicle blocked = new Vehicle(
                "D", 6, Orientation.HORIZONTAL, 0, 0);

        Board simulation = new Board(6, 6);
        simulation.addVehicle(blocked);

        when(board.getRows()).thenReturn(6);
        when(board.getColumns()).thenReturn(6);

        when(board.getVehicles())
                .thenReturn(java.util.List.of(blocked));

        when(board.copy()).thenReturn(simulation);
        when(board.moveVehicle("A", 1)).thenReturn(true);

        GameService game = createGame(board);

        assertTrue(game.humanMove("A", 1));
        assertFalse(game.computerMove());

        assertNull(game.getLastComputerMove());
        assertTrue(game.isHumanTurn());
    }

    @Test
    void humanShouldRejectWrongExitEdge() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "X", 2, Orientation.HORIZONTAL,
                2, 0, VehicleOwner.HUMAN, ExitEdge.RIGHT));

        GameService game = createGame(board);

        assertFalse(game.humanExit());
        assertTrue(game.isHumanTurn());
        assertFalse(board.isGameOver());
    }

    @Test
    void computerMayMoveInsteadOfExiting() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, VehicleOwner.HUMAN, ExitEdge.RIGHT));
        board.addVehicle(new Vehicle(
                "B", 2, Orientation.HORIZONTAL,
                4, 0, VehicleOwner.COMPUTER, ExitEdge.LEFT));

        Random random = mock(Random.class);
        when(random.nextInt(2)).thenReturn(1);
        when(random.nextInt(5)).thenReturn(0);
        GameService game = createGame(board, random);

        assertTrue(game.humanMove("A", 1));
        assertTrue(game.computerMove());
        assertFalse(game.getLastComputerMove().exit());
        assertEquals(1, board.getVehicleById("B").getColumn());
        assertFalse(board.isGameOver());
        assertTrue(game.isHumanTurn());
    }
}
