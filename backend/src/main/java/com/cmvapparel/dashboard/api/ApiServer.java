package com.cmvapparel.dashboard.api;

import com.cmvapparel.dashboard.model.Item;
import com.cmvapparel.dashboard.service.BrandClassifier;
import com.cmvapparel.dashboard.service.SalesService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Endpoints:
//   /api/summary
//   /api/brands
//   /api/top?n=5
//   /api/sales?from=2026-07-01&to=2026-07-31
public class ApiServer {

    private final SalesService sales;

    public ApiServer(SalesService sales) {
        this.sales = sales;
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/summary", this::summary);
        server.createContext("/api/brands", this::brands);
        server.createContext("/api/top", this::top);
        server.createContext("/api/sales", this::salesBetween);
        server.start();
    }

    private void summary(HttpExchange exchange) throws IOException {
        SalesService.Summary s = sales.summary();
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("revenue", s.revenue());
        json.put("profit", s.profit());
        json.put("itemsSold", s.itemsSold());
        json.put("inStock", s.inStock());
        send(exchange, 200, json);
    }

    private void brands(HttpExchange exchange) throws IOException {
        List<Object> json = new ArrayList<>();
        for (SalesService.BrandTotal b : sales.brands()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("brand", b.brand);
            row.put("revenue", b.revenue);
            row.put("profit", b.profit);
            row.put("items", b.items);
            json.add(row);
        }
        send(exchange, 200, json);
    }

    private void top(HttpExchange exchange) throws IOException {
        try {
            int n = Integer.parseInt(getParam(exchange, "n", "5"));
            send(exchange, 200, toJson(sales.topByProfit(n)));
        } catch (NumberFormatException e) {
            send(exchange, 400, Map.of("error", "n must be a whole number"));
        }
    }

    private void salesBetween(HttpExchange exchange) throws IOException {
        try {
            LocalDate from = LocalDate.parse(getParam(exchange, "from", "2000-01-01"));
            LocalDate to = LocalDate.parse(getParam(exchange, "to", "2100-01-01"));
            send(exchange, 200, toJson(sales.soldBetween(from, to)));
        } catch (Exception e) {
            send(exchange, 400, Map.of("error", "dates should look like 2026-07-01"));
        }
    }

    private static List<Object> toJson(List<Item> items) {
        List<Object> json = new ArrayList<>();
        for (Item item : items) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("description", item.description());
            row.put("brand", BrandClassifier.brandOf(item.description()));
            row.put("soldOn", item.soldOn());
            row.put("price", item.price());
            row.put("profit", item.profit());
            json.add(row);
        }
        return json;
    }

    // gets a value from the query string e.g. n from ?n=5
    private static String getParam(HttpExchange exchange, String name, String fallback) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            return fallback;
        }
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts[0].equals(name) && parts.length == 2 && !parts[1].isBlank()) {
                return parts[1];
            }
        }
        return fallback;
    }

    private static void send(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = Json.write(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // so the dashboard page can call it
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
