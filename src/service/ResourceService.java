package service;

import model.Item;
import model.User;
import data.DataStore;
import exceptions.InvalidInputException;
import exceptions.ItemNotAvailableException;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class ResourceService {
    private final Map<String, Item> items;
    private final DataStore store;
    // item name pattern: letters, numbers, spaces, dash, underscore, 2..40 chars
    private static final Pattern ITEM_NAME = Pattern.compile("^[A-Za-z0-9 _-]{2,40}$");

    public ResourceService(DataStore store) {
        this.store = store;
        this.items = store.loadItems();
    }

    public Item addItem(String id, String name, int qty, String addedByUserId, String addedByUserName)
            throws InvalidInputException {

        if (!ITEM_NAME.matcher(name).matches())
            throw new InvalidInputException("Invalid item name");

        if (qty <= 0)
            throw new InvalidInputException("Quantity must be positive");

        if (items.containsKey(id)) {
            // If existing, just increase qty — do NOT overwrite addedBy fields
            Item it = items.get(id);
            it.setQuantity(it.getQuantity() + qty);
            store.saveItems(items);
            return it;
        }

        Item it = new Item(id, name, qty, addedByUserId, addedByUserName);
        items.put(id, it);
        store.saveItems(items);
        return it;
    }


    public void reduceQuantity(String id) throws ItemNotAvailableException {
        Item it = items.get(id);
        if (it == null || it.getQuantity() <= 0) throw new ItemNotAvailableException("Item not available");
        it.setQuantity(it.getQuantity() - 1);
        store.saveItems(items);
    }

    public void increaseQuantity(String id) {
        Item it = items.get(id);
        if (it != null) {
            it.setQuantity(it.getQuantity() + 1);
            store.saveItems(items);
        }
    }

    public List<Item> listAll() {
        return new ArrayList<>(items.values());
    }

    public Item getItem(String id) {
        return items.get(id);
    }

    // Generic utility method for filtering using wildcard generics
    public static <T extends Item> List<T> filterByNameWildcard(Collection<T> coll, String pattern) {
        String lower = pattern.toLowerCase();
        return coll.stream()
            .filter(i -> i.getName().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    // custom comparator example: sort by name
    public List<Item> sortedByName() {
        List<Item> l = listAll();
        l.sort(Comparator.comparing(Item::getName));
        return l;
    }

    public Map<String, Item> internalMap() { return items; }

    public void deleteItem(String itemId, User requester, RequestService requestService) throws Exception {
        Item it = items.get(itemId);
        if (it == null)
            throw new Exception("Item not found.");

        // If not admin, user must be the creator
        if (!"admin".equals(requester.getId())) {
            if (!Objects.equals(it.getAddedByUserId(), requester.getId())) {
                throw new Exception("You can only delete items that you added.");
            }
        }

        // Block deletion if item is currently borrowed
        boolean borrowed = requestService.listRequests()
            .stream()
            .anyMatch(r -> r.getItemId().equals(itemId) && r.isApproved() && !r.isReturned());

        if (borrowed) {
            throw new Exception("This resource is currently borrowed and cannot be deleted.");
        }

        items.remove(itemId);
        store.saveItems(items);
    }

}
