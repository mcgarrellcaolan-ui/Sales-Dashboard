package com.cmvapparel.dashboard;

import java.time.LocalDate;
import java.util.ArrayList;

// Does all the sums for the API
public class SalesService {

    private ArrayList<Item> items;
    private ArrayList<Item> soldByDate;

    public SalesService(ArrayList<Item> items) {
        this.items = items;

        ArrayList<Item> sold = new ArrayList<>();
        for (Item item : items) {
            if (item.isSold()) {
                sold.add(item);
            }
        }
        // sorted once here so binary search can be used in soldBetween
        soldByDate = MergeSort.sortByDate(sold);
    }

    public double getRevenue() {
        double total = 0;
        for (Item item : soldByDate) {
            total += item.getPrice();
        }
        return total;
    }

    public double getProfit() {
        double total = 0;
        for (Item item : soldByDate) {
            total += item.getProfit();
        }
        return total;
    }

    public int getItemsSold() {
        return soldByDate.size();
    }

    public int getInStock() {
        return items.size() - soldByDate.size();
    }

    // adds up each brand using the hash table, then puts the highest revenue first
    public ArrayList<BrandTotal> getBrands() {
        HashTable totals = new HashTable();
        for (Item item : soldByDate) {
            BrandTotal total = totals.get(item.getBrand());
            if (total == null) {
                total = new BrandTotal(item.getBrand());
                totals.put(item.getBrand(), total);
            }
            total.revenue += item.getPrice();
            total.profit += item.getProfit();
            total.items++;
        }

        // only a few brands, so a simple selection sort is fine here
        ArrayList<BrandTotal> list = totals.values();
        for (int i = 0; i < list.size(); i++) {
            int biggest = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).revenue > list.get(biggest).revenue) {
                    biggest = j;
                }
            }
            BrandTotal temp = list.get(i);
            list.set(i, list.get(biggest));
            list.set(biggest, temp);
        }
        return list;
    }

    // most profitable sales using the min-heap
    public ArrayList<Item> getTopByProfit(int n) {
        return MinHeap.topN(soldByDate, n);
    }

    // sales between two dates (both included) using binary search
    public ArrayList<Item> getSoldBetween(LocalDate from, LocalDate to) {
        int start = BinarySearch.firstOnOrAfter(soldByDate, from);
        int end = BinarySearch.firstOnOrAfter(soldByDate, to.plusDays(1));

        ArrayList<Item> result = new ArrayList<>();
        for (int i = start; i < end; i++) {
            result.add(soldByDate.get(i));
        }
        return result;
    }
}
