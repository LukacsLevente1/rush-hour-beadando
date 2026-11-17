
package hu.nye.progtech.rushhour.persistence;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.ui.ConsoleUI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LevelFileMockitoTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadTxtLevelUsingMock() {
        LevelFile mockedLevelFile = mock(LevelFile.class);

        Board board = new Board(6, 6);

        when(mockedLevelFile.loadLevel("level1.txt"))
                .thenReturn(board);

        Board result = mockedLevelFile.loadLevel("level1.txt");

        assertTrue(result == board);

        verify(mockedLevelFile, times(1))
                .loadLevel("level1.txt");
    }

    @Test
    void shouldUseMockedLevelFileInConsoleUI() {
        LevelFile mockedLevelFile = mock(LevelFile.class);

        Board board = new Board(6, 6);

        when(mockedLevelFile.loadLevel("level1.txt"))
                .thenReturn(board);

        String commands = "Anna\n1\nquit\n";

        ByteArrayInputStream input = new ByteArrayInputStream(
                commands.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ScoreRepository scores = new ScoreRepository(
                "jdbc:sqlite:"
                        + tempDir.resolve("mockito-test.db")
                        .toAbsolutePath());

        ConsoleUI ui = new ConsoleUI(
                input,
                new PrintStream(
                        output, true, StandardCharsets.UTF_8),
                mockedLevelFile,
                scores);

        ui.start();

        verify(mockedLevelFile, times(1))
                .loadLevel("level1.txt");

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Udvozollek, Anna!"));
        assertTrue(result.contains("Jatek bezarva."));
    }
}
