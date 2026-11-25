package hu.nye.progtech.rushhour.persistence;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.ExitEdge;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.model.VehicleOwner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelJsonFileTest {

    private final LevelJsonFile loader = new LevelJsonFile();

    @Test
    void shouldLoadJsonLevel() {
        Board board = loader.loadLevel("level1.json");

        assertNotNull(board);
        assertEquals(6, board.getRows());
        assertEquals(6, board.getColumns());
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldLoadVehicleA() {
        Board board = loader.loadLevel("level1.json");
        Vehicle vehicle = board.getVehicleById("A");

        assertNotNull(vehicle);
        assertEquals(2, vehicle.getLength());
        assertEquals(Orientation.HORIZONTAL, vehicle.getOrientation());
        assertEquals(2, vehicle.getRow());
        assertEquals(0, vehicle.getColumn());
        assertEquals(VehicleOwner.HUMAN, vehicle.getOwner());
        assertEquals(ExitEdge.RIGHT, vehicle.getExitEdge());
        assertTrue(vehicle.isTarget());
    }

    @Test
    void shouldLoadVehicleB() {
        Board board = loader.loadLevel("level1.json");
        Vehicle vehicle = board.getVehicleById("B");

        assertNotNull(vehicle);
        assertEquals(2, vehicle.getLength());
        assertEquals(Orientation.HORIZONTAL, vehicle.getOrientation());
        assertEquals(4, vehicle.getRow());
        assertEquals(2, vehicle.getColumn());
        assertEquals(VehicleOwner.COMPUTER, vehicle.getOwner());
        assertEquals(ExitEdge.LEFT, vehicle.getExitEdge());
        assertTrue(vehicle.isTarget());
    }

    @Test
    void shouldLoadVehicleC() {
        Board board = loader.loadLevel("level1.json");
        Vehicle vehicle = board.getVehicleById("C");

        assertNotNull(vehicle);
        assertEquals(3, vehicle.getLength());
        assertEquals(Orientation.VERTICAL, vehicle.getOrientation());
        assertEquals(0, vehicle.getRow());
        assertEquals(4, vehicle.getColumn());
        assertEquals(VehicleOwner.NEUTRAL, vehicle.getOwner());
        assertEquals(ExitEdge.NONE, vehicle.getExitEdge());
        assertFalse(vehicle.isTarget());
    }

    @Test
    void shouldUseDefaultBoardWhenFileMissing() {
        Board board = loader.loadLevel("missing.json");

        assertNotNull(board);
        assertEquals(6, board.getRows());
        assertEquals(6, board.getColumns());
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldUseDefaultBoardForEmptyFileName() {
        Board board = loader.loadLevel("");

        assertNotNull(board);
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldUseDefaultBoardForNullFileName() {
        Board board = loader.loadLevel(null);

        assertNotNull(board);
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldMatchTxtLevel() {
        Board json = loader.loadLevel("level1.json");
        Board txt = new LevelFile().loadLevel("level1.txt");

        assertEquals(txt, json);
    }

    @Test
    void shouldPreserveOwnersAndExitEdgesInDefaultBoard() {
        Board board = loader.loadLevel("missing.json");

        assertEquals(VehicleOwner.HUMAN,
                board.getVehicleById("A").getOwner());
        assertEquals(ExitEdge.RIGHT,
                board.getVehicleById("A").getExitEdge());
        assertEquals(VehicleOwner.COMPUTER,
                board.getVehicleById("B").getOwner());
        assertEquals(ExitEdge.LEFT,
                board.getVehicleById("B").getExitEdge());
        assertEquals(VehicleOwner.NEUTRAL,
                board.getVehicleById("C").getOwner());
        assertEquals(ExitEdge.NONE,
                board.getVehicleById("C").getExitEdge());
    }
}
