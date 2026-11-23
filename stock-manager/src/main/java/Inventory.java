import java.util.ArrayList;

// Holds all my stock and works out totals
public class Inventory {

    private ArrayList<Item> items;

    public Inventory(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Item> getAllItems() {
        return items;
    }

    public ArrayList<Item> getUnsoldItems() {
        ArrayList<Item> unsold = new ArrayList<>();
        for (Item item : items) {
            if (!item.isSold()) {
                unsold.add(item);
            }
        }
        return unsold;
    }

    // Linear search: checks every item, so O(n)
    public ArrayList<Item> search(String text) {
        ArrayList<Item> results = new ArrayList<>();
        for (Item item : items) {
            if (item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                results.add(item);
            }
        }
        return results;
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Item item : items) {
            if (item.isSold()) {
                total += item.getSalePrice();
            }
        }
        return total;
    }

    public double getTotalProfit() {
        double total = 0;
        for (Item item : items) {
            if (item.isSold()) {
                total += item.getProfit();
            }
        }
        return total;
    }

    public int countSold() {
        int count = 0;
        for (Item item : items) {
            if (item.isSold()) {
                count++;
            }
        }
        return count;
    }

    public int countInStock() {
        return items.size() - countSold();
    }

    // How much I paid for everything that hasn't sold yet
    public double getStockValue() {
        double total = 0;
        for (Item item : getUnsoldItems()) {
            total += item.getCost();
        }
        return total;
    }
}
