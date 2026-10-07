// Small helpers used across the app.

var MONTHS = ["Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar"];

// UK tax year runs 6 April to 5 April, so month 0 in the charts is April.
function monthIndex(date) {
  return (date.getMonth() + 9) % 12;
}

function money(n) {
  n = n || 0;
  var text = "£" + Math.abs(n).toLocaleString("en-GB", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  return n < 0 ? "(" + text + ")" : text;
}

function count(n) {
  return Math.round(n || 0).toLocaleString("en-GB");
}

function percent(n) {
  return ((n || 0) * 100).toFixed(1) + "%";
}

function shortDate(d) {
  if (!(d instanceof Date) || isNaN(d)) return "";
  return d.toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" });
}

function $(id) {
  return document.getElementById(id);
}

// make("div", { class: "tile", text: "Hi" }, [children])
function make(tag, attrs, children) {
  var node = document.createElement(tag);
  for (var key in attrs || {}) {
    if (key === "class") node.className = attrs[key];
    else if (key === "text") node.textContent = attrs[key];
    else node.setAttribute(key, attrs[key]);
  }
  (children || []).forEach(function (child) { node.appendChild(child); });
  return node;
}

// Builds a plain table. Columns are { label, num } and rows are arrays of text.
function makeTable(columns, rows) {
  var head = make("tr", {}, columns.map(function (c) {
    return make("th", { class: c.num ? "num" : "", text: c.label });
  }));
  var body = rows.map(function (row) {
    return make("tr", {}, row.map(function (cell, i) {
      return make("td", { class: columns[i].num ? "num" : "", text: cell });
    }));
  });
  return make("table", {}, [make("thead", {}, [head]), make("tbody", {}, body)]);
}

// Wires up a group of buttons where only one can be active at a time.
function onSwitch(id, callback) {
  $(id).addEventListener("click", function (e) {
    var btn = e.target.closest("button[data-value]");
    if (!btn) return;
    this.querySelectorAll("button").forEach(function (b) { b.classList.toggle("active", b === btn); });
    callback(btn.dataset.value);
  });
}

function resetSwitch(id, value) {
  $(id).querySelectorAll("button").forEach(function (b) {
    b.classList.toggle("active", b.dataset.value === value);
  });
}
