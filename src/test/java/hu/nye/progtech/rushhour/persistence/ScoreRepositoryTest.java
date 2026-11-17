
package hu.nye.progtech.rushhour.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ScoreRepositoryTest {

    @TempDir
    Path tempDir;

    private ScoreRepository createRepository() {
        Path database = tempDir.resolve("test-scores.db");

        return new ScoreRepository(
                "jdbc:sqlite:" + database.toAbsolutePath());
    }

    @Test
    void shouldStartWithZeroWins() {
        ScoreRepository repository = createRepository();

        assertEquals(0, repository.getWins("Anna"));
    }

    @Test
    void shouldSaveFirstWin() {
        ScoreRepository repository = createRepository();

        repository.addWin("Anna");

        assertEquals(1, repository.getWins("Anna"));
    }

    @Test
    void shouldIncreaseWins() {
        ScoreRepository repository = createRepository();

        repository.addWin("Anna");
        repository.addWin("Anna");
        repository.addWin("Anna");

        assertEquals(3, repository.getWins("Anna"));
    }

    @Test
    void shouldStorePlayersSeparately() {
        ScoreRepository repository = createRepository();

        repository.addWin("Anna");
        repository.addWin("Anna");
        repository.addWin("Bela");

        assertEquals(2, repository.getWins("Anna"));
        assertEquals(1, repository.getWins("Bela"));
    }

    @Test
    void shouldKeepResultsAfterReopeningDatabase() {
        ScoreRepository firstRepository = createRepository();
        firstRepository.addWin("Anna");

        ScoreRepository secondRepository = createRepository();

        assertEquals(1, secondRepository.getWins("Anna"));
    }

    @Test
    void shouldRejectEmptyPlayerName() {
        ScoreRepository repository = createRepository();

        assertThrows(IllegalArgumentException.class,
                () -> repository.addWin(""));
    }

    @Test
    void shouldRejectInvalidDatabaseUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> new ScoreRepository(""));
    }

    @Test
    void shouldPrintLeaderboard() {
        ScoreRepository repository = createRepository();

        repository.addWin("Anna");
        repository.addWin("Bela");

        assertDoesNotThrow(repository::printLeaderboard);
    }
}
