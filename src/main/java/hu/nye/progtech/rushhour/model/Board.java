
package hu.nye.progtech.rushhour.model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final int rows;
    private final int columns;
    private final List<Vehicle> vehicles;

    private String winnerId;

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

    public boolean isGameOver() {
        return winnerId != null;
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null || isGameOver()) {
            throw new IllegalArgumentException(
                    "Nem adhato hozza jarmu.");
        }

        for (Vehicle existing : vehicles) {
            if (existing.getId().equals(vehicle.getId())) {
                throw new IllegalArgumentException(
                        "Duplikalt jarmu azonosito.");
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

            if (r < 0 || r >= rows || c < 0 || c >= columns
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

    public boolean moveVehicle(String id, int steps) {
        if (steps == 0 || isGameOver()) {
            return false;
        }

        Vehicle selected = null;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(id)) {
                selected = vehicle;
                break;
            }
        }

        if (selected == null) {
            return false;
        }

        int rowDirection = selected.getOrientation()
                == Orientation.VERTICAL
                ? Integer.signum(steps) : 0;

        int colDirection = selected.getOrientation()
                == Orientation.HORIZONTAL
                ? Integer.signum(steps) : 0;

        for (long i = 1; i <= Math.abs((long) steps); i++) {
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

            if (r < 0 || r >= rows || c < 0 || c >= columns) {
                return false;
            }

            Vehicle blocking = getVehicleAt((int) r, (int) c);
            if (blocking != null && blocking != selected) {
                return false;
            }
        }

        int newRow = selected.getRow()
                + rowDirection * steps;
        int newCol = selected.getColumn()
                + colDirection * steps;

        selected.setPosition(newRow, newCol);
        return true;
    }

    public boolean exitVehicle(String id, boolean rightExit) {
        if (isGameOver()) {
            return false;
        }

        Vehicle selected = null;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(id)) {
                selected = vehicle;
                break;
            }
        }

        if (selected == null || !selected.isTarget()
                || selected.getOrientation()
                != Orientation.HORIZONTAL) {
            return false;
        }

        if (rightExit) {
            if (!id.equals("A")
                    || selected.getColumn()
                    + selected.getLength() != columns) {
                return false;
            }
        } else {
            if (!id.equals("C")
                    || selected.getColumn() != 0) {
                return false;
            }
        }

        winnerId = id;
        vehicles.remove(selected);
        return true;
    }

    public void printBoard() {
        System.out.println();

        System.out.print("   ");
        for (int c = 0; c < columns; c++) {
            System.out.print((char) ('a' + c) + " ");
        }
        System.out.println();

        for (int r = 0; r < rows; r++) {
            System.out.printf("%2d ", r + 1);

            for (int c = 0; c < columns; c++) {
                Vehicle vehicle = getVehicleAt(r, c);
                System.out.print(
                        vehicle == null ? ". " : vehicle.getId() + " ");
            }
            System.out.println();
        }

        System.out.println();
    }
}
