
package hu.nye.progtech.rushhour.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public final class Board {

    private final int rows;
    private final int columns;
    private final List<Vehicle> vehicles;

    private String winnerId;
    private VehicleOwner winnerOwner;

    public Board(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                    "Hibas tabla meret.");
        }

        this.rows = rows;
        this.columns = columns;
        this.vehicles = new ArrayList<>();
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public List<Vehicle> getVehicles() {
        return List.copyOf(vehicles);
    }

    public String getWinnerId() {
        return winnerId;
    }

    public VehicleOwner getWinnerOwner() {
        return winnerOwner;
    }

    public boolean isGameOver() {
        return winnerId != null;
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null || isGameOver()) {
            throw new IllegalArgumentException(
                    "Nem adhato hozza jarmu.");
        }

        for (Vehicle existing : vehicles) {
            if (existing.getId().equalsIgnoreCase(
                    vehicle.getId())) {

                throw new IllegalArgumentException(
                        "Duplikalt jarmu azonosito.");
            }

            if (vehicle.getOwner() != VehicleOwner.NEUTRAL
                    && existing.getOwner()
                    == vehicle.getOwner()) {

                throw new IllegalArgumentException(
                        "A jatekosnak mar van sajat autoja.");
            }
        }

        if (!canPlace(vehicle)) {
            throw new IllegalArgumentException(
                    "A jarmu nem fer el.");
        }

        vehicles.add(vehicle);
    }

    private boolean canPlace(Vehicle vehicle) {
        for (int i = 0; i < vehicle.getLength(); i++) {
            int r = vehicle.getRow();
            int c = vehicle.getColumn();

            if (vehicle.getOrientation()
                    == Orientation.HORIZONTAL) {
                c += i;
            } else {
                r += i;
            }

            if (r < 0 || r >= rows
                    || c < 0 || c >= columns
                    || getVehicleAt(r, c) != null) {
                return false;
            }
        }

        return true;
    }

    public Vehicle getVehicleAt(int row, int column) {
        for (Vehicle vehicle : vehicles) {
            for (int i = 0; i < vehicle.getLength(); i++) {
                int r = vehicle.getRow();
                int c = vehicle.getColumn();

                if (vehicle.getOrientation()
                        == Orientation.HORIZONTAL) {
                    c += i;
                } else {
                    r += i;
                }

                if (r == row && c == column) {
                    return vehicle;
                }
            }
        }

        return null;
    }

    public Vehicle getVehicleById(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equalsIgnoreCase(id)) {
                return vehicle;
            }
        }

        return null;
    }

    public Vehicle getVehicleByOwner(VehicleOwner owner) {
        if (owner == null || owner == VehicleOwner.NEUTRAL) {
            return null;
        }

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getOwner() == owner) {
                return vehicle;
            }
        }

        return null;
    }

    public boolean moveVehicle(String id, int steps) {
        if (steps == 0 || isGameOver()) {
            return false;
        }

        Vehicle selected = getVehicleById(id);

        if (selected == null) {
            return false;
        }

        int rowDirection = selected.getOrientation()
                == Orientation.VERTICAL
                ? Integer.signum(steps) : 0;

        int colDirection = selected.getOrientation()
                == Orientation.HORIZONTAL
                ? Integer.signum(steps) : 0;

        long distance = Math.abs((long) steps);

        for (long i = 1; i <= distance; i++) {
            long r = selected.getRow() + rowDirection * i;
            long c = selected.getColumn() + colDirection * i;

            if (steps > 0) {
                if (selected.getOrientation()
                        == Orientation.HORIZONTAL) {
                    c += selected.getLength() - 1;
                } else {
                    r += selected.getLength() - 1;
                }
            }

            if (r < 0 || r >= rows
                    || c < 0 || c >= columns) {
                return false;
            }

            Vehicle blocking = getVehicleAt((int) r, (int) c);

            if (blocking != null && blocking != selected) {
                return false;
            }
        }

        int newRow = (int) (selected.getRow()
                + rowDirection * distance);

        int newCol = (int) (selected.getColumn()
                + colDirection * distance);

        Vehicle movedVehicle = selected.withPosition(
                newRow, newCol);

        int index = vehicles.indexOf(selected);
        vehicles.set(index, movedVehicle);

        return true;
    }

    public Board copy() {
        Board copiedBoard = new Board(rows, columns);

        copiedBoard.vehicles.addAll(vehicles);
        copiedBoard.winnerId = winnerId;
        copiedBoard.winnerOwner = winnerOwner;

        return copiedBoard;
    }

    public boolean canExitVehicle(String id) {
        if (isGameOver()) {
            return false;
        }

        Vehicle selected = getVehicleById(id);

        if (selected == null || !selected.isTarget()
                || selected.getOwner() == VehicleOwner.NEUTRAL) {
            return false;
        }

        return switch (selected.getExitEdge()) {
            case LEFT -> selected.getColumn() == 0;

            case RIGHT -> selected.getColumn()
                    + selected.getLength() == columns;

            case TOP -> selected.getRow() == 0;

            case BOTTOM -> selected.getRow()
                    + selected.getLength() == rows;

            case NONE -> false;
        };
    }

    public boolean exitVehicle(String id) {
        if (!canExitVehicle(id)) {
            return false;
        }

        Vehicle selected = getVehicleById(id);

        winnerId = selected.getId();
        winnerOwner = selected.getOwner();

        vehicles.remove(selected);

        return true;
    }

    public boolean exitVehicle(String id, boolean rightExit) {
        Vehicle selected = getVehicleById(id);

        if (selected == null) {
            return false;
        }

        ExitEdge requestedEdge = rightExit
                ? ExitEdge.RIGHT : ExitEdge.LEFT;

        if (selected.getExitEdge() != requestedEdge) {
            return false;
        }

        return exitVehicle(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Board other)) {
            return false;
        }

        return rows == other.rows
                && columns == other.columns
                && Objects.equals(winnerId, other.winnerId)
                && winnerOwner == other.winnerOwner
                && new HashSet<>(vehicles).equals(
                new HashSet<>(other.vehicles));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                rows,
                columns,
                new HashSet<>(vehicles),
                winnerId,
                winnerOwner
        );
    }

    @Override
    public String toString() {
        return "Board{"
                + "rows=" + rows
                + ", columns=" + columns
                + ", vehicles=" + vehicles
                + ", winnerId='" + winnerId + '\''
                + ", winnerOwner=" + winnerOwner
                + '}';
    }
}
