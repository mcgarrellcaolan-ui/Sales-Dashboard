import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

// Starts the program and shows the menu
public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        String fileName = findFile(args);
        Inventory inventory = new Inventory(CsvReader.read(fileName));
        SlowStockReport report = new SlowStockReport();

        System.out.println("CMVApparel Stock Manager");
        System.out.println("Loaded " + inventory.getAllItems().size() + " items from " + fileName);

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1. List all stock");
            System.out.println("2. Search");
            System.out.println("3. Mark an item as sold");
            System.out.println("4. Summary");
            System.out.println("5. Slow stock report");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    printItems(inventory.getAllItems());
                    break;
                case "2":
                    System.out.print("Search for: ");
                    printItems(inventory.search(scanner.nextLine().trim()));
                    break;
                case "3":
                    markAsSold(inventory);
                    break;
                case "4":
                    printSummary(inventory);
                    break;
                case "5":
                    printSlowStock(inventory, report);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Please enter a number from the menu.");
            }
        }
        System.out.println("Bye!");
    }

    // Uses the file given when running it, otherwise the sample file
    private static String findFile(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        if (new File("data/sample-data.csv").exists()) {
            return "data/sample-data.csv";
        }
        return "../data/sample-data.csv";
    }

    private static void printItems(ArrayList<Item> items) {
        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }
        System.out.printf("%-4s %-38s %-14s %9s  %s%n", "#", "Item", "Brand", "Cost", "Status");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String status = "In stock";
            if (item.isSold()) {
                status = String.format("Sold for £%.2f", item.getSalePrice());
            }
            System.out.printf("%-4d %-38s %-14s %9s  %s%n",
                    i + 1, item.getDescription(), item.getBrand(), money(item.getCost()), status);
        }
    }

    private static void markAsSold(Inventory inventory) {
        ArrayList<Item> unsold = inventory.getUnsoldItems();
        printItems(unsold);
        if (unsold.isEmpty()) {
            return;
        }

        int number = readNumber("Which item number sold? ");
        if (number < 1 || number > unsold.size()) {
            System.out.println("There's no item with that number.");
            return;
        }
        double price = readPrice("What did it sell for? £");

        Item item = unsold.get(number - 1);
        item.markAsSold(LocalDate.now(), price);
        System.out.println("Marked " + item.getDescription() + " as sold. Profit: " + money(item.getProfit()));
        System.out.println("(This is only kept until you exit. Update the spreadsheet to save it.)");
    }

    private static void printSummary(Inventory inventory) {
        System.out.println("Revenue:      " + money(inventory.getTotalRevenue()));
        System.out.println("Profit:       " + money(inventory.getTotalProfit()));
        System.out.println("Items sold:   " + inventory.countSold());
        System.out.println("In stock:     " + inventory.countInStock());
        System.out.println("Stock value:  " + money(inventory.getStockValue()));
    }

    private static void printSlowStock(Inventory inventory, SlowStockReport report) {
        int days = readNumber("Show items in stock for at least how many days? ");
        LocalDate today = LocalDate.now();
        ArrayList<Item> slow = report.getSlowItems(inventory, days, today);

        if (slow.isEmpty()) {
            System.out.println("Nothing has been in stock that long.");
            return;
        }
        System.out.printf("%-38s %5s %9s  %s%n", "Item", "Days", "Cost", "Suggestion");
        for (Item item : slow) {
            System.out.printf("%-38s %5d %9s  %s%n",
                    item.getDescription(), item.getDaysInStock(today), money(item.getCost()),
                    report.getSuggestion(item, today));
        }
        System.out.println(slow.size() + " items, " + money(report.getMoneyTiedUp(slow)) + " tied up in stock");
    }

    // Keeps asking until the user types a whole number
    private static int readNumber(String question) {
        while (true) {
            System.out.print(question);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please type a whole number, e.g. 60");
            }
        }
    }

    // Keeps asking until the user types a price that isn't negative
    private static double readPrice(String question) {
        while (true) {
            System.out.print(question);
            try {
                double price = Double.parseDouble(scanner.nextLine().trim());
                if (price >= 0) {
                    return price;
                }
            } catch (NumberFormatException e) {
                // not a number, so ask again below
            }
            System.out.println("Please type a price, e.g. 45.50");
        }
    }

    private static String money(double amount) {
        return String.format("£%.2f", amount);
    }
}
