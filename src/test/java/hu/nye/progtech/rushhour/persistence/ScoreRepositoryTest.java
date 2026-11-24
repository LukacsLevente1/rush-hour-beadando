package hu.nye.progtech.rushhour.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void shouldReturnLeaderboard() {
        ScoreRepository repository = createRepository();

        repository.addWin("Anna");
        repository.addWin("Bela");

        List<ScoreRepository.ScoreEntry> leaderboard =
                repository.getLeaderboard();

        assertEquals(2, leaderboard.size());
        assertEquals("Anna", leaderboard.get(0).name());
        assertEquals(1, leaderboard.get(0).wins());
        assertEquals("Bela", leaderboard.get(1).name());
        assertEquals(1, leaderboard.get(1).wins());
    }

    @Test
    void shouldReturnEmptyLeaderboard() {
        ScoreRepository repository = createRepository();

        assertTrue(repository.getLeaderboard().isEmpty());
    }

    @Test
    void shouldSortLeaderboardByWins() {
        ScoreRepository repository = createRepository();

        repository.addWin("Anna");

        repository.addWin("Bela");
        repository.addWin("Bela");
        repository.addWin("Bela");

        repository.addWin("Peter");
        repository.addWin("Peter");

        List<ScoreRepository.ScoreEntry> leaderboard =
                repository.getLeaderboard();

        assertEquals(3, leaderboard.size());

        assertEquals("Bela", leaderboard.get(0).name());
        assertEquals(3, leaderboard.get(0).wins());

        assertEquals("Peter", leaderboard.get(1).name());
        assertEquals(2, leaderboard.get(1).wins());

        assertEquals("Anna", leaderboard.get(2).name());
        assertEquals(1, leaderboard.get(2).wins());
    }

    @Test
    void shouldSortEqualWinsAlphabetically() {
        ScoreRepository repository = createRepository();

        repository.addWin("Peter");
        repository.addWin("Bela");
        repository.addWin("Anna");

        List<ScoreRepository.ScoreEntry> leaderboard =
                repository.getLeaderboard();

        assertEquals("Anna", leaderboard.get(0).name());
        assertEquals("Bela", leaderboard.get(1).name());
        assertEquals("Peter", leaderboard.get(2).name());
    }

    @Test
    void shouldReturnImmutableLeaderboard() {
        ScoreRepository repository = createRepository();

        repository.addWin("Anna");

        List<ScoreRepository.ScoreEntry> leaderboard =
                repository.getLeaderboard();

        assertThrows(UnsupportedOperationException.class,
                () -> leaderboard.add(
                        new ScoreRepository.ScoreEntry("Bela", 2)));
    }

    @Test
    void shouldRejectNullPlayerName() {
        ScoreRepository repository = createRepository();

        assertThrows(IllegalArgumentException.class,
                () -> repository.addWin(null));
    }

    @Test
    void shouldRejectBlankPlayerName() {
        ScoreRepository repository = createRepository();

        assertThrows(IllegalArgumentException.class,
                () -> repository.addWin("   "));
    }

    @Test
    void shouldRejectNullDatabaseUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> new ScoreRepository(null));
    }

    @Test
    void shouldKeepLeaderboardAfterReopeningDatabase() {
        ScoreRepository firstRepository = createRepository();

        firstRepository.addWin("Anna");
        firstRepository.addWin("Anna");
        firstRepository.addWin("Bela");

        ScoreRepository secondRepository = createRepository();

        assertEquals(firstRepository.getLeaderboard(),
                secondRepository.getLeaderboard());
    }
}