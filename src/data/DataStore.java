package data;

import model.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class DataStore {
    private static final String DIR = "data-files";
    private static final String USERS = DIR + "/users.dat";
    private static final String ITEMS = DIR + "/items.dat";
    private static final String REQUESTS = DIR + "/requests.dat";
    private static final String TXNS = DIR + "/transactions.dat";

    public DataStore() {
        try {
            Files.createDirectories(Paths.get(DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generic read/write
    @SuppressWarnings("unchecked")
    private <T> T readObject(String path, T defaultVal) {
        File f = new File(path);
        if (!f.exists()) return defaultVal;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return defaultVal;
        }
    }

    private <T> void writeObject(String path, T obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Users
    public Map<String, model.User> loadUsers() {
        Map<String, model.User> m = readObject(USERS, new HashMap<String, model.User>());
        return m;
    }
    public void saveUsers(Map<String, model.User> users) {
        writeObject(USERS, users);
    }

    // Items
    public Map<String, model.Item> loadItems() {
        Map<String, model.Item> m = readObject(ITEMS, new HashMap<String, model.Item>());
        return m;
    }
    public void saveItems(Map<String, model.Item> items) { writeObject(ITEMS, items); }

    // Requests
    public Map<String, model.Request> loadRequests() {
        Map<String, model.Request> m = readObject(REQUESTS, new HashMap<String, model.Request>());
        return m;
    }
    public void saveRequests(Map<String, model.Request> requests) { writeObject(REQUESTS, requests); }

    // Transactions
    public List<model.Transaction> loadTransactions() {
        List<model.Transaction> l = readObject(TXNS, new ArrayList<model.Transaction>());
        return l;
    }
    public void saveTransactions(List<model.Transaction> txns) { writeObject(TXNS, txns); }
}
