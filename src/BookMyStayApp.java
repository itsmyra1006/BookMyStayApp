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

    public synchronized int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public synchronized void updateAvailability(String roomType, int newCount) {
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
        requestQueue.offer(reservation);
        System.out.println("Added to queue: " + reservation);
    }

    public Reservation pollRequest() {
        return requestQueue.poll(); // FIFO dequeue
    }

    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }
}

// Booking Service handles allocation
class BookingService {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms; // roomType -> set of room IDs

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.allocatedRooms = new HashMap<>();
    }

    public void processRequests(BookingRequestQueue queue) {
        while (!queue.isEmpty()) {
            Reservation request = queue.pollRequest();
            allocateRoom(request);
        }
    }

    private void allocateRoom(Reservation request) {
        String roomType = request.getRoomType();
        int available = inventory.getAvailability(roomType);

        if (available > 0) {
            // Generate unique room ID
            String roomId = UUID.randomUUID().toString();

            // Ensure uniqueness using Set
            allocatedRooms.putIfAbsent(roomType, new HashSet<>());
            allocatedRooms.get(roomType).add(roomId);

            // Update inventory immediately
            inventory.updateAvailability(roomType, available - 1);

            System.out.println("Confirmed Reservation: Guest=" + request.getGuestName() +
                    ", RoomType=" + roomType +
                    ", RoomID=" + roomId);
        } else {
            System.out.println("Reservation Failed: No availability for " + roomType +
                    " (Guest=" + request.getGuestName() + ")");
        }
    }

    public void displayAllocations() {
        System.out.println("\nAllocated Rooms:");
        for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        // Initialize inventory
        Map<String, Integer> initialRooms = new HashMap<>();
        initialRooms.put("Single", 2);
        initialRooms.put("Double", 1);
        initialRooms.put("Suite", 1);

        RoomInventory inventory = new RoomInventory(initialRooms);

        // Booking request queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        bookingQueue.addRequest(new Reservation("Alice", "Single"));
        bookingQueue.addRequest(new Reservation("Bob", "Suite"));
        bookingQueue.addRequest(new Reservation("Charlie", "Single"));
        bookingQueue.addRequest(new Reservation("Diana", "Double"));
        bookingQueue.addRequest(new Reservation("Eve", "Suite")); // Will fail if no availability

        // Booking service processes requests
        BookingService bookingService = new BookingService(inventory);
        bookingService.processRequests(bookingQueue);

        // Display final allocations
        bookingService.displayAllocations();
    }
}