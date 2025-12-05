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

            // FIXED ADMIN LOGIN (hardcoded)
            if (id.equals("admin") && pw.equals("admin@123")) {
                System.out.println("Welcome System Administrator");
                User adminUser = new User("admin", "System Administrator", "admin@example.com", "admin@123");
                adminMenu(adminUser);
                return;
            }

            // NORMAL USER LOGIN
            User u = userService.login(id, pw);
            System.out.println("Welcome " + u.getName());
            userMenu(u);

        } catch (UserNotFoundException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }



    private void adminMenu(User u) {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Browse Resources");
            System.out.println("2. Search Resource");
            System.out.println("3. View Pending Requests");
            System.out.println("4. Approve Request");
            System.out.println("5. View Borrow Status (Returned / Not Returned / Overdue)");
            System.out.println("6. Delete Resource");
            System.out.println("0. Logout");
            System.out.print("Select: ");
            String s = scanner.nextLine().trim();
            try {
                switch (s) {
                    case "1": browse(); break;
                    case "2": searchResources(); break;
                    case "3": pendingRequests(); break;
                    case "4": approveRequest(); break;
                    case "5": viewBorrowStatus(); break;
                    case "6": deleteResource(u); break;
                    case "0": return;
                    default: System.out.println("Invalid");
                }
            } catch (Exception e) {
                System.out.println("Operation error: " + e.getMessage());
            }
        }
    }

    private void deleteResource(User u) {
        System.out.print("Enter item ID to delete: ");
        String id = scanner.nextLine().trim();

        try {
            resourceService.deleteItem(id, u, requestService);
            System.out.println("Resource deleted successfully.");
        } catch (Exception e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    private void viewBorrowStatus() {
        List<Request> all = requestService.listRequests();

        System.out.println("\n--- BORROW STATUS ---");
        for (Request r : all) {
            String status;

            if (!r.isApproved())
                status = "Pending Approval";
            else if (r.isReturned())
                status = "Returned";
            else if (r.getDueDate().isBefore(LocalDate.now()))
                status = "OVERDUE";
            else
                status = "Borrowed (Active)";

            System.out.printf("Request %s | User: %s | Item: %s | Borrow: %s | Due: %s | Status: %s%n",
                    r.getId(), r.getUserId(), r.getItemId(),
                    r.getBorrowDate(), r.getDueDate(), status);
        }
    }


    private void pendingRequests() {
        List<Request> list = requestService.listRequests().stream()
            .filter(r -> !r.isApproved())
            .toList();
        if (list.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        list.forEach(r -> System.out.println(r));
    }

    private void approveRequest() throws Exception {
        System.out.print("Enter request id to approve: ");
        String rid = scanner.nextLine().trim();
        requestService.approveRequest(rid);
        System.out.println("Approved successfully.");
    }
    

    private void userMenu(User u) {
        while (true) {
            System.out.println("\n--- User Menu (" + u.getName() + ") ---");
            System.out.println("1. Add Resource");
            System.out.println("2. Browse Resources");
            System.out.println("3. Search Resource by Name");
            System.out.println("4. Request Resource");
            System.out.println("5. My Borrows");
            System.out.println("6. Return Item");
            System.out.println("7. Delete My Resource");
            System.out.println("0. Logout");
            System.out.print("Select: ");

            String s = scanner.nextLine().trim();

            try {
                switch (s) {
                    case "1":
                        addResource(u);
                        break;

                    case "2":
                        browse();
                        break;

                    case "3":
                        searchResources();
                        break;

                    case "4":
                        requestResource(u);
                        break;

                    case "5":
                        viewMyBorrows(u);
                        break;

                    case "6":
                        returnItem();
                        break;

                    case "7":
                        deleteResource(u);
                        break;

                    case "0":
                        return;

                    default:
                        System.out.println("Invalid option. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Operation error: " + e.getMessage());
            }
        }
    }


    private void addResource(User u) throws InvalidInputException {
        System.out.print("Item id: "); 
        String id = scanner.nextLine().trim();

        System.out.print("Name: "); 
        String name = scanner.nextLine().trim();

        System.out.print("Quantity: "); 
        int q = Integer.parseInt(scanner.nextLine().trim());

        Item it = resourceService.addItem(id, name, q, u.getId(), u.getName());
        System.out.println("Added: " + it);
    }


    private void browse() {
        List<Item> all = resourceService.sortedByName();
        System.out.printf("%-10s %-20s %-5s %-15s %-15s%n",
                "ID", "Name", "Qty", "Added By", "User ID");

        for (Item it : all)
            System.out.printf("%-10s %-20s %-5d %-15s %-15s%n",
                    it.getId(), it.getName(), it.getQuantity(),
                    it.getAddedByUserName(), it.getAddedByUserId());
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

    private void searchResources() {
        System.out.print("Enter name pattern to search: ");
        String pattern = scanner.nextLine().trim();
        List<Item> filtered = ResourceService.filterByNameWildcard(resourceService.listAll(), pattern);
        if (filtered.isEmpty()) {
            System.out.println("No matching items found.");
            return;
        }
        System.out.printf("%-10s %-20s %-5s%n", "ID", "Name", "Qty");
        for (Item it : filtered)
            System.out.printf("%-10s %-20s %-5d%n", it.getId(), it.getName(), it.getQuantity());
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
