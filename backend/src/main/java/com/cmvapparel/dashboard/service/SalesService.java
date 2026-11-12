package com.cmvapparel.dashboard.service;

import com.cmvapparel.dashboard.datastructures.BinarySearch;
import com.cmvapparel.dashboard.datastructures.HashTable;
import com.cmvapparel.dashboard.datastructures.MergeSort;
import com.cmvapparel.dashboard.datastructures.MinHeap;
import com.cmvapparel.dashboard.model.Item;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SalesService {

    public record Summary(double revenue, double profit, int itemsSold, int inStock) {
    }

    public static class BrandTotal {
        public String brand;
        public double revenue;
        public double profit;
        public int items;

        BrandTotal(String brand) {
            this.brand = brand;
        }
    }

    private final List<Item> items;
    private final List<Item> soldByDate;

    public SalesService(List<Item> items) {
        this.items = items;

        List<Item> sold = new ArrayList<>();
        for (Item item : items) {
            if (item.isSold()) {
                sold.add(item);
            }
        }
        // sorted once here so binary search can be used in soldBetween
        soldByDate = MergeSort.sort(sold, Comparator.comparing(Item::soldOn));
    }

    public Summary summary() {
        double revenue = 0;
        double profit = 0;
        int sold = 0;
        int inStock = 0;

        for (Item item : items) {
            if (item.isSold()) {
                revenue += item.price();
                profit += item.profit();
                sold++;
            } else {
                inStock++;
            }
        }
        return new Summary(revenue, profit, sold, inStock);
    }

    // totals per brand using the hash table, then ranked by revenue with merge sort
    public List<BrandTotal> brands() {
        HashTable<String, BrandTotal> totals = new HashTable<>();

        for (Item item : soldByDate) {
            String brand = BrandClassifier.brandOf(item.description());
            BrandTotal total = totals.get(brand);
            if (total == null) {
                total = new BrandTotal(brand);
                totals.put(brand, total);
            }
            total.revenue += item.price();
            total.profit += item.profit();
            total.items++;
        }

        List<BrandTotal> list = new ArrayList<>();
        for (String brand : totals.keys()) {
            list.add(totals.get(brand));
        }
        Comparator<BrandTotal> byRevenue = Comparator.comparingDouble(b -> b.revenue);
        return MergeSort.sort(list, byRevenue.reversed());
    }

    // most profitable sales using the min-heap
    public List<Item> topByProfit(int n) {
        return MinHeap.topN(soldByDate, n, Comparator.comparingDouble(Item::profit));
    }

    // sales between two dates (both included) using binary search
    public List<Item> soldBetween(LocalDate from, LocalDate to) {
        int start = BinarySearch.firstOnOrAfter(soldByDate, from);
        int end = BinarySearch.firstOnOrAfter(soldByDate, to.plusDays(1));
        if (end < start) {
            end = start;
        }
        return new ArrayList<>(soldByDate.subList(start, end));
    }
}
