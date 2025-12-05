package model;

import java.io.Serializable;

public abstract class Resource<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String id;
    protected String name;
    protected T payload;

    public Resource(String id, String name, T payload) {
        this.id = id;
        this.name = name;
        this.payload = payload;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public T getPayload() { return payload; }

    public abstract String brief();
}
