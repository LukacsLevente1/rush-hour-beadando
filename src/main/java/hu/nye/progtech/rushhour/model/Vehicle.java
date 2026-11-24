package hu.nye.progtech.rushhour.model;

import java.util.Objects;

public final class Vehicle {

    private final String id;
    private final int length;
    private final Orientation orientation;
    private final boolean target;
    private final int row;
    private final int column;

    public Vehicle(String id, int length,
                   Orientation orientation,
                   int row, int column) {
        this(id, length, orientation, row, column, false);
    }

    public Vehicle(String id, int length,
                   Orientation orientation,
                   int row, int column,
                   boolean target) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "A jarmu azonositoja nem lehet ures.");
        }

        if (length < 1 || orientation == null
                || row < 0 || column < 0) {
            throw new IllegalArgumentException(
                    "Hibas jarmu adatok.");
        }

        this.id = id;
        this.length = length;
        this.orientation = orientation;
        this.row = row;
        this.column = column;
        this.target = target;
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

    public Vehicle withPosition(int row, int column) {
        return new Vehicle(
                id, length, orientation,
                row, column, target);
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
                && orientation == other.orientation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, length, orientation,
                row, column, target);
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
                + '}';
    }
}