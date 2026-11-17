
package hu.nye.progtech.rushhour.ui;

import hu.nye.progtech.rushhour.persistence.LevelFile;
import hu.nye.progtech.rushhour.persistence.ScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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

        ConsoleUI ui = new ConsoleUI(
                input,
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new LevelFile(),
                scores);

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
        String result = runGame("Anna\n1\nrossz\nquit\n");

        assertTrue(result.contains("Hibas parancs!"));
    }

    @Test
    void shouldRejectInvalidStepNumber() {
        String result = runGame("Anna\n1\nA alma\nquit\n");

        assertTrue(result.contains("Hibas lepesszam!"));
    }

    @Test
    void shouldRejectIllegalMove() {
        String result = runGame("Anna\n1\nA -10\nquit\n");

        assertTrue(result.contains("Szabalytalan lepes!"));
    }

    @Test
    void shouldShowLeaderboard() {
        String result = runGame("Anna\n1\nscores\nquit\n");

        assertTrue(result.contains("Udvozollek, Anna!"));
        assertTrue(result.contains("Jatek bezarva."));
    }

    @Test
    void shouldRejectEarlyExit() {
        String result = runGame("Anna\n1\nexit A\nquit\n");

        assertTrue(result.contains(
                "Az A auto meg nincs a kijaratnal."));
    }

    @Test
    void shouldHandleSuccessfulMove() {
        String result = runGame("Anna\n1\nA 1\nquit\n");

        assertTrue(result.contains("Sikeres lepes!"));
        assertTrue(result.contains("A gep kovetkezik..."));
    }

    @Test
    void shouldHandleMissingInput() {
        String result = runGame("");

        assertTrue(result.contains("Add meg a neved:"));
    }

    @Test
    void shouldRejectNullDependencies() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConsoleUI(
                        null,
                        System.out,
                        new LevelFile(),
                        new ScoreRepository(
                                "jdbc:sqlite:"
                                        + tempDir.resolve("null-test.db")
                                        .toAbsolutePath())));
    }
}
