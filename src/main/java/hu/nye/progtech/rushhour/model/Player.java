
package hu.nye.progtech.rushhour.model;

public class Player {

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
    public String toString() {
        return name + (computer ? " (gep)" : " (ember)");
    }
}
