import java.util.HashMap;
import java.util.Map;

class RoomInventory {
    private Map<String, Integer> inventory;

    // Constructor initializes inventory with room types and counts
    public RoomInventory(Map<String, Integer> initialInventory) {
        this.inventory = new HashMap<>(initialInventory);
    }

    // Retrieve current availability for a specific room type
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Controlled update of availability
    public void updateAvailability(String roomType, int newCount) {
        if (newCount >= 0) {
            inventory.put(roomType, newCount);
        } else {
            System.out.println("Invalid update: availability cannot be negative.");
        }
    }

    // Display the entire inventory state
    public void displayInventory() {
        System.out.println("Current Room Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        // Initialize inventory with room types
        Map<String, Integer> initialRooms = new HashMap<>();
        initialRooms.put("Single", 10);
        initialRooms.put("Double", 5);
        initialRooms.put("Suite", 2);

        RoomInventory inventory = new RoomInventory(initialRooms);

        // Display initial state
        inventory.displayInventory();

        // Simulate booking a Single room
        System.out.println("\nBooking a Single room...");
        int singleAvailable = inventory.getAvailability("Single");
        inventory.updateAvailability("Single", singleAvailable - 1);

        // Display updated state
        inventory.displayInventory();

        // Add a new room type dynamically
        System.out.println("\nAdding Deluxe room type...");
        inventory.updateAvailability("Deluxe", 3);

        // Display final state
        inventory.displayInventory();
    }
}