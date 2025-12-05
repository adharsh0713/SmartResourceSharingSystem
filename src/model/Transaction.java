package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String requestId;
    private final LocalDate timestamp;
    private final String type; // "BORROW" or "RETURN"

    public Transaction(String id, String requestId, LocalDate ts, String type) {
        this.id = id;
        this.requestId = requestId;
        this.timestamp = ts;
        this.type = type;
    }

    public String getId(){ return id; }
    public String getRequestId(){ return requestId; }
    public LocalDate getTimestamp(){ return timestamp; }
    public String getType(){ return type; }

    @Override
    public String toString(){
        return String.format("Txn[%s req=%s time=%s type=%s]", id, requestId, timestamp, type);
    }
}
