
package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Player;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.model.VehicleOwner;

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
    private final Random random;

    private boolean humanTurn = true;

    private Move lastComputerMove;

    public GameService(Board board, Player human,
                       Player computer) {

        this(board, human, computer, new Random());
    }

    public GameService(Board board, Player human,
                       Player computer, Random random) {

        if (board == null || human == null
                || computer == null || random == null) {

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
        this.random = random;

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

        if (board.getWinnerOwner() == VehicleOwner.HUMAN) {
            return human;
        }

        if (board.getWinnerOwner() == VehicleOwner.COMPUTER) {
            return computer;
        }

        return null;
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

        Vehicle vehicle =
                board.getVehicleByOwner(VehicleOwner.HUMAN);

        if (vehicle == null) {
            return false;
        }

        boolean success = executeExit(
                vehicle.getId(), VehicleOwner.HUMAN);

        if (success) {
            humanTurn = false;
        }

        return success;
    }

    private boolean executeExit(String id, VehicleOwner owner) {
        Vehicle vehicle = board.getVehicleById(id);

        if (vehicle == null || vehicle.getOwner() != owner) {
            return false;
        }

        if (!board.exitVehicle(id)) {
            LOGGER.debug("Sikertelen kijutasi kiserlet: {}", id);
            return false;
        }

        LOGGER.info("Sikeres kijutas. Jarmu: {}, tulajdonos: {}",
                id, owner);

        return true;
    }

    public boolean computerMove() {
        if (humanTurn || board.isGameOver()) {
            return false;
        }

        lastComputerMove = null;

        Move chosen = chooseRandomMove();

        if (chosen == null) {
            LOGGER.warn("A szamitogep nem tudott lepni.");

            humanTurn = true;
            return false;
        }

        boolean success;

        if (chosen.exit()) {
            success = executeExit(
                    chosen.vehicleId(), VehicleOwner.COMPUTER);
        } else {
            success = board.moveVehicle(
                    chosen.vehicleId(), chosen.steps());
        }

        if (success) {
            lastComputerMove = chosen;

            LOGGER.info("Szamitogep lepese: {}, lepes: {}, kijutas: {}",
                    chosen.vehicleId(),
                    chosen.steps(),
                    chosen.exit());
        }

        humanTurn = true;
        return success;
    }

    private Move chooseRandomMove() {

        List<Vehicle> candidates =
                new ArrayList<>(board.getVehicles());

        while (!candidates.isEmpty()) {

            int vehicleIndex =
                    random.nextInt(candidates.size());

            Vehicle vehicle =
                    candidates.remove(vehicleIndex);

            List<Move> legalMoves =
                    getLegalMoves(vehicle);

            if (!legalMoves.isEmpty()) {

                int moveIndex =
                        random.nextInt(legalMoves.size());

                return legalMoves.get(moveIndex);
            }

            LOGGER.debug("Nem mozgathato jarmu: {}",
                    vehicle.getId());
        }

        return null;
    }

    private List<Move> getLegalMoves(Vehicle vehicle) {

        List<Move> legalMoves = new ArrayList<>();

        int maxSteps = Math.max(
                board.getRows(), board.getColumns());

        for (int steps = -maxSteps;
             steps <= maxSteps; steps++) {

            if (steps == 0) {
                continue;
            }

            Board simulation = board.copy();

            if (simulation.moveVehicle(
                    vehicle.getId(), steps)) {

                legalMoves.add(new Move(
                        vehicle.getId(), steps));
            }
        }

        if (vehicle.getOwner() == VehicleOwner.COMPUTER
                && board.canExitVehicle(vehicle.getId())) {

            legalMoves.add(new Move(
                    vehicle.getId(), 0, true));
        }

        return legalMoves;
    }

    public record Move(String vehicleId, int steps,
                       boolean exit) {

        public Move(String vehicleId, int steps) {
            this(vehicleId, steps, false);
        }
    }
}
