import java.util.*;

// Reservation entity
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean cancelled;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.cancelled = false;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isCancelled() { return cancelled; }

    public void markCancelled() { this.cancelled = true; }

    @Override
    public String toString() {
        return "Reservation[ID=" + reservationId +
                ", Guest=" + guestName +
                ", RoomType=" + roomType +
                ", RoomID=" + roomId +
                ", Cancelled=" + cancelled + "]";
    }
}

// Booking History
class BookingHistory {
    private List<Reservation> confirmedBookings = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(confirmedBookings);
    }

    public Reservation findReservationById(String reservationId) {
        for (Reservation r : confirmedBookings) {
            if (r.getReservationId().equals(reservationId)) {
                return r;
            }
        }
        return null;
    }
}

// Cancellation Service
class CancellationService {
    private Map<String, Integer> inventory;
    private BookingHistory history;
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(Map<String, Integer> inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void cancelReservation(String reservationId) {
        Reservation reservation = history.findReservationById(reservationId);

        if (reservation == null) {
            System.out.println("❌ Cancellation Failed: Reservation not found.");
            return;
        }

        if (reservation.isCancelled()) {
            System.out.println("❌ Cancellation Failed: Reservation already cancelled.");
            return;
        }

        // Perform rollback
        rollbackStack.push(reservation.getRoomId());
        inventory.put(reservation.getRoomType(),
                inventory.getOrDefault(reservation.getRoomType(), 0) + 1);
        reservation.markCancelled();

        System.out.println("✅ Cancellation Successful: " + reservation.getReservationId() +
                " (Room released: " + rollbackStack.peek() + ")");
    }

    public void displayRollbackStack() {
        System.out.println("=== Rollback Stack (Released Rooms) ===");
        for (String roomId : rollbackStack) {
            System.out.println(roomId);
        }
    }
}

// Booking Service
class BookingService {
    private Map<String, Integer> inventory;
    private BookingHistory history;

    public BookingService(Map<String, Integer> inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void processReservation(String reservationId, String guestName, String roomType) {
        if (!inventory.containsKey(roomType)) {
            System.out.println("❌ Reservation Failed: Invalid room type " + roomType);
            return;
        }

        if (inventory.get(roomType) <= 0) {
            System.out.println("❌ Reservation Failed: No availability for " + roomType);
            return;
        }

        // Allocate room
        String roomId = UUID.randomUUID().toString();
        inventory.put(roomType, inventory.get(roomType) - 1);
        Reservation reservation = new Reservation(reservationId, guestName, roomType, roomId);
        history.addReservation(reservation);

        System.out.println("✅ Confirmed Reservation: " + reservation);
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

        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService(initialInventory, history);
        CancellationService cancellationService = new CancellationService(initialInventory, history);

        // Confirm bookings
        bookingService.processReservation("R001", "Alice", "Single");
        bookingService.processReservation("R002", "Bob", "Suite");
        bookingService.processReservation("R003", "Charlie", "Double");

        bookingService.displayInventory();

        // Cancel a booking
        cancellationService.cancelReservation("R002"); // Bob cancels Suite
        cancellationService.cancelReservation("R002"); // Duplicate cancellation
        cancellationService.cancelReservation("R999"); // Non-existent reservation

        bookingService.displayInventory();
        cancellationService.displayRollbackStack();
    }
}