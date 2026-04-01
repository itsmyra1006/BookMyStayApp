import java.util.*;

// Custom Exceptions
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class InventoryException extends Exception {
    public InventoryException(String message) {
        super(message);
    }
}

// Reservation entity
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation[ID=" + reservationId +
                ", Guest=" + guestName +
                ", RoomType=" + roomType + "]";
    }
}

// Validator class
class BookingValidator {
    private Set<String> validRoomTypes;

    public BookingValidator(Set<String> validRoomTypes) {
        this.validRoomTypes = validRoomTypes;
    }

    public void validateReservation(Reservation reservation, Map<String, Integer> inventory)
            throws InvalidRoomTypeException, InventoryException {
        if (reservation.getGuestName() == null || reservation.getGuestName().isEmpty()) {
            throw new InvalidRoomTypeException("Guest name cannot be empty.");
        }

        if (!validRoomTypes.contains(reservation.getRoomType())) {
            throw new InvalidRoomTypeException("Invalid room type: " + reservation.getRoomType());
        }

        if (inventory.getOrDefault(reservation.getRoomType(), 0) <= 0) {
            throw new InventoryException("No availability for room type: " + reservation.getRoomType());
        }
    }
}

// Booking Service with validation
class BookingService {
    private Map<String, Integer> inventory;
    private BookingValidator validator;

    public BookingService(Map<String, Integer> inventory) {
        this.inventory = inventory;
        this.validator = new BookingValidator(inventory.keySet());
    }

    public void processReservation(Reservation reservation) {
        try {
            validator.validateReservation(reservation, inventory);
            // Deduct inventory safely
            inventory.put(reservation.getRoomType(), inventory.get(reservation.getRoomType()) - 1);
            System.out.println("✅ Confirmed Reservation: " + reservation);
        } catch (InvalidRoomTypeException | InventoryException e) {
            System.out.println("❌ Reservation Failed: " + e.getMessage());
        }
    }

    public void displayInventory() {
        System.out.println("=== Current Inventory ===");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue() + " rooms left");
        }
    }
}

// Main Application
public class BookMyStayApp {
    public static void main(String[] args) {
        // Initialize inventory
        Map<String, Integer> initialInventory = new HashMap<>();
        initialInventory.put("Single", 2);
        initialInventory.put("Double", 1);
        initialInventory.put("Suite", 1);

        BookingService bookingService = new BookingService(initialInventory);

        // Test reservations
        Reservation r1 = new Reservation("R001", "Alice", "Single");
        Reservation r2 = new Reservation("R002", "Bob", "Suite");
        Reservation r3 = new Reservation("R003", "Charlie", "Deluxe"); // Invalid room type
        Reservation r4 = new Reservation("R004", "", "Double");        // Empty guest name
        Reservation r5 = new Reservation("R005", "Eve", "Suite");      // No availability after Bob

        bookingService.processReservation(r1);
        bookingService.processReservation(r2);
        bookingService.processReservation(r3);
        bookingService.processReservation(r4);
        bookingService.processReservation(r5);

        bookingService.displayInventory();
    }
} 