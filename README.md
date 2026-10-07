# Sales Dashboard

A dashboard for my clothing resale business (Stone Island, CP Company, Napapijri).
I keep my stock and sales in an Excel spreadsheet, and this reads that file and shows
revenue, profit, stock, expenses and an estimate of the tax I owe.

**[Live demo](https://mcgarrellcaolan-ui.github.io/Sales-Dashboard/)** (uses made-up sample data)

## Features

- **Overview**: revenue, net profit, margin, items sold, stock value and a monthly chart
- **Brands**: sales and profit for each brand, worked out from the item description
- **Expenses & Tax**: expenses by category and a UK self-employed tax estimate
  (income tax + Class 4 NI)
- **Goals**: set revenue, profit or sales targets and track progress
- **Inventory**: search, filter and sort every item

The spreadsheet is read in the browser, so nothing is uploaded anywhere.

## Project structure

```
index.html            page layout and tabs
css/style.css         styling (light and dark mode)
js/helpers.js         formatting and small shared functions
js/data.js            reads the spreadsheet and saves it in the browser
js/stats.js           works out totals, brand split and tax
js/charts.js          bar charts drawn with SVG
js/pages.js           fills in each tab
js/goals.js           goals tab
js/main.js            button handlers and start-up
lib/xlsx.full.min.js  SheetJS, for reading Excel files
data/sample-data.xlsx fake data for the demo
```

## How it works

1. **Read** - the user picks an .xlsx file and SheetJS turns each sheet into rows.
2. **Clean** - each row becomes an object (item, cost, sale price, dates...) and blank rows are skipped.
3. **Calculate** - `getStats()` adds everything up by month, brand and expense category.
4. **Show** - each tab is drawn from those totals. Charts are plain SVG, no chart library.
5. **Save** - the data is kept in `localStorage` so it's still there next time.

## Spreadsheet format

The file needs four sheets: `Inventory Detail`, `Expenses`, `Returns` and `Salary`.
See [data/sample-data.xlsx](data/sample-data.xlsx) for the columns.

## Running it

```bash
git clone https://github.com/mcgarrellcaolan-ui/Sales-Dashboard.git
cd Sales-Dashboard
python -m http.server 8000
```

Then open http://localhost:8000.

## Built with

HTML, CSS and JavaScript, with no framework or build step. SheetJS is the only library.

Tax figures use 2024/25 UK rates and are estimates, not financial advice.
