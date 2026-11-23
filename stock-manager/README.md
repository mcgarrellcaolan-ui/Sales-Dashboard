# Stock Manager (Java)

A console app for managing the stock in my clothing business. It reads my stock from the
spreadsheet (saved as CSV) and lets me search it, mark items as sold, see totals, and get a
**slow stock report** that shows what has been unsold too long and what to do about it.

I built it to practise what I've learned in my Java and Algorithms and Data Structures modules.

## Menu

```
1. List all stock
2. Search
3. Mark an item as sold
4. Summary
5. Slow stock report
0. Exit
```

## Classes

| Class | What it does | What it shows |
|---|---|---|
| `Item` | one item: description, size, cost, dates, sale price, profit | encapsulation, constructor overloading |
| `CsvReader` | reads the CSV file into a list of `Item`s | file handling, exceptions |
| `Inventory` | holds all the items, works out totals, searches | classes working together, linear search O(n) |
| `SlowStockReport` | finds slow items, sorts them oldest first, suggests what to do | insertion sort O(n²) |
| `PricingRule` | interface for a suggestion | interfaces |
| `Relist`, `BundleDeal`, `SellAtCost` | the three suggestions (60, 90 and 120+ days) | polymorphism |
| `Main` | the menu | `Scanner`, loops, `switch`, input checking |

## Running it

Open the `stock-manager` folder in IntelliJ and run `Main`, or use Maven:

```
cd stock-manager
mvn test
mvn compile exec:java
```

It reads `data/sample-data.csv` (made-up sample data).

## Using my own spreadsheet

Open the spreadsheet, go to the `Inventory Detail` sheet, then **File > Save As > CSV**.
Run it with the file name, e.g. `mvn compile exec:java -Dexec.args="../data/my-stock.csv"`.

Item descriptions can't contain commas, because commas separate the columns in a CSV file.
Items marked as sold are only kept until the program closes.
