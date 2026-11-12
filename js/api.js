// Java API tab - gets data from the Java backend running on localhost:8080

var API_URL = "http://localhost:8080/api";
var apiTopN = 5;

function getJson(path) {
  return fetch(API_URL + path).then(function (res) {
    return res.json().then(function (body) {
      if (!res.ok) throw new Error(body.error || "Request failed");
      return body;
    });
  });
}

function itemRows(items) {
  return items.map(function (i) {
    return [i.description, i.brand, i.soldOn || "", money(i.price), money(i.profit)];
  });
}

var ITEM_COLUMNS = [
  { label: "Item" }, { label: "Brand" }, { label: "Sold" },
  { label: "Price", num: true }, { label: "Profit", num: true }
];

function showApiSummary(s) {
  var tiles = [
    ["Revenue", money(s.revenue)],
    ["Profit on sales", money(s.profit)],
    ["Items sold", count(s.itemsSold)],
    ["In stock", count(s.inStock)]
  ];
  $("apiSummary").replaceChildren(...tiles.map(function (t) {
    return make("div", { class: "tile" }, [
      make("div", { class: "label", text: t[0] }),
      make("div", { class: "value", text: t[1] })
    ]);
  }));
}

function showApiBrands(brands) {
  var rows = brands.map(function (b, i) {
    return [String(i + 1), b.brand, money(b.revenue), money(b.profit), count(b.items)];
  });
  $("apiBrands").replaceChildren(makeTable(
    [{ label: "#" }, { label: "Brand" }, { label: "Revenue", num: true }, { label: "Profit", num: true }, { label: "Items", num: true }],
    rows));
}

function loadApiTop() {
  getJson("/top?n=" + apiTopN).then(function (items) {
    $("apiTop").replaceChildren(makeTable(ITEM_COLUMNS, itemRows(items)));
  });
}

function loadApiSales() {
  var from = $("apiFrom").value, to = $("apiTo").value;
  getJson("/sales?from=" + from + "&to=" + to).then(function (items) {
    var revenue = 0, profit = 0;
    items.forEach(function (i) { revenue += i.price; profit += i.profit; });
    var line = make("p", { class: "empty", text: items.length + " sales, " + money(revenue) + " revenue, " + money(profit) + " profit" });
    $("apiSales").replaceChildren(line, makeTable(ITEM_COLUMNS, itemRows(items)));
  }).catch(function (err) {
    $("apiSales").replaceChildren(make("p", { class: "empty", text: err.message }));
  });
}

function loadApi() {
  $("apiStatus").textContent = "Connecting to the Java API...";
  getJson("/summary").then(function (s) {
    $("apiStatus").textContent = "Connected to the Java backend";
    $("apiContent").hidden = false;
    showApiSummary(s);
    getJson("/brands").then(showApiBrands);
    loadApiTop();
    loadApiSales();
  }).catch(function () {
    $("apiStatus").textContent = "The Java API isn't running. Start it from the backend folder to see this tab.";
    $("apiContent").hidden = true;
  });
}

var apiLoaded = false;
$("tabs").addEventListener("click", function (e) {
  var btn = e.target.closest("button[data-page]");
  if (btn && btn.dataset.page === "java" && !apiLoaded) {
    apiLoaded = true;
    loadApi();
  }
});

$("apiRefresh").addEventListener("click", loadApi);
$("apiRange").addEventListener("click", loadApiSales);
onSwitch("apiTopN", function (value) { apiTopN = Number(value); loadApiTop(); });
