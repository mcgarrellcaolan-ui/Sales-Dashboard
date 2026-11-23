package com.cmvapparel.dashboard;

import java.time.LocalDate;

// One item from my stock list
public class Item {

    private String description;
    private LocalDate soldOn; // null if it hasn't sold yet
    private double price;
    private double profit;

    public Item(String description, LocalDate soldOn, double price, double profit) {
        this.description = description;
        this.soldOn = soldOn;
        this.price = price;
        this.profit = profit;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getSoldOn() {
        return soldOn;
    }

    public double getPrice() {
        return price;
    }

    public double getProfit() {
        return profit;
    }

    public boolean isSold() {
        return soldOn != null;
    }

    public String getBrand() {
        return Brand.of(description);
    }
}
