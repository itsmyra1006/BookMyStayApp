import java.util.*;

// Domain model for Room details
class Room {
    private String type;
    private double price;
    private String amenities;

    public Room(String type, double price, String amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getAmenities() { return amenities; }

    @Override
    public String toString() {
        return type + " | Price: $" + price + " | Amenities: " + amenities;
    }
}

// Centralized inventory management
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory(Map<String, Integer> initialInventory) {
        this.inventory = new HashMap<>(initialInventory);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int newCount) {
        if (newCount >= 0) {
            inventory.put(roomType, newCount);
        } else {
            System.out.println("Invalid update: availability cannot be negative.");
        }
    }

    public Map<String, Integer> getInventorySnapshot() {
        return new HashMap<>(inventory); // Defensive copy
    }
}

// Read-only search service
class SearchService {
    private RoomInventory inventory;
    private Map<String, Room> roomDetails;

    public SearchService(RoomInventory inventory, Map<String, Room> roomDetails) {
        this.inventory = inventory;
        this.roomDetails = roomDetails;
    }

    public void searchAvailableRooms() {
        System.out.println("\nAvailable Rooms:");
        for (Map.Entry<String, Room> entry : roomDetails.entrySet()) {
            String roomType = entry.getKey();
            int available = inventory.getAvailability(roomType);

            if (available > 0) {
                Room room = entry.getValue();
                System.out.println(room.toString() + " | Available: " + available);
            }
        }
    }
}

// Reservation represents guest intent
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return "Reservation Request: Guest=" + guestName + ", RoomType=" + roomType;
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        this.requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation); // FIFO insertion
        System.out.println("Added to queue: " + reservation);
    }

    public void displayQueue() {
        System.out.println("\nCurrent Booking Request Queue:");
        for (Reservation r : requestQueue) {
            System.out.println(r);
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        // Initialize inventory
        Map<String, Integer> initialRooms = new HashMap<>();
        initialRooms.put("Single", 10);
        initialRooms.put("Double", 5);
        initialRooms.put("Suite", 2);

        RoomInventory inventory = new RoomInventory(initialRooms);

        // Initialize room details
        Map<String, Room> roomDetails = new HashMap<>();
        roomDetails.put("Single", new Room("Single", 50.0, "WiFi, TV"));
        roomDetails.put("Double", new Room("Double", 90.0, "WiFi, TV, Mini-bar"));
        roomDetails.put("Suite", new Room("Suite", 200.0, "WiFi, TV, Mini-bar, Jacuzzi"));

        // Search service
        SearchService searchService = new SearchService(inventory, roomDetails);
        searchService.searchAvailableRooms();

        // Booking request queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Guests submit booking requests
        bookingQueue.addRequest(new Reservation("Alice", "Single"));
        bookingQueue.addRequest(new Reservation("Bob", "Suite"));
        bookingQueue.addRequest(new Reservation("Charlie", "Double"));

        // Display queue state
        bookingQueue.displayQueue();

        // Note: No inventory mutation yet — requests are only queued
    }
}