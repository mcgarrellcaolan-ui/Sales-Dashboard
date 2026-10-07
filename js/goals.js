// Savings / sales goals, saved in the browser.

var GOALS_KEY = "sales-dashboard-goals";

var GOAL_COLORS = {
  revenue: "var(--accent)",
  profit: "var(--green)",
  items: "var(--yellow)",
  custom: "var(--orange)"
};

function loadGoals() {
  try {
    state.goals = JSON.parse(localStorage.getItem(GOALS_KEY)) || [];
  } catch (e) {
    state.goals = [];
  }
}

function saveGoals() {
  try { localStorage.setItem(GOALS_KEY, JSON.stringify(state.goals)); } catch (e) {}
}

function goalMonth(goal) {
  return monthIndex(goal.deadline ? new Date(goal.deadline + "T00:00:00") : new Date());
}

function goalProgress(goal, s) {
  if (goal.metric === "custom") return goal.current || 0;
  if (goal.monthly) {
    var m = s.months[goalMonth(goal)];
    return { revenue: m.revenue, profit: m.netProfit, items: m.items }[goal.metric];
  }
  return { revenue: s.revenue, profit: s.netProfit, items: s.itemsSold }[goal.metric];
}

// "late" = deadline passed, "behind" = well behind where it should be by now.
function goalPace(goal, done) {
  if (done >= 1 || !goal.deadline) return "";
  var start = new Date(goal.createdAt);
  var end = new Date(goal.deadline + "T23:59:59");
  var now = new Date();
  if (now > end) return "late";
  var expected = (now - start) / (end - start);
  return done + 0.15 < expected ? "behind" : "";
}

function goalMessage(done, pace) {
  if (pace === "late") return "Deadline passed";
  if (done >= 1) return "Done!";
  if (pace === "behind") return "Behind pace";
  if (done >= 0.5) return "Over halfway";
  return "In progress";
}

function goalCard(goal, s) {
  var current = goalProgress(goal, s);
  var done = goal.target > 0 ? current / goal.target : 0;
  var pace = goalPace(goal, done);
  var format = goal.metric === "items" ? count : money;

  var remove = make("button", { text: "Remove" });
  remove.addEventListener("click", function () {
    state.goals = state.goals.filter(function (g) { return g !== goal; });
    saveGoals();
    showGoals(getStats());
  });

  var fill = make("div", { class: pace });
  fill.style.width = Math.min(100, done * 100) + "%";

  var card = make("div", { class: "goal" }, [
    make("div", { class: "goal-head" }, [make("b", { text: goal.name }), remove]),
    make("div", { class: "goal-pct", text: percent(done) }),
    make("div", { class: "bar" }, [fill]),
    make("div", { class: "goal-text", text: format(current) + " of " + format(goal.target) }),
    make("div", { class: "goal-status", text: goalMessage(done, pace) })
  ]);
  card.style.setProperty("--color", GOAL_COLORS[goal.metric]);

  if (goal.monthly && goal.metric !== "custom") {
    var month = new Date(2000, (goalMonth(goal) + 3) % 12, 1).toLocaleDateString("en-GB", { month: "long" });
    card.appendChild(make("div", { class: "goal-date", text: "For " + month }));
  } else if (goal.deadline) {
    card.appendChild(make("div", { class: "goal-date", text: "By " + shortDate(new Date(goal.deadline + "T00:00:00")) }));
  }

  // Custom goals aren't linked to the spreadsheet, so progress is typed in.
  if (goal.metric === "custom" && done < 1) {
    var input = make("input", { type: "number", min: "0", value: goal.current || 0 });
    var update = make("button", { class: "btn small", text: "Update" });
    update.addEventListener("click", function () {
      goal.current = parseFloat(input.value) || 0;
      saveGoals();
      showGoals(getStats());
    });
    card.appendChild(make("div", { class: "goal-update" }, [input, update]));
  }

  return card;
}

function showGoals(s) {
  var list = $("goalList");
  if (!state.goals.length) {
    list.replaceChildren(make("p", { class: "empty", text: "No goals yet. Add one above." }));
    return;
  }
  list.replaceChildren(...state.goals.map(function (goal) { return goalCard(goal, s); }));
}

var newGoal = { metric: "revenue", period: "year" };

function setupGoalForm() {
  onSwitch("goalMetric", function (value) {
    newGoal.metric = value;
    $("goalCurrentField").hidden = value !== "custom";
  });

  onSwitch("goalPeriod", function (value) {
    newGoal.period = value;
    $("goalDeadlineLabel").textContent = value === "month" ? "Month (optional)" : "By (optional)";
  });

  $("goalForm").addEventListener("submit", function (e) {
    e.preventDefault();
    var name = $("goalName").value.trim();
    var target = parseFloat($("goalTarget").value) || 0;
    if (!name || target <= 0) return;

    state.goals.push({
      name: name,
      metric: newGoal.metric,
      monthly: newGoal.period === "month",
      target: target,
      current: newGoal.metric === "custom" ? parseFloat($("goalCurrent").value) || 0 : 0,
      deadline: $("goalDeadline").value || null,
      createdAt: new Date().toISOString()
    });
    saveGoals();

    this.reset();
    newGoal = { metric: "revenue", period: "year" };
    resetSwitch("goalMetric", "revenue");
    resetSwitch("goalPeriod", "year");
    $("goalCurrentField").hidden = true;
    $("goalDeadlineLabel").textContent = "By (optional)";
    showGoals(getStats());
  });
}
