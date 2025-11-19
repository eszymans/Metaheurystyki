package org.example.model;

import java.util.List;

public class Backpack {
    private final int capacity;
    private final List<Item> items;

    public Backpack(int capacity, List<Item> items) {
        this.capacity = capacity;
        this.items = items;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Item> getItems() {
        return items;
    }
}
