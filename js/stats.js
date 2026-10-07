// Turns the spreadsheet rows into totals, plus the UK tax estimate.

var TAX_YEAR_START = new Date(2026, 3, 6); // 6 Apr 2026
var TAX_YEAR_END = new Date(2027, 3, 5);   // 5 Apr 2027

var BRANDS = [
  { key: "stoneIsland", name: "Stone Island", color: "var(--blue)", match: /\bstone\s*island\b|\bsi\b/i },
  { key: "cpCompany", name: "CP Company", color: "var(--orange)", match: /\bcp\b|\bc\.p\.?\s*company\b/i },
  { key: "napapijri", name: "Napapijri", color: "var(--green)", match: /\bnapa(pijri)?\b/i },
  { key: "other", name: "Other", color: "var(--muted)", match: /.*/ }
];

function getBrand(description) {
  for (var i = 0; i < BRANDS.length; i++) {
    if (BRANDS[i].match.test(description || "")) return BRANDS[i].key;
  }
}

function isSold(status) {
  return /sold/i.test(status || "");
}

function needsSaleDate(status) {
  return /needs sale date/i.test(status || "");
}

function sum(rows) {
  return rows.reduce(function (total, row) { return total + row.amount; }, 0);
}

function getStats() {
  var months = MONTHS.map(function () {
    return { revenue: 0, profit: 0, items: 0, costs: 0, netProfit: 0 };
  });

  var brands = {};
  BRANDS.forEach(function (b) { brands[b.key] = { revenue: 0, profit: 0, items: 0 }; });

  var s = { revenue: 0, profit: 0, sellingCosts: 0, itemsSold: 0, stockCount: 0, stockValue: 0 };

  state.inventory.forEach(function (item) {
    if (item.soldOn) {
      var m = months[monthIndex(item.soldOn)];
      var costs = item.cogs + item.postage + item.fees;
      m.revenue += item.price;
      m.profit += item.profit;
      m.items += 1;
      m.costs += costs;

      var b = brands[getBrand(item.description)];
      b.revenue += item.price;
      b.profit += item.profit;
      b.items += 1;

      s.revenue += item.price;
      s.profit += item.profit;
      s.sellingCosts += costs;
      s.itemsSold += 1;
    } else if (!item.status || /stock/i.test(item.status) && !isSold(item.status)) {
      s.stockCount += 1;
      s.stockValue += item.cost;
    }
  });

  // Group expenses by category, ignoring differences in capitals and spacing.
  var expenseCategories = {};
  state.expenses.forEach(function (e) {
    var name = e.category.replace(/\s+/g, " ").toLowerCase();
    name = name.charAt(0).toUpperCase() + name.slice(1);
    expenseCategories[name] = (expenseCategories[name] || 0) + e.amount;
    months[monthIndex(e.date)].costs += e.amount;
  });
  state.returns.forEach(function (r) {
    months[monthIndex(r.date)].costs += r.amount;
  });

  months.forEach(function (m) { m.netProfit = m.revenue - m.costs; });

  s.expenses = sum(state.expenses);
  s.returns = sum(state.returns);
  s.drawings = sum(state.drawings);
  s.netProfit = s.revenue - s.sellingCosts - s.expenses - s.returns;
  s.margin = s.revenue > 0 ? s.netProfit / s.revenue : 0;
  s.months = months;
  s.brands = brands;
  s.expenseCategories = expenseCategories;
  return s;
}

// ---- UK tax, using 2024/25 rates (frozen until 2028) ----

function personalAllowance(income) {
  // The £12,570 allowance drops by £1 for every £2 earned over £100k.
  if (income <= 100000) return 12570;
  return Math.max(0, 12570 - (income - 100000) / 2);
}

function incomeTax(income) {
  var taxable = Math.max(0, income - personalAllowance(income));
  var basic = Math.min(taxable, 37700);
  var higher = Math.min(taxable - basic, 74870);
  var additional = taxable - basic - higher;
  return basic * 0.2 + higher * 0.4 + additional * 0.45;
}

function class4NI(profit) {
  if (profit <= 12570) return 0;
  if (profit <= 50270) return (profit - 12570) * 0.06;
  return (50270 - 12570) * 0.06 + (profit - 50270) * 0.02;
}

// Tax on the business profit only. If there is other income (like a job),
// the profit sits on top of it, so it can fall into a higher band.
function getTax(profit, otherIncome) {
  profit = Math.max(0, profit);
  otherIncome = Math.max(0, otherIncome || 0);
  var income = incomeTax(otherIncome + profit) - incomeTax(otherIncome);
  var ni = class4NI(profit);
  var total = income + ni;
  return {
    incomeTax: income,
    ni: ni,
    total: total,
    takeHome: profit - total,
    rate: profit > 0 ? total / profit : 0
  };
}

function taxYearProgress() {
  var day = 86400000;
  var totalDays = Math.round((TAX_YEAR_END - TAX_YEAR_START) / day) + 1;
  var daysGone = Math.round((new Date() - TAX_YEAR_START) / day) + 1;
  daysGone = Math.max(1, Math.min(totalDays, daysGone));
  return { daysGone: daysGone, totalDays: totalDays, fraction: daysGone / totalDays };
}
