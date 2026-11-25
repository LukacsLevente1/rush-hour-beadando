
package hu.nye.progtech.rushhour.persistence;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.ExitEdge;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.model.VehicleOwner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LevelFile {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LevelFile.class);

    public Board loadLevel(String fileName) {
        try {
            return readLevel(fileName);
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.warn("Nem sikerult betolteni a palyat: {}",
                    e.getMessage());

            LOGGER.info("Alapertelmezett palya betoltese.");

            return createDefaultBoard();
        }
    }

    private Board readLevel(String fileName) throws IOException {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Hianyzik a palya neve.");
        }

        Path path = Path.of(fileName);

        if (Files.isRegularFile(path)) {
            return loadFromPath(path);
        }

        String resourcePath = "levels/" + fileName;

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (input == null) {
            throw new IOException(
                    "Nem talalhato: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        input, StandardCharsets.UTF_8))) {

            Board board = parseBoard(reader);

            LOGGER.info("Palya sikeresen betoltve: {}",
                    resourcePath);

            return board;
        }
    }

    public Board loadFromPath(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException(
                    "Hianyzik a fajl eleresi utja.");
        }

        try (BufferedReader reader = Files.newBufferedReader(
                path, StandardCharsets.UTF_8)) {

            Board board = parseBoard(reader);

            LOGGER.info("Palya sikeresen betoltve: {}", path);

            return board;
        }
    }

    private Board parseBoard(BufferedReader reader)
            throws IOException {

        String firstLine;

        do {
            firstLine = reader.readLine();
        } while (firstLine != null && firstLine.isBlank());

        if (firstLine == null) {
            throw new IllegalArgumentException(
                    "Ures palyafajl.");
        }

        String[] dimensions =
                firstLine.trim().split("\\s+");

        if (dimensions.length != 2) {
            throw new IllegalArgumentException(
                    "Hibas palyameret.");
        }

        int rows = Integer.parseInt(dimensions[0]);
        int columns = Integer.parseInt(dimensions[1]);

        Board board = new Board(rows, columns);

        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }

            board.addVehicle(parseVehicle(line));
        }

        return board;
    }

    private Vehicle parseVehicle(String line) {

        String[] parts = line.trim().split("\\s+");

        if (parts.length != 6 && parts.length != 7) {
            throw new IllegalArgumentException(
                    "Hibas jarmu sor: " + line);
        }

        String id = parts[0];
        int length = Integer.parseInt(parts[1]);

        Orientation orientation =
                Orientation.valueOf(parts[2]);

        int row = Integer.parseInt(parts[3]);
        int column = Integer.parseInt(parts[4]);

        if (parts.length == 7) {

            VehicleOwner owner =
                    VehicleOwner.valueOf(parts[5]);

            ExitEdge exitEdge =
                    ExitEdge.valueOf(parts[6]);

            return new Vehicle(
                    id, length, orientation,
                    row, column, owner, exitEdge);
        }

        if (!parts[5].equals("true")
                && !parts[5].equals("false")) {

            throw new IllegalArgumentException(
                    "Hibas celjarmu ertek.");
        }

        boolean target = Boolean.parseBoolean(parts[5]);

        LOGGER.debug("Regi jarmuformatum: {}", id);

        return new Vehicle(
                id, length, orientation,
                row, column, target);
    }

    public void saveLevel(Board board, String fileName)
            throws IOException {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Hianyzik a mentendo fajl neve.");
        }

        saveLevel(board, Path.of(fileName));
    }

    public void saveLevel(Board board, Path path)
            throws IOException {

        if (board == null || path == null) {
            throw new IllegalArgumentException(
                    "Hibas mentesi adatok.");
        }

        if (board.isGameOver()) {
            throw new IllegalArgumentException(
                    "Befejezett jatek nem mentheto.");
        }

        Path parent = path.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardCharsets.UTF_8)) {

            writer.write(board.getRows()
                    + " " + board.getColumns());

            writer.newLine();

            for (Vehicle vehicle : board.getVehicles()) {

                writer.write(formatVehicle(vehicle));

                writer.newLine();
            }
        }

        LOGGER.info("Jatektabla sikeresen mentve: {}", path);
    }

    private String formatVehicle(Vehicle vehicle) {

        String base = vehicle.getId() + " "
                + vehicle.getLength() + " "
                + vehicle.getOrientation() + " "
                + vehicle.getRow() + " "
                + vehicle.getColumn() + " ";

        if (vehicle.isTarget()
                && vehicle.getOwner() == VehicleOwner.NEUTRAL) {

            return base + vehicle.isTarget();
        }

        return base
                + vehicle.getOwner() + " "
                + vehicle.getExitEdge();
    }

    public Board createDefaultBoard() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, VehicleOwner.HUMAN, ExitEdge.RIGHT));

        board.addVehicle(new Vehicle(
                "C", 3, Orientation.VERTICAL,
                0, 4, VehicleOwner.NEUTRAL, ExitEdge.NONE));

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.HORIZONTAL,
                4, 2, VehicleOwner.COMPUTER, ExitEdge.LEFT));

        return board;
    }
}
