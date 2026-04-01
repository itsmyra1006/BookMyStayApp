import java.util.*;

// Reservation class (already exists in your system)
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

// BookingHistory stores confirmed reservations
class BookingHistory {
    private List<Reservation> confirmedBookings = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(confirmedBookings);
    }
}

// Reporting service generates summaries
class BookingReportService {
    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    public void displayAllBookings() {
        System.out.println("=== Booking History ===");
        for (Reservation r : history.getAllReservations()) {
            System.out.println(r);
        }
    }

    public void generateSummaryReport() {
        Map<String, Integer> roomTypeCounts = new HashMap<>();
        for (Reservation r : history.getAllReservations()) {
            roomTypeCounts.put(r.getRoomType(),
                    roomTypeCounts.getOrDefault(r.getRoomType(), 0) + 1);
        }

        System.out.println("=== Booking Summary Report ===");
        for (Map.Entry<String, Integer> entry : roomTypeCounts.entrySet()) {
            System.out.println("RoomType: " + entry.getKey() +
                    " | Total Bookings: " + entry.getValue());
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        BookingHistory history = new BookingHistory();

        // Simulate confirmed reservations
        Reservation r1 = new Reservation("R001", "Alice", "Single");
        Reservation r2 = new Reservation("R002", "Bob", "Suite");
        Reservation r3 = new Reservation("R003", "Charlie", "Double");

        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);

        // Reporting
        BookingReportService reportService = new BookingReportService(history);
        reportService.displayAllBookings();
        reportService.generateSummaryReport();
    }
} 