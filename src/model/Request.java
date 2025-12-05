package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String userId;
    private final String itemId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private boolean approved;
    private boolean returned;

    public Request(String id, String userId, String itemId, LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.approved = false;
        this.returned = false;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean a) { approved = a; }
    public boolean isReturned() { return returned; }
    public void setReturned(boolean r) { returned = r; }

    @Override
    public String toString() {
        return String.format("Request[%s user=%s item=%s borrow=%s due=%s approved=%b returned=%b]",
            id, userId, itemId, borrowDate, dueDate, approved, returned);
    }
}
