# Java backend

A small REST API for the dashboard, written in Java 17 with no frameworks. It reads my sales
from `data/sales.csv` and returns JSON, which the **Java API** tab on the dashboard shows.

I wrote the data structures myself to practise what I learned in my Algorithms and Data Structures module:

| Data structure | Used for | Big-O |
|---|---|---|
| Hash table | adding up sales for each brand | O(1) average |
| Merge sort | ranking brands, sorting sales by date | O(n log n) |
| Binary search | finding sales between two dates | O(log n) |
| Min-heap | top 5 most profitable sales | O(n log k) |

## Endpoints

- `/api/summary` - revenue, profit, items sold, items in stock
- `/api/brands` - brands ranked by revenue
- `/api/top?n=5` - most profitable sales
- `/api/sales?from=2026-07-01&to=2026-07-31` - sales between two dates

## Running it

Needs Java 17+ and Maven (or open the `backend` folder in IntelliJ).

```
cd backend
mvn test
mvn exec:java
```

Then open `index.html` and go to the Java API tab. The API runs on port 8080.

## Using my own data

Save the Inventory sheet as `data/sales.csv` with the columns
`description,size,boughtOn,cost,soldOn,price,profit` (dates as 2026-07-26).
