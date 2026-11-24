package hu.nye.progtech.rushhour.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerTest {

    @Test
    void shouldCreateHumanPlayer() {
        Player player = new Player("Teszt", false);

        assertEquals("Teszt", player.getName());
        assertFalse(player.isComputer());
    }

    @Test
    void shouldCreateComputerPlayer() {
        Player player = new Player("Computer", true);

        assertEquals("Computer", player.getName());
        assertTrue(player.isComputer());
    }

    @Test
    void shouldRejectNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player(null, false));
    }

    @Test
    void shouldRejectEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("", false));
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("   ", false));
    }

    @Test
    void shouldBeEqualWithSameValues() {
        Player first = new Player("Teszt", false);
        Player second = new Player("Teszt", false);

        assertEquals(first, second);
        assertEquals(second, first);
    }

    @Test
    void shouldNotBeEqualWithDifferentNames() {
        Player first = new Player("Teszt1", false);
        Player second = new Player("Teszt2", false);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWithDifferentTypes() {
        Player first = new Player("Teszt", false);
        Player second = new Player("Teszt", true);

        assertNotEquals(first, second);
    }

    @Test
    void shouldBeEqualToItself() {
        Player player = new Player("Teszt", false);

        assertEquals(player, player);
    }

    @Test
    void shouldNotBeEqualToNull() {
        Player player = new Player("Teszt", false);

        assertNotEquals(player, null);
    }

    @Test
    void shouldNotBeEqualToDifferentObjectType() {
        Player player = new Player("Teszt", false);

        assertNotEquals(player, "Teszt");
    }

    @Test
    void shouldHaveSameHashCodeForEqualPlayers() {
        Player first = new Player("Teszt", false);
        Player second = new Player("Teszt", false);

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldReturnHumanPlayerAsString() {
        Player player = new Player("Teszt", false);

        assertEquals("Teszt (ember)", player.toString());
    }

    @Test
    void shouldReturnComputerPlayerAsString() {
        Player player = new Player("Computer", true);

        assertEquals("Computer (gep)", player.toString());
    }
}