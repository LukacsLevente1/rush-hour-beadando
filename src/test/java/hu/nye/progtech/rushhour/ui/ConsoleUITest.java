package hu.nye.progtech.rushhour.ui;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.persistence.LevelFile;
import hu.nye.progtech.rushhour.persistence.ScoreRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleUITest {
    @TempDir
    Path tempDir;

    private String runGame(String commands) {
        ByteArrayInputStream input = new ByteArrayInputStream(
                commands.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ScoreRepository scores = new ScoreRepository(
                "jdbc:sqlite:" + tempDir.resolve("ui-test.db")
                        .toAbsolutePath());
        ConsoleUI ui = new ConsoleUI(input,
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new LevelFile(), scores);
        ui.start();
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void shouldStartGameAndQuit() {
        String result = runGame("Anna\n1\nquit\n");
        assertTrue(result.contains("=== RUSH HOUR ==="));
        assertTrue(result.contains("Udvozollek, Anna!"));
        assertTrue(result.contains("Jatek bezarva."));
    }

    @Test
    void shouldRejectEmptyName() {
        String result = runGame("\nAnna\n1\nquit\n");
        assertTrue(result.contains("A nev nem lehet ures"));
        assertTrue(result.contains("Udvozollek, Anna!"));
    }

    @Test
    void shouldRejectInvalidCommand() {
        assertTrue(runGame("Anna\n1\nrossz\nquit\n")
                .contains("Hibas parancs!"));
    }

    @Test
    void shouldRejectInvalidStepNumber() {
        assertTrue(runGame("Anna\n1\nA alma\nquit\n")
                .contains("Hibas lepesszam!"));
    }

    @Test
    void shouldRejectIllegalMove() {
        assertTrue(runGame("Anna\n1\nA -10\nquit\n")
                .contains("Szabalytalan lepes!"));
    }

    @Test
    void shouldShowLeaderboard() {
        String result = runGame("Anna\n1\nscores\nquit\n");
        assertTrue(result.contains("Udvozollek, Anna!"));
        assertTrue(result.contains("Jatek bezarva."));
    }

    @Test
    void shouldRejectEarlyExit() {
        String result = runGame("Anna\n1\nexit\nquit\n");
        assertTrue(result.contains("A sajat auto meg nincs a kijaratnal."));
    }

    @Test
    void shouldAcceptLegacyExitCommand() {
        String result = runGame("Anna\n1\nexit A\nquit\n");
        assertTrue(result.contains("A sajat auto meg nincs a kijaratnal."));
    }

    @Test
    void shouldHandleSuccessfulMove() {
        String result = runGame("Anna\n1\nA 1\nquit\n");
        assertTrue(result.contains("Sikeres lepes!"));
        assertTrue(result.contains("A gep kovetkezik..."));
    }

    @Test
    void shouldHandleMissingInput() {
        assertTrue(runGame("").contains("Add meg a neved:"));
    }

    @Test
    void shouldRejectNullDependencies() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConsoleUI(null, System.out, new LevelFile(),
                        new ScoreRepository("jdbc:sqlite:"
                                + tempDir.resolve("null-test.db")
                                .toAbsolutePath())));
    }

    @Test
    void shouldSaveGameUsingCommand() throws IOException {
        Path file = tempDir.resolve("save.txt");
        String result = runGame("Anna\n1\nsave " + file + "\nquit\n");
        assertTrue(result.contains("Jatek sikeresen mentve:"));
        assertTrue(Files.exists(file));
        Board saved = new LevelFile().loadFromPath(file);
        assertEquals(6, saved.getRows());
        assertEquals(6, saved.getColumns());
        assertEquals(3, saved.getVehicles().size());
    }

    @Test
    void shouldSaveCurrentVehiclePositions() throws IOException {
        Path file = tempDir.resolve("moved.txt");
        Path restoredFile = tempDir.resolve("restored.txt");
        String result = runGame("Anna\n1\nA 1\nsave " + file
                + "\nload " + file + "\nsave " + restoredFile + "\nquit\n");
        assertTrue(result.contains("Sikeres lepes!"));
        assertTrue(result.contains("Jatek sikeresen mentve:"));
        assertTrue(result.contains("Jatek sikeresen betoltve:"));
        assertTrue(Files.exists(file));
        assertTrue(Files.exists(restoredFile));
        LevelFile levelFile = new LevelFile();
        Board saved = levelFile.loadFromPath(file);
        Board restored = levelFile.loadFromPath(restoredFile);
        assertEquals(6, saved.getRows());
        assertEquals(6, saved.getColumns());
        assertEquals(3, saved.getVehicles().size());
        assertEquals(saved.getVehicles(), restored.getVehicles());
    }

    @Test
    void shouldLoadSavedGameUsingCommand() throws IOException {
        Path file = tempDir.resolve("load.txt");
        Path restoredFile = tempDir.resolve("restored.txt");
        LevelFile levelFile = new LevelFile();
        Board board = levelFile.createDefaultBoard();
        assertTrue(board.moveVehicle("A", 1));
        levelFile.saveLevel(board, file);
        String result = runGame("Anna\n1\nload " + file
                + "\nsave " + restoredFile + "\nquit\n");
        assertTrue(result.contains("Jatek sikeresen betoltve:"));
        Board restored = levelFile.loadFromPath(restoredFile);
        assertEquals(board.getVehicles(), restored.getVehicles());
    }

    @Test
    void shouldRejectSaveWithoutFileName() {
        assertTrue(runGame("Anna\n1\nsave\nquit\n")
                .contains("Hasznalat: save mentes.txt"));
    }

    @Test
    void shouldRejectLoadWithoutFileName() {
        assertTrue(runGame("Anna\n1\nload\nquit\n")
                .contains("Hasznalat: load mentes.txt"));
    }

    @Test
    void shouldHandleMissingSaveFile() {
        Path file = tempDir.resolve("missing.txt");
        String result = runGame("Anna\n1\nload " + file + "\nquit\n");
        assertTrue(result.contains("Betoltesi hiba:"));
        assertTrue(result.contains("Jatek bezarva."));
    }

    @Test
    void shouldMoveVehicleUsingCoordinates() {
        String result = runGame("Anna\n1\na3 right 1\nquit\n");
        assertTrue(result.contains("Sikeres lepes!"));
        assertTrue(result.contains("A gep kovetkezik..."));
    }

    @Test
    void shouldAcceptHungarianDirection() {
        assertTrue(runGame("Anna\n1\na3 jobb 1\nquit\n")
                .contains("Sikeres lepes!"));
    }

    @Test
    void shouldRejectInvalidCoordinate() {
        assertTrue(runGame("Anna\n1\nz0 right 1\nquit\n")
                .contains("Hibas koordinata!"));
    }

    @Test
    void shouldRejectEmptyCoordinate() {
        assertTrue(runGame("Anna\n1\na1 right 1\nquit\n")
                .contains("Nincs jarmu a megadott helyen!"));
    }

    @Test
    void shouldRejectWrongMovementDirection() {
        assertTrue(runGame("Anna\n1\na3 up 1\nquit\n")
                .contains("Hibas mozgasi irany!"));
    }

    @Test
    void shouldRejectUnknownDirection() {
        assertTrue(runGame("Anna\n1\na3 diagonal 1\nquit\n")
                .contains("Ismeretlen mozgasi irany!"));
    }

    @Test
    void shouldLoadCustomLevel() throws IOException {
        Path file = tempDir.resolve("custom.txt");
        Path savedFile = tempDir.resolve("custom-saved.txt");
        Files.writeString(file, "8 10\n"
                + "A 2 HORIZONTAL 2 0 HUMAN RIGHT\n"
                + "B 2 HORIZONTAL 4 2 COMPUTER LEFT\n");
        String result = runGame("Anna\n4\n" + file
                + "\nsave " + savedFile + "\nquit\n");
        assertTrue(result.contains("Add meg a fajl eleresi utjat:"));
        assertTrue(result.contains("Jatek sikeresen mentve:"));
        Board saved = new LevelFile().loadFromPath(savedFile);
        assertEquals(8, saved.getRows());
        assertEquals(10, saved.getColumns());
        assertEquals(2, saved.getVehicles().size());
    }

    @Test
    void shouldShowHelp() {
        String result = runGame("Anna\n1\nhelp\nquit\n");
        String helpText = "Koordinatas lepes: a3 right 2";
        int first = result.indexOf(helpText);
        int last = result.lastIndexOf(helpText);
        assertTrue(first >= 0);
        assertTrue(last > first);
        assertTrue(result.contains("Mentes: save mentes.txt"));
        assertTrue(result.contains("Betoltes: load mentes.txt"));
    }
}
