package hu.nye.progtech.rushhour.model;

import java.util.Objects;

public final class Player {

    private final String name;
    private final boolean computer;

    public Player(String name, boolean computer) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "A jatekos neve nem lehet ures.");
        }

        this.name = name;
        this.computer = computer;
    }

    public String getName() {
        return name;
    }

    public boolean isComputer() {
        return computer;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Player other)) {
            return false;
        }

        return computer == other.computer
                && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, computer);
    }

    @Override
    public String toString() {
        return name + (computer ? " (gep)" : " (ember)");
    }
}