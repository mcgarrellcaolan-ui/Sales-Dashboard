// Button handlers and start-up.

function showDashboard() {
  $("emptyState").style.display = "none";
  $("app").classList.add("ready");
  $("loadStatus").textContent = state.fileName + " · loaded " + state.loadedAt.toLocaleString("en-GB");
  showAll();
}

function openWorkbook(bytes, fileName) {
  var workbook = XLSX.read(new Uint8Array(bytes), { type: "array", cellDates: true });
  readWorkbook(workbook, fileName);
  showDashboard();
}

// Tabs
$("tabs").addEventListener("click", function (e) {
  var btn = e.target.closest("button[data-page]");
  if (!btn) return;
  document.querySelectorAll("[data-page]").forEach(function (el) {
    el.classList.toggle("active", el.dataset.page === btn.dataset.page);
  });
  window.scrollTo(0, 0);
});

// Loading a spreadsheet
$("loadBtn").addEventListener("click", function () {
  $("fileInput").click();
});

$("fileInput").addEventListener("change", function () {
  var file = this.files[0];
  if (!file) return;
  var reader = new FileReader();
  reader.onload = function () {
    try {
      openWorkbook(reader.result, file.name);
    } catch (err) {
      $("loadStatus").textContent = "Couldn't read that file: " + err.message;
    }
  };
  reader.readAsArrayBuffer(file);
  this.value = "";
});

// Chart options
onSwitch("monthlyMetric", function (value) { view.monthlyMetric = value; showMonthly(getStats()); });
onSwitch("monthlyView", function (value) { view.monthlyView = value; showMonthly(getStats()); });
onSwitch("brandMetric", function (value) { view.brandMetric = value; showBrands(getStats()); });

document.querySelectorAll(".table-toggle").forEach(function (btn) {
  btn.addEventListener("click", function () {
    $(btn.dataset.table).classList.toggle("open");
  });
});

// Tax
$("otherIncome").addEventListener("input", function () {
  state.otherIncome = parseFloat(this.value) || 0;
  showTax(getStats());
});

// Inventory filters
$("search").addEventListener("input", function () {
  view.search = this.value;
  showInventory();
});
onSwitch("statusFilter", function (value) { view.status = value; showInventory(); });

setupGoalForm();

// Start-up: use the last loaded spreadsheet, or the sample data on a first visit.
loadGoals();
if (loadSavedData()) {
  showDashboard();
} else {
  fetch("data/sample-data.xlsx")
    .then(function (res) {
      if (!res.ok) throw new Error("No sample data");
      return res.arrayBuffer();
    })
    .then(function (bytes) { openWorkbook(bytes, "sample-data.xlsx (demo)"); })
    .catch(function () {});
}
