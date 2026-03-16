import java.util.HashMap;
import java.util.Map;

class Room {
    private String type;
    private double price;
    private String amenities;

    public Room(String type, double price, String amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public String getAmenities() {
        return amenities;
    }

    @Override
    public String toString() {
        return type + " | Price: $" + price + " | Amenities: " + amenities;
    }
}

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
        return new HashMap<>(inventory);
    }
}

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

public class BookMyStayApp {
    public static void main(String[] args) {
        Map<String, Integer> initialRooms = new HashMap<>();
        initialRooms.put("Single", 10);
        initialRooms.put("Double", 5);
        initialRooms.put("Suite", 2);

        RoomInventory inventory = new RoomInventory(initialRooms);

        Map<String, Room> roomDetails = new HashMap<>();
        roomDetails.put("Single", new Room("Single", 50.0, "WiFi, TV"));
        roomDetails.put("Double", new Room("Double", 90.0, "WiFi, TV, Mini-bar"));
        roomDetails.put("Suite", new Room("Suite", 200.0, "WiFi, TV, Mini-bar, Jacuzzi"));

        SearchService searchService = new SearchService(inventory, roomDetails);

        searchService.searchAvailableRooms();

        System.out.println("\nBooking a Single room...");
        int singleAvailable = inventory.getAvailability("Single");
        inventory.updateAvailability("Single", singleAvailable - 1);

        searchService.searchAvailableRooms();
    }
}