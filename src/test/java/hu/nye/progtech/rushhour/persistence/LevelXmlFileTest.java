package hu.nye.progtech.rushhour.persistence;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelXmlFileTest {

    private final LevelXmlFile loader = new LevelXmlFile();

    @Test
    void shouldLoadXmlLevel() {
        Board board = loader.loadLevel("level1.xml");

        assertNotNull(board);
        assertEquals(6, board.getRows());
        assertEquals(6, board.getColumns());
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldLoadVehiclePositions() {
        Board board = loader.loadLevel("level1.xml");

        Vehicle vehicleA = board.getVehicles().stream()
                .filter(v -> v.getId().equals("A"))
                .findFirst()
                .orElseThrow();

        assertEquals(2, vehicleA.getLength());
        assertEquals(Orientation.HORIZONTAL,
                vehicleA.getOrientation());
        assertEquals(2, vehicleA.getRow());
        assertEquals(0, vehicleA.getColumn());
        assertTrue(vehicleA.isTarget());
    }

    @Test
    void shouldLoadComputerTargetVehicle() {
        Board board = loader.loadLevel("level1.xml");

        Vehicle vehicleC = board.getVehicles().stream()
                .filter(v -> v.getId().equals("C"))
                .findFirst()
                .orElseThrow();

        assertEquals(4, vehicleC.getRow());
        assertEquals(2, vehicleC.getColumn());
        assertTrue(vehicleC.isTarget());
    }

    @Test
    void shouldUseDefaultBoardWhenFileMissing() {
        Board board = loader.loadLevel("missing.xml");

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
