package hu.nye.progtech.rushhour.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleTest {

    @Test
    void shouldCreateVehicle() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        assertEquals("A", vehicle.getId());
        assertEquals(2, vehicle.getLength());
        assertEquals(Orientation.HORIZONTAL,
                vehicle.getOrientation());
        assertEquals(2, vehicle.getRow());
        assertEquals(0, vehicle.getColumn());
        assertFalse(vehicle.isTarget());
    }

    @Test
    void shouldCreateTargetVehicle() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, true);

        assertTrue(vehicle.isTarget());
    }

    @Test
    void shouldCreateVehicleWithNewPosition() {
        Vehicle vehicle = new Vehicle(
                "B", 3, Orientation.VERTICAL, 0, 4);

        Vehicle moved = vehicle.withPosition(1, 4);

        assertEquals(1, moved.getRow());
        assertEquals(4, moved.getColumn());
        assertNotSame(vehicle, moved);
    }

    @Test
    void shouldKeepOriginalVehicleUnchanged() {
        Vehicle vehicle = new Vehicle(
                "B", 3, Orientation.VERTICAL, 0, 4);

        Vehicle moved = vehicle.withPosition(1, 4);

        assertEquals(0, vehicle.getRow());
        assertEquals(4, vehicle.getColumn());

        assertEquals(1, moved.getRow());
        assertEquals(4, moved.getColumn());
    }

    @Test
    void shouldPreserveVehiclePropertiesAfterMove() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL,
                2, 0, true);

        Vehicle moved = vehicle.withPosition(2, 3);

        assertEquals(vehicle.getId(), moved.getId());
        assertEquals(vehicle.getLength(), moved.getLength());
        assertEquals(vehicle.getOrientation(),
                moved.getOrientation());
        assertEquals(vehicle.isTarget(), moved.isTarget());
    }

    @Test
    void shouldRejectNegativePosition() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        assertThrows(IllegalArgumentException.class,
                () -> vehicle.withPosition(-1, 0));

        assertThrows(IllegalArgumentException.class,
                () -> vehicle.withPosition(2, -1));
    }

    @Test
    void shouldRejectInvalidLength() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(
                        "A", 0, Orientation.HORIZONTAL, 2, 0));
    }

    @Test
    void shouldRejectEmptyId() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(
                        "", 2, Orientation.HORIZONTAL, 2, 0));
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(
                        null, 2, Orientation.HORIZONTAL, 2, 0));
    }

    @Test
    void shouldRejectNullOrientation() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("A", 2, null, 2, 0));
    }

    @Test
    void shouldRecognizeEqualVehicles() {
        Vehicle first = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0, true);

        Vehicle second = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0, true);

        assertEquals(first, second);
        assertEquals(second, first);
        assertNotSame(first, second);
    }

    @Test
    void shouldRecognizeDifferentVehicles() {
        Vehicle first = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        assertNotEquals(first, new Vehicle(
                "B", 2, Orientation.HORIZONTAL, 2, 0));

        assertNotEquals(first, new Vehicle(
                "A", 3, Orientation.HORIZONTAL, 2, 0));

        assertNotEquals(first, new Vehicle(
                "A", 2, Orientation.VERTICAL, 2, 0));

        assertNotEquals(first, first.withPosition(3, 0));
        assertNotEquals(first, first.withPosition(2, 1));

        assertNotEquals(first, new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0, true));
    }

    @Test
    void shouldBeEqualToItself() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        assertEquals(vehicle, vehicle);
    }

    @Test
    void shouldNotEqualNullOrDifferentType() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        assertFalse(vehicle.equals(null));
        assertFalse(vehicle.equals("A"));
    }

    @Test
    void shouldHaveSameHashCodeForEqualVehicles() {
        Vehicle first = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        Vehicle second = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldReturnReadableString() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0, true);

        String result = vehicle.toString();

        assertTrue(result.contains("id='A'"));
        assertTrue(result.contains("length=2"));
        assertTrue(result.contains("orientation=HORIZONTAL"));
        assertTrue(result.contains("row=2"));
        assertTrue(result.contains("column=0"));
        assertTrue(result.contains("target=true"));
    }
}