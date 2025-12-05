package service;

import model.User;
import data.DataStore;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;

import java.util.*;
import java.util.regex.*;

public class UserService {
    private final Map<String, User> users;
    private final DataStore store;

    // simple regexes for demo
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    // password: at least 6 chars, one digit
    private static final Pattern PASSWORD = Pattern.compile("^(?=.*\\d).{6,}$");

    public UserService(DataStore ds) {
        this.store = ds;
        this.users = ds.loadUsers();
    }

    public User register(String id, String name, String email, String password) throws InvalidInputException {
        if (!EMAIL.matcher(email).matches())
            throw new InvalidInputException("Invalid email format");
        if (!PASSWORD.matcher(password).matches())
            throw new InvalidInputException("Password must be >=6 chars and contain a digit");
        if (users.containsKey(id)) throw new InvalidInputException("Id already used");
        User u = new User(id, name, email, password);
        users.put(id, u);
        store.saveUsers(users);
        return u;
    }

    public User login(String id, String password) throws UserNotFoundException {
        User u = users.get(id);
        if (u == null || !u.getPassword().equals(password)) throw new UserNotFoundException("Invalid credentials");
        return u;
    }

    public User getById(String id) throws UserNotFoundException {
        User u = users.get(id);
        if (u == null) throw new UserNotFoundException("User not found: " + id);
        return u;
    }

    public Collection<User> allUsers() {
        return users.values();
    }

    // used by autosave
    public Map<String, User> internalMap() { return users; }
}
