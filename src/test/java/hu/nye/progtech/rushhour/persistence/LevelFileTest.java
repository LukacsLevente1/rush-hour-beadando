
package hu.nye.progtech.rushhour.persistence;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelFileTest {

    @Test
    void shouldLoadLevelFromTxt() {
        LevelFile levelFile = new LevelFile();

        Board board = levelFile.loadLevel("level1.txt");

        assertEquals(6, board.getRows());
        assertEquals(6, board.getColumns());
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldLoadTargetVehicles() {
        LevelFile levelFile = new LevelFile();

        Board board = levelFile.loadLevel("level1.txt");

        Vehicle vehicleA = board.getVehicles().stream()
                .filter(v -> v.getId().equals("A"))
                .findFirst()
                .orElseThrow();

        assertTrue(vehicleA.isTarget());
    }

    @Test
    void shouldUseDefaultBoardWhenFileMissing() {
        LevelFile levelFile = new LevelFile();

        Board board = levelFile.loadLevel("missing.txt");

        assertNotNull(board);
        assertEquals(6, board.getRows());
        assertEquals(6, board.getColumns());
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldUseDefaultBoardForEmptyFileName() {
        LevelFile levelFile = new LevelFile();

        Board board = levelFile.loadLevel("");

        assertNotNull(board);
        assertEquals(3, board.getVehicles().size());
    }
}
