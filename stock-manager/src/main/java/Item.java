import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// One item from my stock list.
// The fields are private (encapsulation), so they can only be changed through methods.
public class Item {

    private String description;
    private String size;
    private LocalDate dateBought;
    private double cost;
    private LocalDate dateSold; // null if it hasn't sold yet
    private double salePrice;
    private double profit;

    // Constructor for an item that is still in stock
    public Item(String description, String size, LocalDate dateBought, double cost) {
        this.description = description;
        this.size = size;
        this.dateBought = dateBought;
        this.cost = cost;
        this.dateSold = null;
        this.salePrice = 0;
        this.profit = 0;
    }

    // Constructor for an item that has already sold (overloading: same name, different parameters)
    public Item(String description, String size, LocalDate dateBought, double cost,
                LocalDate dateSold, double salePrice, double profit) {
        this.description = description;
        this.size = size;
        this.dateBought = dateBought;
        this.cost = cost;
        this.dateSold = dateSold;
        this.salePrice = salePrice;
        this.profit = profit;
    }

    public String getDescription() {
        return description;
    }

    public String getSize() {
        return size;
    }

    public LocalDate getDateBought() {
        return dateBought;
    }

    public double getCost() {
        return cost;
    }

    public LocalDate getDateSold() {
        return dateSold;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public double getProfit() {
        return profit;
    }

    public boolean isSold() {
        return dateSold != null;
    }

    public void markAsSold(LocalDate date, double price) {
        dateSold = date;
        salePrice = price;
        profit = price - cost;
    }

    // How many days the item has been in stock, up to the date given
    public long getDaysInStock(LocalDate today) {
        return ChronoUnit.DAYS.between(dateBought, today);
    }

    // The brand isn't its own column, so it's worked out from the description
    public String getBrand() {
        String text = description.toLowerCase();

        if (text.contains("stone island") || hasWord(text, "si")) {
            return "Stone Island";
        }
        if (text.contains("cp company") || text.contains("c.p. company") || hasWord(text, "cp")) {
            return "CP Company";
        }
        if (text.contains("napapijri") || hasWord(text, "napa")) {
            return "Napapijri";
        }
        return "Other";
    }

    // Checks for a whole word, so "si" inside "classic" doesn't count as Stone Island
    private boolean hasWord(String text, String word) {
        String[] words = text.split(" ");
        for (String w : words) {
            if (w.equals(word)) {
                return true;
            }
        }
        return false;
    }
}
