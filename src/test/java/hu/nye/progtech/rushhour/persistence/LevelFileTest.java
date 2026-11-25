package hu.nye.progtech.rushhour.persistence;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.ExitEdge;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.model.VehicleOwner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelFileTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadLevelFromTxt() {
        Board board = new LevelFile().loadLevel("level1.txt");
        assertEquals(6, board.getRows());
        assertEquals(6, board.getColumns());
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldLoadTargetVehicles() {
        Board board = new LevelFile().loadLevel("level1.txt");
        assertTrue(board.getVehicleById("A").isTarget());
        assertTrue(board.getVehicleById("B").isTarget());
    }

    @Test
    void shouldUseDefaultBoardWhenFileMissing() {
        Board board = new LevelFile().loadLevel("missing.txt");
        assertNotNull(board);
        assertEquals(6, board.getRows());
        assertEquals(6, board.getColumns());
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldUseDefaultBoardForEmptyFileName() {
        Board board = new LevelFile().loadLevel("");
        assertNotNull(board);
        assertEquals(3, board.getVehicles().size());
    }

    @Test
    void shouldSaveBoardToTxt() throws IOException {
        LevelFile levelFile = new LevelFile();
        Board board = levelFile.createDefaultBoard();
        Path file = tempDir.resolve("save.txt");
        levelFile.saveLevel(board, file);
        assertTrue(Files.exists(file));
        assertTrue(Files.size(file) > 0);
    }

    @Test
    void shouldRestoreSavedBoard() throws IOException {
        LevelFile levelFile = new LevelFile();
        Board original = levelFile.createDefaultBoard();
        Path file = tempDir.resolve("save.txt");
        levelFile.saveLevel(original, file);
        Board restored = levelFile.loadFromPath(file);
        assertEquals(original.getRows(), restored.getRows());
        assertEquals(original.getColumns(), restored.getColumns());
        assertEquals(original.getVehicles(), restored.getVehicles());
    }

    @Test
    void shouldPreserveMovedVehiclePositions() throws IOException {
        LevelFile levelFile = new LevelFile();
        Board board = levelFile.createDefaultBoard();
        assertTrue(board.moveVehicle("A", 1));
        assertTrue(board.moveVehicle("B", -1));
        Path file = tempDir.resolve("moved.txt");
        levelFile.saveLevel(board, file);
        Board restored = levelFile.loadFromPath(file);
        assertEquals(1, restored.getVehicleById("A").getColumn());
        assertEquals(1, restored.getVehicleById("B").getColumn());
        assertEquals(board.getVehicles(), restored.getVehicles());
    }

    @Test
    void shouldIgnoreLeadingBlankLines() throws IOException {
        LevelFile levelFile = new LevelFile();
        Path file = tempDir.resolve("blank-lines.txt");
        Files.writeString(file, "\n\n   \n"
                + "6 6\n"
                + "A 2 HORIZONTAL 2 0 true\n"
                + "C 2 HORIZONTAL 4 2 true\n");
        Board board = levelFile.loadFromPath(file);
        assertEquals(6, board.getRows());
        assertEquals(6, board.getColumns());
        assertEquals(2, board.getVehicles().size());
    }

    @Test
    void shouldLoadLevelFromCustomPath() throws IOException {
        LevelFile levelFile = new LevelFile();
        Path file = tempDir.resolve("custom-level.txt");
        Files.writeString(file, "8 10\n"
                + "A 2 HORIZONTAL 2 0 HUMAN RIGHT\n"
                + "B 2 HORIZONTAL 4 2 COMPUTER LEFT\n");
        Board board = levelFile.loadLevel(file.toString());
        assertEquals(8, board.getRows());
        assertEquals(10, board.getColumns());
        assertEquals(2, board.getVehicles().size());
    }

    @Test
    void shouldCreateMissingSaveDirectories() throws IOException {
        LevelFile levelFile = new LevelFile();
        Board board = levelFile.createDefaultBoard();
        Path file = tempDir.resolve("saves")
                .resolve("games").resolve("save.txt");
        levelFile.saveLevel(board, file.toString());
        assertTrue(Files.exists(file));
    }

    @Test
    void shouldRejectEmptyLevelFile() throws IOException {
        LevelFile levelFile = new LevelFile();
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "\n\n   \n");
        assertThrows(IllegalArgumentException.class,
                () -> levelFile.loadFromPath(file));
    }

    @Test
    void shouldRejectInvalidBoardDimensions() throws IOException {
        LevelFile levelFile = new LevelFile();
        Path file = tempDir.resolve("invalid-size.txt");
        Files.writeString(file, "0 6\n");
        assertThrows(IllegalArgumentException.class,
                () -> levelFile.loadFromPath(file));
    }

    @Test
    void shouldRejectInvalidVehicleData() throws IOException {
        LevelFile levelFile = new LevelFile();
        Path file = tempDir.resolve("invalid-vehicle.txt");
        Files.writeString(file, "6 6\n"
                + "A 2 HORIZONTAL 2 0 invalid\n");
        assertThrows(IllegalArgumentException.class,
                () -> levelFile.loadFromPath(file));
    }

    @Test
    void shouldRejectSavingFinishedGame() {
        LevelFile levelFile = new LevelFile();
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle("A", 2, Orientation.HORIZONTAL,
                2, 4, VehicleOwner.HUMAN, ExitEdge.RIGHT));
        assertTrue(board.exitVehicle("A"));
        Path file = tempDir.resolve("finished.txt");
        assertThrows(IllegalArgumentException.class,
                () -> levelFile.saveLevel(board, file));
    }

    @Test
    void shouldLoadOwnersAndExitEdgesFromTxt() throws IOException {
        LevelFile levelFile = new LevelFile();
        Path file = tempDir.resolve("owners.txt");
        Files.writeString(file, "6 6\n"
                + "A 2 HORIZONTAL 2 0 HUMAN RIGHT\n"
                + "C 3 VERTICAL 0 4 NEUTRAL NONE\n"
                + "B 2 HORIZONTAL 4 2 COMPUTER LEFT\n");
        Board board = levelFile.loadFromPath(file);
        assertEquals(VehicleOwner.HUMAN, board.getVehicleById("A").getOwner());
        assertEquals(ExitEdge.RIGHT, board.getVehicleById("A").getExitEdge());
        assertEquals(VehicleOwner.COMPUTER, board.getVehicleById("B").getOwner());
        assertEquals(ExitEdge.LEFT, board.getVehicleById("B").getExitEdge());
        assertEquals(VehicleOwner.NEUTRAL, board.getVehicleById("C").getOwner());
        assertEquals(ExitEdge.NONE, board.getVehicleById("C").getExitEdge());
    }

    @Test
    void shouldPreserveCustomOwnerAndExitEdgeWhenSaving() throws IOException {
        LevelFile levelFile = new LevelFile();
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle("X", 2, Orientation.HORIZONTAL,
                2, 1, VehicleOwner.HUMAN, ExitEdge.LEFT));
        Path file = tempDir.resolve("custom-save.txt");
        levelFile.saveLevel(board, file);
        Vehicle restored = levelFile.loadFromPath(file).getVehicleById("X");
        assertEquals(VehicleOwner.HUMAN, restored.getOwner());
        assertEquals(ExitEdge.LEFT, restored.getExitEdge());
    }
}
