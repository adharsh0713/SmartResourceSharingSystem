package ui.console;

import data.DataStore;
import service.*;
import model.*;
import exceptions.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.regex.*;

public class ConsoleApp {
    private final Scanner scanner = new Scanner(System.in);
    private final DataStore store = new DataStore();
    private final UserService userService = new UserService(store);
    private final ResourceService resourceService = new ResourceService(store);
    private final RequestService requestService = new RequestService(store, resourceService, userService);
    private final AutoSaveThread autoSave;
    private final OverdueCheckThread overdueThread;

    public ConsoleApp() {
        autoSave = new AutoSaveThread(userService, resourceService, requestService);
        overdueThread = new OverdueCheckThread(requestService, false, (list) -> {
            System.out.println("=== OVERDUE ALERT ===");
            list.forEach(r -> System.out.println("Overdue: " + r));
            System.out.println("=====================");
        });
        autoSave.start();
        overdueThread.start();
    }

    public void run() {
        while (true) {
            System.out.println("\n=== Smart Resource Sharing - Console ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.print("Select: ");
            String opt = scanner.nextLine().trim();
            try {
                switch (opt) {
                    case "1": doRegister(); break;
                    case "2": doLogin(); break;
                    case "0":
                        shutdown();
                        return;
                    default: System.out.println("Invalid option");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void doRegister() throws InvalidInputException {
        System.out.print("Enter id: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password (>=6 chars, has digit): ");
        String pw = scanner.nextLine().trim();
        User u = userService.register(id, name, email, pw);
        System.out.println("Registered: " + u);
    }

    private void doLogin() {
        try {
            System.out.print("Id: ");
            String id = scanner.nextLine().trim();
            System.out.print("Password: ");
            String pw = scanner.nextLine().trim();
            User u = userService.login(id, pw);
            System.out.println("Welcome " + u.getName());
            userMenu(u);
        } catch (UserNotFoundException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void userMenu(User u) {
        while (true) {
            System.out.println("\n--- User Menu (" + u.getName() + ") ---");
            System.out.println("1. Add Resource");
            System.out.println("2. Browse Resources");
            System.out.println("3. Request Resource");
            System.out.println("4. My Borrows");
            System.out.println("5. Return Item");
            System.out.println("0. Logout");
            System.out.print("Select: ");
            String s = scanner.nextLine().trim();
            try {
                switch (s) {
                    case "1": addResource(); break;
                    case "2": browse(); break;
                    case "3": requestResource(u); break;
                    case "4": viewMyBorrows(u); break;
                    case "5": returnItem(); break;
                    case "0": return;
                    default: System.out.println("Invalid");
                }
            } catch (Exception e) {
                System.out.println("Operation error: " + e.getMessage());
            }
        }
    }

    private void addResource() throws InvalidInputException {
        System.out.print("Item id: "); String id = scanner.nextLine().trim();
        System.out.print("Name: "); String name = scanner.nextLine().trim();
        System.out.print("Quantity: "); int q = Integer.parseInt(scanner.nextLine().trim());
        Item it = resourceService.addItem(id, name, q);
        System.out.println("Added: " + it);
    }

    private void browse() {
        List<Item> all = resourceService.sortedByName();
        System.out.printf("%-10s %-20s %-5s%n", "ID", "Name", "Qty");
        for (Item it : all) System.out.printf("%-10s %-20s %-5d%n", it.getId(), it.getName(), it.getQuantity());
    }

    private void requestResource(User u) {
        System.out.print("Enter item id to request: ");
        String id = scanner.nextLine().trim();
        System.out.print("For how many days? ");
        int days = Integer.parseInt(scanner.nextLine().trim());
        try {
            Request r = requestService.requestItem(u.getId(), id, days);
            System.out.println("Request placed (pending approval): " + r.getId());
        } catch (ItemNotAvailableException e) {
            System.out.println("Request failed: " + e.getMessage());
        }
    }

    private void viewMyBorrows(User u) {
        List<Request> list = requestService.outstandingRequestsForUser(u.getId());
        if (list.isEmpty()) System.out.println("No current borrows.");
        for (Request r : list) {
            System.out.println(r);
            long daysLeft = java.time.Period.between(java.time.LocalDate.now(), r.getDueDate()).getDays();
            System.out.println("Days until due: " + daysLeft);
        }
    }

    private void returnItem() {
        System.out.print("Enter request id to return: ");
        String rid = scanner.nextLine().trim();
        try {
            requestService.returnItem(rid);
            System.out.println("Returned.");
        } catch (Exception e) {
            System.out.println("Return failed: " + e.getMessage());
        }
    }

    private void shutdown() {
        System.out.println("Shutting down...");
        autoSave.stopRunning();
        overdueThread.stopRunning();
    }

    public static void main(String[] args) {
        new ConsoleApp().run();
    }
}
