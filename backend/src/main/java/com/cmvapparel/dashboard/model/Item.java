package com.cmvapparel.dashboard.model;

import java.time.LocalDate;

// One item from the stock list. soldOn is null if it hasn't sold yet.
public record Item(String description, String size, LocalDate boughtOn, double cost,
                   LocalDate soldOn, double price, double profit) {

    public boolean isSold() {
        return soldOn != null;
    }
}
