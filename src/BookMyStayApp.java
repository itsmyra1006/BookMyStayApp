import java.util.*;

class Service {
    private String name;
    private double cost;

    public Service(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return name + " (₹" + cost + ")";
    }
}

class Reservation {
    private String reservationId;
    private String guestName;

    public Reservation(String reservationId, String guestName) {
        this.reservationId = reservationId;
        this.guestName = guestName;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }
}

class AddOnServiceManager {
    // Map reservation ID → list of services
    private Map<String, List<Service>> reservationServices = new HashMap<>();

    public void addServiceToReservation(String reservationId, Service service) {
        reservationServices
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public List<Service> getServicesForReservation(String reservationId) {
        return reservationServices.getOrDefault(reservationId, Collections.emptyList());
    }

    public double calculateAdditionalCost(String reservationId) {
        return getServicesForReservation(reservationId)
                .stream()
                .mapToDouble(Service::getCost)
                .sum();
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        // Example reservation
        Reservation r1 = new Reservation("R001", "Alice");

        // Services
        Service breakfast = new Service("Breakfast Buffet", 500);
        Service spa = new Service("Spa Session", 1500);
        Service airportPickup = new Service("Airport Pickup", 800);

        // Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Guest selects services
        manager.addServiceToReservation(r1.getReservationId(), breakfast);
        manager.addServiceToReservation(r1.getReservationId(), spa);

        // Display services
        System.out.println("Reservation ID: " + r1.getReservationId());
        System.out.println("Guest: " + r1.getGuestName());
        System.out.println("Selected Services: " + manager.getServicesForReservation(r1.getReservationId()));
        System.out.println("Additional Cost: ₹" + manager.calculateAdditionalCost(r1.getReservationId()));
    }
}