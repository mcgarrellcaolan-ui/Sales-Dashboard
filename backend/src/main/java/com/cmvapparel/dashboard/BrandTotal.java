package com.cmvapparel.dashboard;

// Running totals for one brand
public class BrandTotal {

    public String brand;
    public double revenue = 0;
    public double profit = 0;
    public int items = 0;

    public BrandTotal(String brand) {
        this.brand = brand;
    }
}
