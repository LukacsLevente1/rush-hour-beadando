
package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Player;
import hu.nye.progtech.rushhour.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameService {

    private final Board board;
    private final Player human;
    private final Player computer;
    private final Random random = new Random();

    private boolean humanTurn = true;

    public GameService(Board board, Player human, Player computer) {
        if (board == null || human == null || computer == null) {
            throw new IllegalArgumentException("Hibas jatek adatok.");
        }

        if (human.isComputer() || !computer.isComputer()) {
            throw new IllegalArgumentException("Hibas jatekos tipusok.");
        }

        this.board = board;
        this.human = human;
        this.computer = computer;
    }

    public boolean isHumanTurn() {
        return humanTurn;
    }

    public Player getCurrentPlayer() {
        return humanTurn ? human : computer;
    }

    public Player getWinner() {
        if (!board.isGameOver()) {
            return null;
        }

        return "A".equals(board.getWinnerId())
                ? human : computer;
    }

    public boolean humanMove(String id, int steps) {
        if (!humanTurn || board.isGameOver()) {
            return false;
        }

        if (!board.moveVehicle(id, steps)) {
            return false;
        }

        humanTurn = false;
        return true;
    }

    public boolean humanExit() {
        if (!humanTurn || board.isGameOver()) {
            return false;
        }

        return board.exitVehicle("A", true);
    }

    public boolean computerMove() {
        if (humanTurn || board.isGameOver()) {
            return false;
        }

        if (board.exitVehicle("C", false)) {
            return true;
        }

        List<Move> possibleMoves = new ArrayList<>();
        int maxSteps = Math.max(board.getRows(), board.getColumns());

        for (Vehicle vehicle : board.getVehicles()) {
            for (int steps = -maxSteps; steps <= maxSteps; steps++) {
                if (steps == 0) {
                    continue;
                }

                int oldRow = vehicle.getRow();
                int oldColumn = vehicle.getColumn();

                if (board.moveVehicle(vehicle.getId(), steps)) {
                    possibleMoves.add(new Move(vehicle.getId(), steps));
                    vehicle.setPosition(oldRow, oldColumn);
                }
            }
        }

        if (possibleMoves.isEmpty()) {
            humanTurn = true;
            return false;
        }

        Move chosen = possibleMoves.get(
                random.nextInt(possibleMoves.size()));

        boolean success = board.moveVehicle(
                chosen.vehicleId(), chosen.steps());

        if (success) {
            System.out.println("A gep lepese: "
                    + chosen.vehicleId() + " " + chosen.steps());
        }

        humanTurn = true;
        return success;
    }

    private record Move(String vehicleId, int steps) {
    }
}
