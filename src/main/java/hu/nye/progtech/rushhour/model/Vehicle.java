
package hu.nye.progtech.rushhour.model;

import java.util.Objects;

public final class Vehicle {

    private final String id;
    private final int length;
    private final Orientation orientation;
    private final boolean target;
    private final int row;
    private final int column;
    private final VehicleOwner owner;
    private final ExitEdge exitEdge;

    public Vehicle(String id, int length,
                   Orientation orientation,
                   int row, int column) {

        this(id, length, orientation, row, column, false);
    }

    public Vehicle(String id, int length,
                   Orientation orientation,
                   int row, int column,
                   boolean target) {

        this(id, length, orientation, row, column,
                target, VehicleOwner.NEUTRAL, ExitEdge.NONE);
    }

    public Vehicle(String id, int length,
                   Orientation orientation,
                   int row, int column,
                   VehicleOwner owner, ExitEdge exitEdge) {

        this(id, length, orientation, row, column,
                owner != VehicleOwner.NEUTRAL,
                owner, exitEdge);
    }

    private Vehicle(String id, int length,
                    Orientation orientation,
                    int row, int column,
                    boolean target,
                    VehicleOwner owner, ExitEdge exitEdge) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "A jarmu azonositoja nem lehet ures.");
        }

        if (length < 1 || orientation == null
                || row < 0 || column < 0
                || owner == null || exitEdge == null) {

            throw new IllegalArgumentException(
                    "Hibas jarmu adatok.");
        }

        if (owner == VehicleOwner.NEUTRAL
                && exitEdge != ExitEdge.NONE) {

            throw new IllegalArgumentException(
                    "Semleges jarmunek nem lehet kijarata.");
        }

        if (owner != VehicleOwner.NEUTRAL
                && exitEdge == ExitEdge.NONE) {

            throw new IllegalArgumentException(
                    "A kiemelt jarmunek kell kijarat.");
        }

        if (orientation == Orientation.HORIZONTAL
                && (exitEdge == ExitEdge.TOP
                || exitEdge == ExitEdge.BOTTOM)) {

            throw new IllegalArgumentException(
                    "Hibas kijarati irany.");
        }

        if (orientation == Orientation.VERTICAL
                && (exitEdge == ExitEdge.LEFT
                || exitEdge == ExitEdge.RIGHT)) {

            throw new IllegalArgumentException(
                    "Hibas kijarati irany.");
        }

        this.id = id;
        this.length = length;
        this.orientation = orientation;
        this.row = row;
        this.column = column;
        this.target = target;
        this.owner = owner;
        this.exitEdge = exitEdge;
    }

    public String getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isTarget() {
        return target;
    }

    public VehicleOwner getOwner() {
        return owner;
    }

    public ExitEdge getExitEdge() {
        return exitEdge;
    }

    public Vehicle withPosition(int row, int column) {

        return new Vehicle(
                id, length, orientation,
                row, column, target,
                owner, exitEdge);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Vehicle other)) {
            return false;
        }

        return length == other.length
                && row == other.row
                && column == other.column
                && target == other.target
                && id.equals(other.id)
                && orientation == other.orientation
                && owner == other.owner
                && exitEdge == other.exitEdge;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, length, orientation,
                row, column, target,
                owner, exitEdge);
    }

    @Override
    public String toString() {
        return "Vehicle{"
                + "id='" + id + '\''
                + ", length=" + length
                + ", orientation=" + orientation
                + ", row=" + row
                + ", column=" + column
                + ", target=" + target
                + ", owner=" + owner
                + ", exitEdge=" + exitEdge
                + '}';
    }
}
