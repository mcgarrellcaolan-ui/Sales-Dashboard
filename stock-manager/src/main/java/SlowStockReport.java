import java.time.LocalDate;
import java.util.ArrayList;

// Finds items that have been in stock too long and suggests what to do with them
public class SlowStockReport {

    private ArrayList<PricingRule> rules = new ArrayList<>();

    public SlowStockReport() {
        // Most serious first, because the first rule that applies is the one used
        rules.add(new SellAtCost());
        rules.add(new BundleDeal());
        rules.add(new Relist());
    }

    // Unsold items in stock for at least this many days, oldest first
    public ArrayList<Item> getSlowItems(Inventory inventory, int days, LocalDate today) {
        ArrayList<Item> slow = new ArrayList<>();
        for (Item item : inventory.getUnsoldItems()) {
            if (item.getDaysInStock(today) >= days) {
                slow.add(item);
            }
        }
        sortOldestFirst(slow);
        return slow;
    }

    // Polymorphism: each rule is a different class, but they're all used the same way
    public String getSuggestion(Item item, LocalDate today) {
        for (PricingRule rule : rules) {
            if (rule.appliesTo(item, today)) {
                return rule.getSuggestion(item);
            }
        }
        return "Keep it listed";
    }

    // How much money is tied up in these items
    public double getMoneyTiedUp(ArrayList<Item> slowItems) {
        double total = 0;
        for (Item item : slowItems) {
            total += item.getCost();
        }
        return total;
    }

    // Insertion sort by date bought.
    // Takes each item and moves it left until the item before it is older.
    // O(n^2) in the worst case, but fine for a small list like my stock.
    public static void sortOldestFirst(ArrayList<Item> list) {
        for (int i = 1; i < list.size(); i++) {
            Item current = list.get(i);
            int j = i - 1;

            while (j >= 0 && list.get(j).getDateBought().isAfter(current.getDateBought())) {
                list.set(j + 1, list.get(j)); // shift the newer item one place right
                j--;
            }
            list.set(j + 1, current);
        }
    }
}
