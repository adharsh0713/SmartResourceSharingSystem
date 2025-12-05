package service;

import model.*;
import data.DataStore;
import exceptions.*;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestService {
    private final Map<String, Request> requests;
    private final List<Transaction> transactions;
    private final DataStore store;
    private final ResourceService resourceService;
    private final UserService userService;
    private final AtomicInteger reqCounter = new AtomicInteger(1);
    private final AtomicInteger txnCounter = new AtomicInteger(1);

    public RequestService(DataStore store, ResourceService rs, UserService us) {
        this.store = store;
        this.resourceService = rs;
        this.userService = us;
        this.requests = store.loadRequests();
        this.transactions = store.loadTransactions();
    }

    public Request requestItem(String userId, String itemId, int periodDays) throws ItemNotAvailableException {
        Item it = resourceService.getItem(itemId);
        if (it == null || it.getQuantity() <= 0) throw new ItemNotAvailableException("Not available");
        LocalDate borrow = LocalDate.now();
        LocalDate due = borrow.plusDays(periodDays);
        String rid = "R" + reqCounter.getAndIncrement() + System.currentTimeMillis()%1000;
        Request r = new Request(rid, userId, itemId, borrow, due);
        requests.put(rid, r);
        store.saveRequests(requests);
        return r;
    }

    public void approveRequest(String reqId) throws Exception {
        Request r = requests.get(reqId);
        if (r == null) throw new Exception("Request not found");
        if (r.isApproved()) return;
        resourceService.reduceQuantity(r.getItemId());
        r.setApproved(true);
        Transaction t = new Transaction("T" + txnCounter.getAndIncrement(), r.getId(), LocalDate.now(), "BORROW");
        transactions.add(t);
        store.saveRequests(requests);
        store.saveTransactions(transactions);
    }

    public void returnItem(String reqId) throws Exception {
        Request r = requests.get(reqId);
        if (r == null) throw new Exception("Request not found");
        if (!r.isApproved() || r.isReturned()) throw new Exception("Not active borrow");
        r.setReturned(true);
        resourceService.increaseQuantity(r.getItemId());
        Transaction t = new Transaction("T" + txnCounter.getAndIncrement(), r.getId(), LocalDate.now(), "RETURN");
        transactions.add(t);
        store.saveRequests(requests);
        store.saveTransactions(transactions);
    }

    public List<Request> listRequests() {
        return new ArrayList<>(requests.values());
    }

    public List<Request> outstandingRequestsForUser(String userId) {
        return requests.values().stream()
            .filter(r -> r.getUserId().equals(userId) && !r.isReturned() && r.isApproved())
            .collect(Collectors.toList());
    }

    public List<Request> overdueRequests() {
        LocalDate now = LocalDate.now();
        return requests.values().stream()
            .filter(r -> r.isApproved() && !r.isReturned() && r.getDueDate().isBefore(now))
            .collect(Collectors.toList());
    }

    public Map<String, Request> internalMap() { return requests; }
    public List<Transaction> internalTxns() { return transactions; }
}
