# Reseller Sales Dashboard

A single-file, privacy-first analytics dashboard for a clothing/collectibles reselling
side-hustle. Point it at a spreadsheet export of your inventory and it turns it into a
full set of profit, stock and tax-estimate views — entirely in the browser.

**[Live demo](https://mcgarrellcaolan-ui.github.io/Sales-Dashboard/)** — loads a sample dataset automatically, or drag in your own spreadsheet.

## Why

I run a small reselling business (Stone Island / CP Company / Napapijri )
and kept my books in a spreadsheet. This dashboard reads that spreadsheet and gives me the
view a proper sales tool would — revenue trends, profit margin, stock on hand, a brand
breakdown, and a rough self-employment tax estimate — without needing a backend, a database,
or a monthly SaaS bill.

## Features

- **Overview** — YTD revenue, net profit, margin, items sold, current stock value, owner
  drawings, and a monthly revenue/profit/items-sold chart.
- **Brand breakdown** — automatic classification of inventory by brand from the item
  description, with per-brand revenue and profit.
- **Category rollup** — a dedicated view for any single product category (configurable via
  a regex), covering both sold and in-stock items.
- **Expenses & tax** — expense tracking by category and a UK self-employment income tax /
  Class 4 NIC estimate, with an optional "other taxable income" input for anyone with a
  second job.
- **Goals** — simple savings/revenue goal tracking.
- **Inventory** — a searchable, sortable table of every item, filterable by Sold / In stock,
  with a visual flag for rows that are missing data (e.g. a sale price entered without a
  sale date) so they don't silently fall out of your totals.

## How it works

- **No backend, no build step.** It's one HTML file with the parsing logic and the
  [SheetJS](https://sheetjs.com/) library bundled inline. Open it in a browser and it runs.
- **Nothing leaves your machine.** The spreadsheet you load is parsed client-side with
  `FileReader` + SheetJS; nothing is uploaded anywhere. That's also why it's safe to host
  this repo publicly — the code has no access to anyone's real financial data unless they
  explicitly load their own file into their own browser tab.
- **Spreadsheet-driven.** All the data — inventory, expenses, returns, salary/drawings —
  comes from four sheets in an `.xlsx` file (`Inventory Detail`, `Expenses`, `Returns`,
  `Salary`). [`sample-data.xlsx`](sample-data.xlsx) in this repo shows the expected shape;
  copy its headers to use it with your own numbers.

## Running it locally

No install required:

```bash
git clone https://github.com/<your-username>/reseller-sales-dashboard.git
cd reseller-sales-dashboard
```

Then just open `index.html` in a browser, or serve the folder with anything static, e.g.:

```bash
python -m http.server 8000
```

Click **Load / refresh from spreadsheet** and pick `sample-data.xlsx` (or your own file
in the same format).

## Tech

Vanilla JavaScript, inline SVG charts, no framework, no build step, no dependencies beyond
the bundled SheetJS parser. Everything — state, rendering, chart drawing, UK tax-bracket
math — is hand-rolled in a single file on purpose, as a constraint: see how far a real,
useful tool can go without reaching for a framework.

## Disclaimer

The tax figures are estimates only, based on 2024/25 UK income tax and Class 4 National
Insurance rates, and are not financial or tax advice.
