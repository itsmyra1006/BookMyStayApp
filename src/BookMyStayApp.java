import java.util.*;
import java.util.concurrent.*;

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

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return "Reservation[ID=" + reservationId +
                ", Guest=" + guestName +
                ", RoomType=" + roomType + "]";
    }
}

// Thread-safe Booking Service
class BookingService {
    private final Map<String, Integer> inventory;

    public BookingService(Map<String, Integer> inventory) {
        this.inventory = inventory;
    }

    // Critical section: synchronized to prevent race conditions
    public synchronized void processReservation(Reservation reservation) {
        String roomType = reservation.getRoomType();

        if (!inventory.containsKey(roomType)) {
            System.out.println("❌ Reservation Failed: Invalid room type " + roomType);
            return;
        }

        if (inventory.get(roomType) <= 0) {
            System.out.println("❌ Reservation Failed: No availability for " + roomType +
                    " (Guest=" + reservation.getGuestName() + ")");
            return;
        }

        // Deduct inventory safely
        inventory.put(roomType, inventory.get(roomType) - 1);
        System.out.println("✅ Confirmed Reservation: " + reservation +
                " | RoomID=" + UUID.randomUUID());
    }

    public void displayInventory() {
        System.out.println("=== Final Inventory State ===");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue() + " rooms left");
        }
    }
}

// Concurrent Booking Processor
public class BookMyStayApp {
    public static void main(String[] args) throws InterruptedException {
        // Initialize inventory
        Map<String, Integer> initialInventory = new HashMap<>();
        initialInventory.put("Single", 2);
        initialInventory.put("Double", 1);
        initialInventory.put("Suite", 1);

        BookingService bookingService = new BookingService(initialInventory);

        // Shared booking requests
        List<Reservation> requests = Arrays.asList(
                new Reservation("R001", "Alice", "Single"),
                new Reservation("R002", "Bob", "Suite"),
                new Reservation("R003", "Charlie", "Single"),
                new Reservation("R004", "Diana", "Double"),
                new Reservation("R005", "Eve", "Suite")
        );

        // Thread pool simulating concurrent guests
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (Reservation r : requests) {
            executor.submit(() -> bookingService.processReservation(r));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        bookingService.displayInventory();
    }
}