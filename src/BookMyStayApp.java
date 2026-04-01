import java.io.*;
import java.util.*;

// Reservation entity (Serializable for persistence)
class Reservation implements Serializable {
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

// Booking History (Serializable)
class BookingHistory implements Serializable {
    private List<Reservation> confirmedBookings = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(confirmedBookings);
    }
}

// Persistence Service
class PersistenceService {
    private static final String FILE_NAME = "booking_state.ser";

    public static void saveState(Map<String, Integer> inventory, BookingHistory history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(history);
            System.out.println("💾 System state saved successfully.");
        } catch (IOException e) {
            System.out.println("❌ Error saving state: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Integer> loadInventory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (Map<String, Integer>) ois.readObject();
        } catch (Exception e) {
            System.out.println("⚠️ No saved inventory found, starting fresh.");
            return new HashMap<>();
        }
    }

    public static BookingHistory loadHistory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            // Skip inventory object
            ois.readObject();
            return (BookingHistory) ois.readObject();
        } catch (Exception e) {
            System.out.println("⚠️ No saved booking history found, starting fresh.");
            return new BookingHistory();
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

    public void displayHistory() {
        System.out.println("=== Booking History ===");
        for (Reservation r : history.getAllReservations()) {
            System.out.println(r);
        }
    }
}

// Main Application
public class BookMyStayApp {
    public static void main(String[] args) {
        // Load persisted state if available
        Map<String, Integer> inventory = PersistenceService.loadInventory();
        BookingHistory history = PersistenceService.loadHistory();

        // If inventory is empty, initialize fresh
        if (inventory.isEmpty()) {
            inventory.put("Single", 2);
            inventory.put("Double", 1);
            inventory.put("Suite", 1);
        }

        BookingService bookingService = new BookingService(inventory, history);

        // Simulate new reservations
        bookingService.processReservation("R001", "Alice", "Single");
        bookingService.processReservation("R002", "Bob", "Suite");

        bookingService.displayInventory();
        bookingService.displayHistory();

        // Save state before shutdown
        PersistenceService.saveState(inventory, history);
    }
}