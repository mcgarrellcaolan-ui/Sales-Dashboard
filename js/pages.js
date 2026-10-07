// Fills in each tab of the dashboard from the stats.

var view = {
  monthlyMetric: "revenue",
  monthlyView: "amount",
  brandMetric: "revenue",
  search: "",
  status: "all",
  sortBy: "soldOn",
  sortAsc: false
};

var METRICS = {
  revenue: { title: "revenue", color: "var(--accent)", format: money },
  profit: { title: "profit", color: "var(--green)", format: money },
  items: { title: "items sold", color: "var(--yellow)", format: count }
};

function showSummary(s) {
  var tiles = [
    ["Revenue", money(s.revenue)],
    ["Net profit (after expenses)", money(s.netProfit)],
    ["Profit margin", percent(s.margin)],
    ["Items sold", count(s.itemsSold)],
    ["In stock", count(s.stockCount) + " items", money(s.stockValue) + " at cost"],
    ["Owner drawings", money(s.drawings), "not a business expense"]
  ];
  $("summary").replaceChildren(...tiles.map(function (t) {
    return make("div", { class: "tile" }, [
      make("div", { class: "label", text: t[0] }),
      make("div", { class: "value", text: t[1] }),
      make("div", { class: "sub", text: t[2] || "" })
    ]);
  }));
}

function showMonthly(s) {
  var metric = METRICS[view.monthlyMetric];
  var data = s.months.map(function (m, i) {
    return { label: MONTHS[i], value: m[view.monthlyMetric] };
  });

  $("monthlyTitle").textContent = "Monthly " + metric.title;
  $("monthlyTable").replaceChildren(makeTable(
    [{ label: "Month" }, { label: "Total", num: true }],
    data.map(function (d) { return [d.label, metric.format(d.value)]; })
  ));

  if (view.monthlyView === "percent") {
    var total = data.reduce(function (t, d) { return t + d.value; }, 0);
    data = data.map(function (d) { return { label: d.label, value: total ? d.value / total : 0 }; });
    barChart($("monthlyChart"), data, metric.color, percent);
  } else {
    barChart($("monthlyChart"), data, metric.color, metric.format);
  }
}

function showBrands(s) {
  var metric = METRICS[view.brandMetric];
  $("brandTitle").textContent = "Brand " + metric.title;

  var data = BRANDS.map(function (b) {
    return { label: b.name, value: s.brands[b.key][view.brandMetric], color: b.color };
  });
  barChart($("brandChart"), data, null, metric.format);

  $("brandChart").appendChild(make("div", { class: "legend" }, BRANDS.map(function (b) {
    return make("span", {}, [make("i", { style: "background:" + b.color }), document.createTextNode(b.name)]);
  })));

  $("brandTable").replaceChildren(makeTable(
    [{ label: "Brand" }, { label: "Revenue", num: true }, { label: "Profit", num: true }, { label: "Items sold", num: true }],
    BRANDS.map(function (b) {
      var x = s.brands[b.key];
      return [b.name, money(x.revenue), money(x.profit), count(x.items)];
    })
  ));
}

function showExpenses(s) {
  var data = Object.keys(s.expenseCategories)
    .map(function (name) { return { label: name, value: s.expenseCategories[name] }; })
    .sort(function (a, b) { return b.value - a.value; });

  if (data.length) {
    rowChart($("expenseChart"), data, "var(--orange)", money);
  } else {
    $("expenseChart").replaceChildren(make("p", { class: "empty", text: "No expenses yet." }));
  }

  $("expenseTable").replaceChildren(makeTable(
    [{ label: "Category" }, { label: "Amount", num: true }, { label: "% of total", num: true }],
    data.map(function (d) { return [d.label, money(d.value), percent(d.value / s.expenses)]; })
  ));
}

