// Reading the spreadsheet and saving it in the browser.

var DATA_KEY = "sales-dashboard-data";

var state = {
  inventory: [],
  expenses: [],
  returns: [],
  drawings: [],
  fileName: null,
  loadedAt: null,
  goals: [],
  otherIncome: 0
};

function asDate(value) {
  return value instanceof Date ? value : null;
}

function asNumber(value) {
  return typeof value === "number" && !isNaN(value) ? value : 0;
}

// Returns the sheet as rows of cells, starting below the header row.
// The header is found by looking for a known word in column A.
function sheetRows(sheet, headerText) {
  var start = 2;
  for (var r = 0; r < 6; r++) {
    var cell = sheet[XLSX.utils.encode_cell({ r: r, c: 0 })];
    if (cell && String(cell.v).trim().toLowerCase() === headerText.toLowerCase()) {
      start = r + 1;
      break;
    }
  }
  return XLSX.utils.sheet_to_json(sheet, { range: start, header: 1, raw: true, defval: null });
}

function readInventory(sheet) {
  return sheetRows(sheet, "#")
    .filter(function (row) { return row[1] != null; })
    .map(function (row) {
      return {
        description: row[1] || "",
        size: row[2] || "",
        boughtOn: asDate(row[3]),
        cost: asNumber(row[4]),
        soldOn: asDate(row[5]),
        price: asNumber(row[6]),
        postage: asNumber(row[7]),
        fees: asNumber(row[8]),
        profit: asNumber(row[9]),
        status: row[10] || "",
        cogs: asNumber(row[11])
      };
    });
}

// Expenses, Returns and Salary all start with a date column.
// Rows without a date (blank rows, the TOTAL row) are skipped.
function readDatedRows(sheet, toItem) {
  return sheetRows(sheet, "date")
    .filter(function (row) { return asDate(row[0]); })
    .map(toItem);
}

function readWorkbook(workbook, fileName) {
  var sheets = workbook.Sheets;

  state.inventory = sheets["Inventory Detail"] ? readInventory(sheets["Inventory Detail"]) : [];

  state.expenses = sheets["Expenses"] ? readDatedRows(sheets["Expenses"], function (row) {
    return { date: row[0], category: String(row[2] || "Uncategorised").trim(), amount: asNumber(row[3]) };
  }) : [];

  // Returns = postage paid to send stock back to a supplier.
  state.returns = sheets["Returns"] ? readDatedRows(sheets["Returns"], function (row) {
    return { date: row[0], amount: asNumber(row[2]) };
  }) : [];

  // The "Salary" sheet holds money taken out of the business.
  state.drawings = sheets["Salary"] ? readDatedRows(sheets["Salary"], function (row) {
    return { date: row[0], amount: asNumber(row[1]) };
  }) : [];

  state.fileName = fileName;
  state.loadedAt = new Date();
  saveData();
}

function saveData() {
  try {
    localStorage.setItem(DATA_KEY, JSON.stringify({
      inventory: state.inventory,
      expenses: state.expenses,
      returns: state.returns,
      drawings: state.drawings,
      fileName: state.fileName,
      loadedAt: state.loadedAt
    }));
  } catch (e) {}
}

// Loads the last spreadsheet from the browser. JSON turns dates into
// strings, so they are turned back into Date objects here.
function loadSavedData() {
  try {
    var saved = JSON.parse(localStorage.getItem(DATA_KEY));
    if (!saved || !saved.inventory.length) return false;

    function fixDates(rows, keys) {
      rows.forEach(function (row) {
        keys.forEach(function (k) { if (row[k]) row[k] = new Date(row[k]); });
      });
      return rows;
    }

    state.inventory = fixDates(saved.inventory, ["boughtOn", "soldOn"]);
    state.expenses = fixDates(saved.expenses || [], ["date"]);
    state.returns = fixDates(saved.returns || [], ["date"]);
    state.drawings = fixDates(saved.drawings || [], ["date"]);
    state.fileName = saved.fileName;
    state.loadedAt = new Date(saved.loadedAt);
    return true;
  } catch (e) {
    return false;
  }
}
