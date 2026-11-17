package hu.nye.progtech.rushhour.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldChangePosition() {
        Vehicle vehicle = new Vehicle(
                "B", 3, Orientation.VERTICAL, 0, 4);

        vehicle.setPosition(1, 4);

        assertEquals(1, vehicle.getRow());
        assertEquals(4, vehicle.getColumn());
    }

    @Test
    void shouldRejectNegativePosition() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 2, 0);

        assertThrows(IllegalArgumentException.class,
                () -> vehicle.setPosition(-1, 0));
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
}
