package hu.nye.progtech.rushhour.persistence;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        Vehicle vehicle = board.getVehicles().stream()
                .filter(v -> v.getId().equals("A"))
                .findFirst()
                .orElseThrow();

        assertEquals(2, vehicle.getLength());
        assertEquals(Orientation.HORIZONTAL,
                vehicle.getOrientation());
        assertEquals(2, vehicle.getRow());
        assertEquals(0, vehicle.getColumn());
        assertTrue(vehicle.isTarget());
    }

    @Test
    void shouldLoadVehicleB() {
        Board board = loader.loadLevel("level1.json");

        Vehicle vehicle = board.getVehicles().stream()
                .filter(v -> v.getId().equals("B"))
                .findFirst()
                .orElseThrow();

        assertEquals(3, vehicle.getLength());
        assertEquals(Orientation.VERTICAL,
                vehicle.getOrientation());
        assertEquals(0, vehicle.getRow());
        assertEquals(4, vehicle.getColumn());
        assertFalse(vehicle.isTarget());
    }

    @Test
    void shouldLoadVehicleC() {
        Board board = loader.loadLevel("level1.json");

        Vehicle vehicle = board.getVehicles().stream()
                .filter(v -> v.getId().equals("C"))
                .findFirst()
                .orElseThrow();

        assertEquals(2, vehicle.getLength());
        assertEquals(Orientation.HORIZONTAL,
                vehicle.getOrientation());
        assertEquals(4, vehicle.getRow());
        assertEquals(2, vehicle.getColumn());
        assertTrue(vehicle.isTarget());
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
}
