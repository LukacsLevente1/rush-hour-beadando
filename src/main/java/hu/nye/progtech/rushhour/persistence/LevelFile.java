
package hu.nye.progtech.rushhour.persistence;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LevelFile {

    public Board loadLevel(String fileName) {
        try {
            return readLevel(fileName);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(
                    "Nem sikerult betolteni a palyat: "
                            + e.getMessage());
            System.out.println(
                    "Alapertelmezett palya betoltese...");
            return createDefaultBoard();
        }
    }

    private Board readLevel(String fileName) throws IOException {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Hianyzik a palya neve.");
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

            String firstLine = reader.readLine();

            if (firstLine == null) {
                throw new IllegalArgumentException(
                        "Ures palyafajl.");
            }

            String[] dimensions = firstLine.trim().split("\\s+");

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

                String[] parts = line.trim().split("\\s+");

                if (parts.length != 6) {
                    throw new IllegalArgumentException(
                            "Hibas jarmu sor: " + line);
                }

                String id = parts[0];
                int length = Integer.parseInt(parts[1]);
                Orientation orientation =
                        Orientation.valueOf(parts[2]);
                int row = Integer.parseInt(parts[3]);
                int column = Integer.parseInt(parts[4]);

                if (!parts[5].equals("true")
                        && !parts[5].equals("false")) {
                    throw new IllegalArgumentException(
                            "Hibas celjarmu ertek.");
                }

                boolean target = Boolean.parseBoolean(parts[5]);

                board.addVehicle(new Vehicle(
                        id, length, orientation,
                        row, column, target));
            }

            return board;
        }
    }

    public Board createDefaultBoard() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0, true));

        board.addVehicle(new Vehicle(
                "B", 3, Orientation.VERTICAL, 0, 4, false));

        board.addVehicle(new Vehicle(
                "C", 2, Orientation.HORIZONTAL, 4, 2, true));

        return board;
    }
}
