package hu.nye.progtech.rushhour.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ScoreRepository {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScoreRepository.class);

    private final String dbUrl;

    public ScoreRepository() {
        this("jdbc:sqlite:rushhour.db");
    }

    public ScoreRepository(String dbUrl) {
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "Az adatbazis cime nem lehet ures.");
        }

        this.dbUrl = dbUrl;
        createTable();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS scores (
                    name TEXT PRIMARY KEY,
                    wins INTEGER NOT NULL DEFAULT 0
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);

            LOGGER.info("Ranglista adatbazis inicializalva.");

        } catch (SQLException e) {
            LOGGER.error("Adatbazis letrehozasi hiba.", e);

            throw new IllegalStateException(
                    "Nem sikerult letrehozni az adatbazist.", e);
        }
    }

    public void addWin(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException(
                    "A jatekos neve nem lehet ures.");
        }

        String sql = """
                INSERT INTO scores (name, wins)
                VALUES (?, 1)
                ON CONFLICT(name)
                DO UPDATE SET wins = wins + 1
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, playerName);
            statement.executeUpdate();

            LOGGER.info("Gyozelem elmentve: {}", playerName);

        } catch (SQLException e) {
            LOGGER.error("Gyozelem mentese sikertelen.", e);

            throw new IllegalStateException(
                    "Nem sikerult elmenteni a gyozelmet.", e);
        }
    }

    public int getWins(String playerName) {
        String sql = "SELECT wins FROM scores WHERE name = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, playerName);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("wins");
                }

                return 0;
            }

        } catch (SQLException e) {
            LOGGER.error("Eredmeny lekerdezesi hiba.", e);

            throw new IllegalStateException(
                    "Nem sikerult lekerdezni az eredmenyt.", e);
        }
    }

    public List<ScoreEntry> getLeaderboard() {
        String sql = """
                SELECT name, wins
                FROM scores
                ORDER BY wins DESC, name ASC
                """;

        List<ScoreEntry> leaderboard = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                leaderboard.add(new ScoreEntry(
                        result.getString("name"),
                        result.getInt("wins")));
            }

            LOGGER.debug("Ranglista lekerdezve. Elemek: {}",
                    leaderboard.size());

            return List.copyOf(leaderboard);

        } catch (SQLException e) {
            LOGGER.error("Ranglista lekerdezesi hiba.", e);

            throw new IllegalStateException(
                    "Nem sikerult lekerdezni a ranglistat.", e);
        }
    }

    public record ScoreEntry(String name, int wins) {
    }
}