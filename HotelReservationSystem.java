import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category;
    boolean isBooked;

    public Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isBooked = false;
    }
}

class Booking {
    String customerName;
    int roomNumber;
    String category;
    double amountPaid;
    String paymentStatus;

    public Booking(String customerName, int roomNumber, String category, double amountPaid, String paymentStatus) {
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.category = category;
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus;
    }

    public String toString() {
        return customerName + "," + roomNumber + "," + category + "," + amountPaid + "," + paymentStatus;
    }
}

public class HotelReservationSystem {
    static List<Room> rooms = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static final String FILE_NAME = "bookings.txt";

    public static void main(String[] args) {
        loadRooms();
        loadBookings();

        int choice;

        do {
            System.out.println("\n===============================");
            System.out.println("  🏨 Welcome to Our Hotel System");
            System.out.println("===============================");
            System.out.println("1. Search Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel a Booking");
            System.out.println("4. View Booking Details");
            System.out.println("5. Exit");
            System.out.println("6. Check Out");
            System.out.print("Choose an option: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> searchRooms();
                case 2 -> bookRoom();
                case 3 -> cancelBooking();
                case 4 -> viewBookings();
                case 5 -> System.out.println("🙏 Thank you for visiting!");
                case 6 -> checkOut();
                default -> System.out.println("⚠️ Invalid option. Please try again.");
            }

        } while (choice != 5);

        saveBookings();
    }

    static void loadRooms() {
        for (int i = 101; i <= 105; i++) rooms.add(new Room(i, "Standard"));
        for (int i = 201; i <= 205; i++) rooms.add(new Room(i, "Deluxe"));
        for (int i = 301; i <= 305; i++) rooms.add(new Room(i, "Suite"));
    }

    static void searchRooms() {
        System.out.print("🔎 Enter room type (Standard / Deluxe / Suite): ");
        String category = scanner.nextLine();
        boolean found = false;

        for (Room r : rooms) {
            if (r.category.equalsIgnoreCase(category) && !r.isBooked) {
                System.out.println("✅ Available: Room " + r.roomNumber);
                found = true;
            }
        }

        if (!found) {
            System.out.println("❌ No rooms available in " + category + " category.");
        }
    }

    static void bookRoom() {
        System.out.print("👤 Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("🏷️ Choose room type (Standard / Deluxe / Suite): ");
        String category = scanner.nextLine();

        Room availableRoom = null;
        for (Room r : rooms) {
            if (!r.isBooked && r.category.equalsIgnoreCase(category)) {
                availableRoom = r;
                break;
            }
        }

        if (availableRoom == null) {
            System.out.println("❌ Sorry! All rooms are full in this category.");
            return;
        }

        double required = switch (category.toLowerCase()) {
            case "standard" -> 1000;
            case "deluxe" -> 2000;
            case "suite" -> 3000;
            default -> 0;
        };

        System.out.println("💰 Payment Required for " + category + ": ₹" + required);
        int attempts = 2;
        boolean paid = false;
        double amount = 0;

        while (attempts > 0) {
            System.out.print("💵 Your offer: ₹");
            amount = scanner.nextDouble(); scanner.nextLine();

            if (amount >= required) {
                paid = true;
                break;
            } else {
                attempts--;
                if (attempts > 0)
                    System.out.println("😕 Not enough. Try again. Remaining tries: " + attempts);
            }
        }

        if (!paid) {
            System.out.println("❌ Booking failed due to insufficient amount.");
            return;
        }

        availableRoom.isBooked = true;
        Booking booking = new Booking(name, availableRoom.roomNumber, category, amount, "Paid");
        bookings.add(booking);

        System.out.println("🎉 Booking Confirmed for Room " + availableRoom.roomNumber + "! Thank you, " + name + "!");
    }

    static void cancelBooking() {
        System.out.print("👤 Enter your name to cancel booking: ");
        String name = scanner.nextLine();
        boolean found = false;

        Iterator<Booking> it = bookings.iterator();
        while (it.hasNext()) {
            Booking b = it.next();
            if (b.customerName.equalsIgnoreCase(name)) {
                it.remove();

                for (Room r : rooms) {
                    if (r.roomNumber == b.roomNumber) {
                        r.isBooked = false;
                        break;
                    }
                }

                System.out.println("✅ Booking cancelled for " + name);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("❌ No booking found with that name.");
        }
    }

    static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("📭 No bookings made yet.");
        } else {
            System.out.println("\n📋 All Booking Records:");
            for (Booking b : bookings) {
                System.out.println("🧍 Name: " + b.customerName +
                        " | Room: " + b.roomNumber +
                        " | Type: " + b.category +
                        " | Paid: ₹" + b.amountPaid +
                        " | Status: " + b.paymentStatus);
            }
        }
    }

    static void checkOut() {
        System.out.print("👤 Enter your name to check out: ");
        String name = scanner.nextLine();
        boolean found = false;

        Iterator<Booking> it = bookings.iterator();
        while (it.hasNext()) {
            Booking b = it.next();
            if (b.customerName.equalsIgnoreCase(name)) {
                it.remove();

                for (Room r : rooms) {
                    if (r.roomNumber == b.roomNumber) {
                        r.isBooked = false;
                        break;
                    }
                }

                System.out.println("🧾 Check-Out completed for " + name + ". Hope you had a great stay!");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("❌ No active booking found for that name.");
        }
    }

    static void loadBookings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Booking b = new Booking(data[0], Integer.parseInt(data[1]), data[2],
                        Double.parseDouble(data[3]), data[4]);
                bookings.add(b);

                for (Room r : rooms) {
                    if (r.roomNumber == b.roomNumber) {
                        r.isBooked = true;
                        break;
                    }
                }
            }
        } catch (IOException ignored) {}
    }

    static void saveBookings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Booking b : bookings) {
                writer.write(b.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠️ Couldn't save bookings to file.");
        }
    }
}
