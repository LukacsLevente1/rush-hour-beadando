package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Player;
import hu.nye.progtech.rushhour.model.Vehicle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GameService.class);

    private final Board board;
    private final Player human;
    private final Player computer;
    private final Random random = new Random();

    private boolean humanTurn = true;

    private Move lastComputerMove;

    public GameService(Board board, Player human,
                       Player computer) {

        if (board == null || human == null || computer == null) {
            throw new IllegalArgumentException(
                    "Hibas jatek adatok.");
        }

        if (human.isComputer() || !computer.isComputer()) {
            throw new IllegalArgumentException(
                    "Hibas jatekos tipusok.");
        }

        this.board = board;
        this.human = human;
        this.computer = computer;

        LOGGER.info("Jatekszolgaltatas inicializalva. Jatekos: {}",
                human.getName());
    }

    public boolean isHumanTurn() {
        return humanTurn;
    }

    public Player getCurrentPlayer() {
        return humanTurn ? human : computer;
    }

    public Move getLastComputerMove() {
        return lastComputerMove;
    }

    public Player getWinner() {
        if (!board.isGameOver()) {
            return null;
        }

        return "A".equalsIgnoreCase(board.getWinnerId())
                ? human : computer;
    }

    public boolean humanMove(String id, int steps) {
        if (!humanTurn || board.isGameOver()) {
            LOGGER.debug("A jatekos jelenleg nem lephet.");
            return false;
        }

        if (!board.moveVehicle(id, steps)) {
            LOGGER.debug("Szabalytalan jatekoslepes: {} {}",
                    id, steps);

            return false;
        }

        LOGGER.info("Jatekos lepese: {} {}", id, steps);

        humanTurn = false;
        return true;
    }

    public boolean humanExit() {
        if (!humanTurn || board.isGameOver()) {
            return false;
        }

        boolean success = board.exitVehicle("A", true);

        if (success) {
            LOGGER.info("A jatekos megnyerte a jatekot: {}",
                    human.getName());
        } else {
            LOGGER.debug("Sikertelen kijutasi kiserlet.");
        }

        return success;
    }

    public boolean computerMove() {
        if (humanTurn || board.isGameOver()) {
            return false;
        }

        lastComputerMove = null;

        if (board.exitVehicle("C", false)) {
            LOGGER.info("A szamitogep megnyerte a jatekot.");
            return true;
        }

        List<Move> possibleMoves = new ArrayList<>();
        List<Move> preferredMoves = new ArrayList<>();

        int maxSteps = Math.max(
                board.getRows(), board.getColumns());

        for (Vehicle vehicle : board.getVehicles()) {
            for (int steps = -maxSteps;
                 steps <= maxSteps; steps++) {

                if (steps == 0) {
                    continue;
                }

                Board simulation = board.copy();

                if (simulation.moveVehicle(
                        vehicle.getId(), steps)) {

                    Move move = new Move(
                            vehicle.getId(), steps);

                    possibleMoves.add(move);

                    if ("C".equalsIgnoreCase(vehicle.getId())
                            && steps < 0) {

                        preferredMoves.add(move);
                    }
                }
            }
        }

        LOGGER.debug("Lehetseges gepi lepesek: {}",
                possibleMoves.size());

        if (possibleMoves.isEmpty()) {
            LOGGER.warn("A szamitogep nem tudott lepni.");

            humanTurn = true;
            return false;
        }

        Move chosen;

        if (!preferredMoves.isEmpty()) {
            chosen = preferredMoves.get(0);

            for (Move move : preferredMoves) {
                if (move.steps() < chosen.steps()) {
                    chosen = move;
                }
            }
        } else {
            chosen = possibleMoves.get(
                    random.nextInt(possibleMoves.size()));
        }

        boolean success = board.moveVehicle(
                chosen.vehicleId(), chosen.steps());

        if (success) {
            lastComputerMove = chosen;

            LOGGER.info("Szamitogep lepese: {} {}",
                    chosen.vehicleId(), chosen.steps());
        }

        humanTurn = true;
        return success;
    }

    public record Move(String vehicleId, int steps) {
    }
}