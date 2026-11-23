package com.cmvapparel.dashboard;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// One item from my stock list
public class Item {

    private String description;
    private LocalDate boughtOn;
    private double cost;
    private LocalDate soldOn; // null if it hasn't sold yet
    private double price;
    private double profit;

    public Item(String description, LocalDate boughtOn, double cost,
                LocalDate soldOn, double price, double profit) {
        this.description = description;
        this.boughtOn = boughtOn;
        this.cost = cost;
        this.soldOn = soldOn;
        this.price = price;
        this.profit = profit;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getBoughtOn() {
        return boughtOn;
    }

    public double getCost() {
        return cost;
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

    // how many days it has been in stock, up to the given day
    public long getDaysInStock(LocalDate today) {
        return ChronoUnit.DAYS.between(boughtOn, today);
    }
}
