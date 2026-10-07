// Bar charts drawn with plain SVG.

function svg(tag, attrs) {
  var node = document.createElementNS("http://www.w3.org/2000/svg", tag);
  for (var key in attrs) node.setAttribute(key, attrs[key]);
  return node;
}

function svgText(text, attrs) {
  var node = svg("text", attrs);
  node.textContent = text;
  return node;
}

function addTooltip(bar, label, value) {
  var tip = $("tooltip");
  bar.style.cursor = "pointer";
  bar.addEventListener("mousemove", function (e) {
    tip.innerHTML = "";
    tip.append(make("strong", { text: label }), make("br"), value);
    tip.style.left = e.clientX + "px";
    tip.style.top = e.clientY + "px";
    tip.classList.add("show");
  });
  bar.addEventListener("mouseleave", function () { tip.classList.remove("show"); });
}

// Rounds the top of the axis up to 1, 2 or 5 times a power of ten.
function roundUp(value) {
  if (value <= 0) return 10;
  var power = Math.pow(10, Math.floor(Math.log10(value)));
  var n = value / power;
  return (n <= 1 ? 1 : n <= 2 ? 2 : n <= 5 ? 5 : 10) * power;
}

// Vertical bars. data = [{ label, value, color? }]
function barChart(box, data, color, format) {
  var width = 1000, height = 260;
  var left = 56, right = 12, top = 16, bottom = 30;
  var plotW = width - left - right;
  var plotH = height - top - bottom;
  var max = roundUp(Math.max.apply(null, data.map(function (d) { return d.value; }).concat(0)));
  var slot = plotW / data.length;
  var barW = Math.min(24, slot / 2);

  var chart = svg("svg", { class: "chart", viewBox: "0 0 " + width + " " + height });

  for (var i = 0; i <= 4; i++) {
    var y = top + plotH - plotH * i / 4;
    chart.appendChild(svg("line", { class: "grid-line", x1: left, x2: width - right, y1: y, y2: y }));
    chart.appendChild(svgText(format(max * i / 4), { class: "axis-label", x: left - 8, y: y + 4, "text-anchor": "end" }));
  }
  chart.appendChild(svg("line", { class: "axis-line", x1: left, x2: width - right, y1: top + plotH, y2: top + plotH }));

  data.forEach(function (d, i) {
    var x = left + slot * i + slot / 2;
    var h = Math.max(0, d.value / max * plotH);
    var bar = svg("rect", { x: x - barW / 2, y: top + plotH - h, width: barW, height: h, rx: 4, fill: d.color || color });
    addTooltip(bar, d.label, format(d.value));
    chart.appendChild(bar);
    chart.appendChild(svgText(d.label, { class: "axis-label", x: x, y: top + plotH + 18, "text-anchor": "middle" }));
  });

  box.innerHTML = "";
  box.appendChild(chart);
}

// Horizontal bars with the value printed at the end of each bar.
function rowChart(box, data, color, format) {
  var rowH = 30;
  var width = 1000, height = data.length * rowH + 20;
  var left = 140, right = 70;
  var max = Math.max.apply(null, data.map(function (d) { return d.value; }).concat(0)) || 1;

  var chart = svg("svg", { class: "chart", viewBox: "0 0 " + width + " " + height });

  data.forEach(function (d, i) {
    var y = 10 + i * rowH + rowH / 2;
    var w = Math.max(0, d.value / max * (width - left - right));
    var bar = svg("rect", { x: left, y: y - 9, width: w, height: 18, rx: 4, fill: color });
    addTooltip(bar, d.label, format(d.value));
    chart.appendChild(svgText(d.label, { class: "axis-label", x: left - 10, y: y + 4, "text-anchor": "end" }));
    chart.appendChild(bar);
    chart.appendChild(svgText(format(d.value), { class: "value-label", x: left + w + 8, y: y + 4 }));
  });

  box.innerHTML = "";
  box.appendChild(chart);
}
