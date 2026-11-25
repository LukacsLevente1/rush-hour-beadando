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
        Vehicle vehicleA = board.getVehicleById("A");

        assertNotNull(vehicleA);
        assertEquals(2, vehicleA.getLength());
        assertEquals(Orientation.HORIZONTAL, vehicleA.getOrientation());
        assertEquals(2, vehicleA.getRow());
        assertEquals(0, vehicleA.getColumn());
        assertTrue(vehicleA.isTarget());
        assertEquals(VehicleOwner.HUMAN, vehicleA.getOwner());
        assertEquals(ExitEdge.RIGHT, vehicleA.getExitEdge());
    }

    @Test
    void shouldLoadComputerTargetVehicle() {
        Board board = loader.loadLevel("level1.xml");
        Vehicle vehicleB = board.getVehicleById("B");

        assertNotNull(vehicleB);
        assertEquals(2, vehicleB.getLength());
        assertEquals(Orientation.HORIZONTAL, vehicleB.getOrientation());
        assertEquals(4, vehicleB.getRow());
        assertEquals(2, vehicleB.getColumn());
        assertTrue(vehicleB.isTarget());
        assertEquals(VehicleOwner.COMPUTER, vehicleB.getOwner());
        assertEquals(ExitEdge.LEFT, vehicleB.getExitEdge());
    }

    @Test
    void shouldLoadNeutralVehicle() {
        Board board = loader.loadLevel("level1.xml");
        Vehicle vehicleC = board.getVehicleById("C");

        assertNotNull(vehicleC);
        assertEquals(3, vehicleC.getLength());
        assertEquals(Orientation.VERTICAL, vehicleC.getOrientation());
        assertEquals(0, vehicleC.getRow());
        assertEquals(4, vehicleC.getColumn());
        assertFalse(vehicleC.isTarget());
        assertEquals(VehicleOwner.NEUTRAL, vehicleC.getOwner());
        assertEquals(ExitEdge.NONE, vehicleC.getExitEdge());
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

    @Test
    void shouldLoadSameBoardFromAllFormats() {
        Board xml = loader.loadLevel("level1.xml");
        Board txt = new LevelFile().loadLevel("level1.txt");
        Board json = new LevelJsonFile().loadLevel("level1.json");

        assertEquals(txt, xml);
        assertEquals(json, xml);
    }

    @Test
    void shouldPreserveDefaultVehicleOwnersAndExitEdges() {
        Board board = loader.loadLevel("missing.xml");

        Vehicle a = board.getVehicleById("A");
        Vehicle b = board.getVehicleById("B");
        Vehicle c = board.getVehicleById("C");

        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertEquals(VehicleOwner.HUMAN, a.getOwner());
        assertEquals(ExitEdge.RIGHT, a.getExitEdge());
        assertEquals(VehicleOwner.COMPUTER, b.getOwner());
        assertEquals(ExitEdge.LEFT, b.getExitEdge());
        assertEquals(VehicleOwner.NEUTRAL, c.getOwner());
        assertEquals(ExitEdge.NONE, c.getExitEdge());
    }
}
