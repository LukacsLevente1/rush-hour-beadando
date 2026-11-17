
package hu.nye.progtech.rushhour.ui;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Player;
import hu.nye.progtech.rushhour.persistence.LevelFile;
import hu.nye.progtech.rushhour.persistence.LevelJsonFile;
import hu.nye.progtech.rushhour.persistence.LevelXmlFile;
import hu.nye.progtech.rushhour.persistence.ScoreRepository;
import hu.nye.progtech.rushhour.service.GameService;

import java.io.InputStream;
import java.io.PrintStream;
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
        GameService game = new GameService(board, human, computer);

        output.println("Udvozollek, " + name + "!");
        output.println("Lepes: A 2 vagy B -1");
        output.println("Kijutas: exit A");
        output.println("Ranglista: scores");
        output.println("Kilepes: quit");

        while (!board.isGameOver()) {
            board.printBoard();

            output.print("Lepesed: ");

            if (!scanner.hasNextLine()) {
                return;
            }

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                output.println("Jatek bezarva.");
                return;
            }

            if (input.equalsIgnoreCase("scores")) {
                scores.printLeaderboard();
                continue;
            }

            if (input.equalsIgnoreCase("exit A")) {
                if (game.humanExit()) {
                    break;
                }

                output.println(
                        "Az A auto meg nincs a kijaratnal.");
                continue;
            }

            String[] parts = input.split("\\s+");

            if (parts.length != 2) {
                output.println("Hibas parancs!");
                continue;
            }

            int steps;

            try {
                steps = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                output.println("Hibas lepesszam!");
                continue;
            }

            if (!game.humanMove(parts[0], steps)) {
                output.println("Szabalytalan lepes!");
                continue;
            }

            output.println("Sikeres lepes!");
            board.printBoard();

            output.println("A gep kovetkezik...");

            if (!game.computerMove()) {
                output.println("A gep nem tudott lepni.");
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

            scores.printLeaderboard();
        }
    }

    private Board chooseLevel() {
        output.println();
        output.println("Valassz palyaformatumot:");
        output.println("1 - TXT");
        output.println("2 - XML");
        output.println("3 - JSON");
        output.print("Valasztas (Enter = TXT): ");

        if (!scanner.hasNextLine()) {
            return levelFile.loadLevel("level1.txt");
        }

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "2":
                output.println("XML palya betoltese...");
                return new LevelXmlFile().loadLevel("level1.xml");

            case "3":
                output.println("JSON palya betoltese...");
                return new LevelJsonFile().loadLevel("level1.json");

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
