package hu.nye.progtech.rushhour.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.ExitEdge;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.model.VehicleOwner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class LevelJsonFile {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LevelJsonFile.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Board loadLevel(String fileName) {
        try {
            Board board = readLevel(fileName);

            LOGGER.info("JSON palya sikeresen betoltve: {}",
                    fileName);

            return board;

        } catch (IOException | IllegalArgumentException e) {

            LOGGER.warn("JSON palya betoltese sikertelen: {}",
                    e.getMessage());

            LOGGER.info("Alapertelmezett palya betoltese.");

            return new LevelFile().createDefaultBoard();
        }
    }

    private Board readLevel(String fileName) throws IOException {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Hianyzik a JSON fajl neve.");
        }

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("levels/" + fileName);

        if (input == null) {
            throw new IOException(
                    "Nem talalhato: levels/" + fileName);
        }

        try (InputStream stream = input) {
            JsonNode root = objectMapper.readTree(stream);

            if (root == null || !root.isObject()) {
                throw new IllegalArgumentException(
                        "Hibas JSON gyoker elem.");
            }

            int rows = getRequiredInt(root, "rows");
            int columns = getRequiredInt(root, "columns");

            Board board = new Board(rows, columns);

            JsonNode vehicles = root.get("vehicles");

            if (vehicles == null || !vehicles.isArray()) {
                throw new IllegalArgumentException(
                        "A vehicles mezonek tombnek kell lennie.");
            }

            for (JsonNode node : vehicles) {
                if (!node.isObject()) {
                    throw new IllegalArgumentException(
                            "Hibas jarmu adat.");
                }

                String id = getRequiredText(node, "id");
                int length = getRequiredInt(node, "length");

                Orientation orientation = Orientation.valueOf(
                        getRequiredText(node, "orientation"));

                int row = getRequiredInt(node, "row");
                int column = getRequiredInt(node, "column");

                Vehicle vehicle;

                if (node.has("owner") || node.has("exitEdge")) {

                    VehicleOwner owner = VehicleOwner.valueOf(
                            getRequiredText(node, "owner"));

                    ExitEdge exitEdge = ExitEdge.valueOf(
                            getRequiredText(node, "exitEdge"));

                    vehicle = new Vehicle(
                            id,
                            length,
                            orientation,
                            row,
                            column,
                            owner,
                            exitEdge
                    );

                } else {

                    boolean target =
                            getRequiredBoolean(node, "target");

                    vehicle = new Vehicle(
                            id,
                            length,
                            orientation,
                            row,
                            column,
                            target
                    );
                }

                board.addVehicle(vehicle);
            }

            LOGGER.debug(
                    "JSON palyameret: {} x {}, jarmuvek szama: {}",
                    rows, columns, board.getVehicles().size());

            return board;
        }
    }

    private int getRequiredInt(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);

        if (value == null || !value.isIntegralNumber()
                || !value.canConvertToInt()) {

            throw new IllegalArgumentException(
                    "Hibas vagy hianyzo egesz szam: " + fieldName);
        }

        return value.intValue();
    }

    private String getRequiredText(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);

        if (value == null || !value.isTextual()
                || value.asText().isBlank()) {

            throw new IllegalArgumentException(
                    "Hibas vagy hianyzo szoveg: " + fieldName);
        }

        return value.asText();
    }

    private boolean getRequiredBoolean(JsonNode node,
                                       String fieldName) {

        JsonNode value = node.get(fieldName);

        if (value == null || !value.isBoolean()) {
            throw new IllegalArgumentException(
                    "Hibas vagy hianyzo logikai ertek: " + fieldName);
        }

        return value.booleanValue();
    }
}