function taxLines(box, title, tax) {
  var rows = [
    ["Income tax", money(tax.incomeTax)],
    ["Class 4 NI", money(tax.ni)],
    ["Total tax", money(tax.total), "total"],
    ["Take-home", money(tax.takeHome)],
    ["Effective rate", percent(tax.rate)]
  ];
  box.replaceChildren(make("h4", { text: title }), ...rows.map(function (r) {
    return make("div", { class: "line " + (r[2] || "") }, [make("span", { text: r[0] }), make("span", { text: r[1] })]);
  }));
}

function showTax(s) {
  var year = taxYearProgress();
  // Simple projection: profit so far scaled up to a full year.
  var projected = s.netProfit / year.fraction;

  $("taxProfit").textContent = money(s.netProfit);
  $("taxElapsed").textContent = year.daysGone + " of " + year.totalDays + " days (" + percent(year.fraction) + ")";
  $("taxProjected").textContent = money(projected);

  taxLines($("taxSoFar"), "So far", getTax(s.netProfit, state.otherIncome));
  taxLines($("taxFullYear"), "Full year (projected)", getTax(projected, state.otherIncome));

  $("taxNote").textContent =
    "Uses 2024/25 UK rates for income tax and Class 4 National Insurance. " +
    "Personal allowance is £12,570, reduced above £100,000. " +
    "The full-year figure assumes the rest of the year goes like it has so far, " +
    "so it will be rough early on. This is a guide, not tax advice.";
}

var COLUMNS = [
  { key: "description", label: "Item" },
  { key: "size", label: "Size" },
  { key: "boughtOn", label: "Bought", format: shortDate },
  { key: "cost", label: "Cost", num: true, format: money },
  { key: "soldOn", label: "Sold", format: shortDate },
  { key: "price", label: "Sale price", num: true, format: money },
  { key: "profit", label: "Profit", num: true, format: money },
  { key: "status", label: "Status" }
];

function sortValue(value) {
  if (value instanceof Date) return value.getTime();
  if (typeof value === "string") return value.toLowerCase();
  return value == null ? -Infinity : value;
}

function showInventory() {
  var search = view.search.toLowerCase();
  var rows = state.inventory.filter(function (item) {
    if (view.status === "sold" && !isSold(item.status)) return false;
    if (view.status === "stock" && isSold(item.status)) return false;
    return item.description.toLowerCase().includes(search);
  });

  rows.sort(function (a, b) {
    var x = sortValue(a[view.sortBy]), y = sortValue(b[view.sortBy]);
    if (x === y) return 0;
    return (x < y) === view.sortAsc ? -1 : 1;
  });

  var head = make("tr", {}, COLUMNS.map(function (col) {
    var arrow = view.sortBy === col.key ? (view.sortAsc ? " ▲" : " ▼") : "";
    var th = make("th", { class: col.num ? "num" : "", text: col.label + arrow });
    th.addEventListener("click", function () {
      view.sortAsc = view.sortBy === col.key ? !view.sortAsc : false;
      view.sortBy = col.key;
      showInventory();
    });
    return th;
  }));

  var body = rows.map(function (item) {
    return make("tr", {}, COLUMNS.map(function (col) {
      var value = item[col.key];
      var text = col.format ? col.format(value) : String(value || "");
      if (col.key === "status") {
        var type = needsSaleDate(value) ? "warn" : isSold(value) ? "sold" : "stock";
        return make("td", {}, [make("span", { class: "status " + type, text: text })]);
      }
      return make("td", { class: col.num ? "num" : "", text: text });
    }));
  });
  if (!rows.length) {
    body = [make("tr", {}, [make("td", { colspan: COLUMNS.length, text: "No items match." })])];
  }

  var table = $("inventoryTable");
  table.tHead.replaceChildren(head);
  table.tBodies[0].replaceChildren(...body);
}

function showAll() {
  var s = getStats();
  showSummary(s);
  showMonthly(s);
  showBrands(s);
  showExpenses(s);
  showTax(s);
  showGoals(s);
  showInventory();
}
