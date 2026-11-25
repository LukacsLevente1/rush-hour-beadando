package hu.nye.progtech.rushhour.ui;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Player;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.persistence.LevelFile;
import hu.nye.progtech.rushhour.persistence.LevelJsonFile;
import hu.nye.progtech.rushhour.persistence.LevelXmlFile;
import hu.nye.progtech.rushhour.persistence.ScoreRepository;
import hu.nye.progtech.rushhour.service.GameService;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner;
    private final PrintStream output;
    private final LevelFile levelFile;
    private final ScoreRepository scores;

    public ConsoleUI() {
        this(System.in, System.out,
                new LevelFile(), new ScoreRepository());
    }

    public ConsoleUI(InputStream input, PrintStream output,
                     LevelFile levelFile, ScoreRepository scores) {

        if (input == null || output == null
                || levelFile == null || scores == null) {

            throw new IllegalArgumentException(
                    "A konzol adatai nem lehetnek null ertekuek.");
        }

        this.scanner = new Scanner(input);
        this.output = output;
        this.levelFile = levelFile;
        this.scores = scores;
    }

    public void start() {
        output.println("=== RUSH HOUR ===");
        output.print("Add meg a neved: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String name = scanner.nextLine().trim();

        while (name.isEmpty()) {
            output.print("A nev nem lehet ures: ");

            if (!scanner.hasNextLine()) {
                return;
            }

            name = scanner.nextLine().trim();
        }

        Player human = new Player(name, false);
        Player computer = new Player("Computer", true);

        Board board = chooseLevel();
        GameService game = new GameService(
                board, human, computer);

        output.println("Udvozollek, " + name + "!");
        printHelp();

        while (!board.isGameOver()) {
            printBoard(board);

            output.print("Lepesed: ");

            if (!scanner.hasNextLine()) {
                return;
            }

            String input = scanner.nextLine().trim();

            String[] command = input.split("\\s+", 2);

            if (input.equalsIgnoreCase("quit")) {
                output.println("Jatek bezarva.");
                return;
            }

            if (input.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }

            if (input.equalsIgnoreCase("scores")) {
                printLeaderboard();
                continue;
            }

            if (command[0].equalsIgnoreCase("save")) {
                if (command.length != 2
                        || command[1].isBlank()) {

                    output.println("Hasznalat: save mentes.txt");
                    continue;
                }

                try {
                    levelFile.saveLevel(board, command[1]);

                    output.println("Jatek sikeresen mentve: "
                            + command[1]);

                } catch (IOException | IllegalArgumentException e) {
                    output.println("Mentési hiba: "
                            + e.getMessage());
                }

                continue;
            }

            if (command[0].equalsIgnoreCase("load")) {
                if (command.length != 2
                        || command[1].isBlank()) {

                    output.println("Hasznalat: load mentes.txt");
                    continue;
                }

                try {
                    Board loadedBoard = levelFile.loadFromPath(
                            Path.of(command[1]));

                    GameService loadedGame = new GameService(
                            loadedBoard, human, computer);

                    board = loadedBoard;
                    game = loadedGame;

                    output.println("Jatek sikeresen betoltve: "
                            + command[1]);

                } catch (IOException | IllegalArgumentException e) {
                    output.println("Betoltesi hiba: "
                            + e.getMessage());
                }

                continue;
            }

            if (input.equalsIgnoreCase("exit")
                    || input.equalsIgnoreCase("exit A")) {

                if (game.humanExit()) {
                    break;
                }

                output.println(
                        "A sajat auto meg nincs a kijaratnal.");

                continue;
            }

            if (!executeMove(input, board, game)) {
                continue;
            }

            output.println("Sikeres lepes!");
            printBoard(board);

            output.println("A gep kovetkezik...");

            if (!game.computerMove()) {
                output.println("A gep nem tudott lepni.");
            } else {
                GameService.Move move = game.getLastComputerMove();

                if (move != null && move.exit()) {
                    output.println("A gep kijutott a palyarol: "
                            + move.vehicleId());
                } else if (move != null) {
                    output.println("A gep lepese: "
                            + move.vehicleId()
                            + " " + move.steps());
                }
            }
        }

        if (board.isGameOver()) {
            Player winner = game.getWinner();

            output.println("=== JATEK VEGE ===");
            output.println("Gyoztes: " + winner);

            if (winner != null) {
                scores.addWin(winner.getName());
                output.println("Gyozelem elmentve!");
            }

            printLeaderboard();
        }
    }

    private boolean executeMove(String input, Board board,
                                GameService game) {

        String[] parts = input.split("\\s+");

        if (parts.length == 2) {
            int steps;

            try {
                steps = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                output.println("Hibas lepesszam!");
                return false;
            }

            if (!game.humanMove(parts[0], steps)) {
                output.println("Szabalytalan lepes!");
                return false;
            }

            return true;
        }

        if (parts.length == 3) {
            return executeCoordinateMove(parts, board, game);
        }

        output.println("Hibas parancs!");
        return false;
    }

    private boolean executeCoordinateMove(
            String[] parts, Board board, GameService game) {

        String coordinate = parts[0].toLowerCase(Locale.ROOT);
        String direction = parts[1].toLowerCase(Locale.ROOT);

        if (!coordinate.matches("[a-z][1-9][0-9]*")) {
            output.println("Hibas koordinata!");
            return false;
        }

        int column = coordinate.charAt(0) - 'a';
        int row;
        int distance;

        try {
            row = Integer.parseInt(
                    coordinate.substring(1)) - 1;

            distance = Integer.parseInt(parts[2]);

        } catch (NumberFormatException e) {
            output.println("Hibas koordinata vagy lepesszam!");
            return false;
        }

        if (row < 0 || row >= board.getRows()
                || column < 0 || column >= board.getColumns()
                || distance <= 0) {

            output.println("Hibas koordinata vagy lepesszam!");
            return false;
        }

        Vehicle vehicle = board.getVehicleAt(row, column);

        if (vehicle == null) {
            output.println("Nincs jarmu a megadott helyen!");
            return false;
        }

        int steps;

        switch (direction) {
            case "left":
            case "bal":
                if (vehicle.getOrientation()
                        != Orientation.HORIZONTAL) {

                    output.println("Hibas mozgasi irany!");
                    return false;
                }

                steps = -distance;
                break;

            case "right":
            case "jobb":
                if (vehicle.getOrientation()
                        != Orientation.HORIZONTAL) {

                    output.println("Hibas mozgasi irany!");
                    return false;
                }

                steps = distance;
                break;

            case "up":
            case "fel":
                if (vehicle.getOrientation()
                        != Orientation.VERTICAL) {

                    output.println("Hibas mozgasi irany!");
                    return false;
                }

                steps = -distance;
                break;

            case "down":
            case "le":
                if (vehicle.getOrientation()
                        != Orientation.VERTICAL) {

                    output.println("Hibas mozgasi irany!");
                    return false;
                }

                steps = distance;
                break;

            default:
                output.println("Ismeretlen mozgasi irany!");
                return false;
        }

        if (!game.humanMove(vehicle.getId(), steps)) {
            output.println("Szabalytalan lepes!");
            return false;
        }

        return true;
    }
    private void printLeaderboard() {
        output.println();
        output.println("=== RANGLISTA ===");

        int position = 1;

        for (ScoreRepository.ScoreEntry entry
                : scores.getLeaderboard()) {

            output.printf(
                    "%d. %s - %d gyozelem%n",
                    position++,
                    entry.name(),
                    entry.wins());
        }

        if (position == 1) {
            output.println("Meg nincs elmentett eredmeny.");
        }

        output.println();
    }
    private void printHelp() {
        output.println("Lepes: A 2 vagy C -1");
        output.println("Koordinatas lepes: a3 right 2");
        output.println("Iranyok: left, right, up, down");
        output.println("Magyarul: bal, jobb, fel, le");
        output.println("Kijutas: exit");
        output.println("Mentes: save mentes.txt");
        output.println("Betoltes: load mentes.txt");
        output.println("Ranglista: scores");
        output.println("Segitseg: help");
        output.println("Kilepes: quit");
    }

    private void printBoard(Board board) {
        output.println();

        output.print("   ");

        for (int c = 0; c < board.getColumns(); c++) {
            output.print((char) ('a' + c) + " ");
        }

        output.println();

        for (int r = 0; r < board.getRows(); r++) {
            output.printf("%2d ", r + 1);

            for (int c = 0; c < board.getColumns(); c++) {
                Vehicle vehicle = board.getVehicleAt(r, c);

                output.print(vehicle == null
                        ? ". "
                        : vehicle.getId() + " ");
            }

            output.println();
        }

        output.println();
    }

    private Board chooseLevel() {
        output.println();
        output.println("Valassz palyaformatumot:");
        output.println("1 - TXT");
        output.println("2 - XML");
        output.println("3 - JSON");
        output.println("4 - Egyeni TXT fajl");
        output.print("Valasztas (Enter = TXT): ");

        if (!scanner.hasNextLine()) {
            return levelFile.loadLevel("level1.txt");
        }

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "2":
                output.println("XML palya betoltese...");
                return new LevelXmlFile()
                        .loadLevel("level1.xml");

            case "3":
                output.println("JSON palya betoltese...");
                return new LevelJsonFile()
                        .loadLevel("level1.json");

            case "4":
                output.print("Add meg a fajl eleresi utjat: ");

                if (!scanner.hasNextLine()) {
                    return levelFile.loadLevel("level1.txt");
                }

                String path = scanner.nextLine().trim();

                try {
                    return levelFile.loadFromPath(Path.of(path));

                } catch (IOException | IllegalArgumentException e) {
                    output.println("Betoltesi hiba: "
                            + e.getMessage());

                    output.println(
                            "Alapertelmezett palya betoltese...");

                    return levelFile.loadLevel("level1.txt");
                }

            case "1":
            case "":
                output.println("TXT palya betoltese...");
                return levelFile.loadLevel("level1.txt");

            default:
                output.println(
                        "Ismeretlen valasztas. TXT palya betoltese...");

                return levelFile.loadLevel("level1.txt");
        }
    }
}