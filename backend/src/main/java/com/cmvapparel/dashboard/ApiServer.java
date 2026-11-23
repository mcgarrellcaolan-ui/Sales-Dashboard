package com.cmvapparel.dashboard;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.ArrayList;

// A small web server using Java's built-in HttpServer.
// Endpoints:
//   /api/summary
//   /api/brands
//   /api/top?n=5
//   /api/sales?from=2026-07-01&to=2026-07-31
public class ApiServer implements HttpHandler {

    private SalesService sales;

    public ApiServer(SalesService sales) {
        this.sales = sales;
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", this); // every /api/... request goes to handle()
        server.start();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/api/summary")) {
            String json = "{\"revenue\":" + round(sales.getRevenue())
                    + ",\"profit\":" + round(sales.getProfit())
                    + ",\"itemsSold\":" + sales.getItemsSold()
                    + ",\"inStock\":" + sales.getInStock() + "}";
            send(exchange, 200, json);

        } else if (path.equals("/api/brands")) {
            send(exchange, 200, brandsToJson(sales.getBrands()));

        } else if (path.equals("/api/top")) {
            try {
                int n = Integer.parseInt(getParam(exchange, "n", "5"));
                send(exchange, 200, itemsToJson(sales.getTopByProfit(n)));
            } catch (NumberFormatException e) {
                send(exchange, 400, "{\"error\":\"n must be a whole number\"}");
            }

        } else if (path.equals("/api/sales")) {
            try {
                LocalDate from = LocalDate.parse(getParam(exchange, "from", "2000-01-01"));
                LocalDate to = LocalDate.parse(getParam(exchange, "to", "2100-01-01"));
                send(exchange, 200, itemsToJson(sales.getSoldBetween(from, to)));
            } catch (Exception e) {
                send(exchange, 400, "{\"error\":\"dates should look like 2026-07-01\"}");
            }

        } else {
            send(exchange, 404, "{\"error\":\"not found\"}");
        }
    }

    private String brandsToJson(ArrayList<BrandTotal> brands) {
        String json = "[";
        for (int i = 0; i < brands.size(); i++) {
            BrandTotal b = brands.get(i);
            if (i > 0) {
                json += ",";
            }
            json += "{\"brand\":\"" + b.brand + "\""
                    + ",\"revenue\":" + round(b.revenue)
                    + ",\"profit\":" + round(b.profit)
                    + ",\"items\":" + b.items + "}";
        }
        return json + "]";
    }

    private String itemsToJson(ArrayList<Item> items) {
        String json = "[";
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (i > 0) {
                json += ",";
            }
            json += "{\"description\":\"" + item.getDescription().replace("\"", "\\\"") + "\""
                    + ",\"brand\":\"" + item.getBrand() + "\""
                    + ",\"soldOn\":\"" + item.getSoldOn() + "\""
                    + ",\"price\":" + round(item.getPrice())
                    + ",\"profit\":" + round(item.getProfit()) + "}";
        }
        return json + "]";
    }

    // 2 decimal places
    private double round(double number) {
        return Math.round(number * 100) / 100.0;
    }

    // gets a value from the URL, e.g. 5 from ?n=5
    private String getParam(HttpExchange exchange, String name, String fallback) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            return fallback;
        }
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2 && parts[0].equals(name)) {
                return parts[1];
            }
        }
        return fallback;
    }

    private void send(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // lets the dashboard page call it
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream out = exchange.getResponseBody();
        out.write(bytes);
        out.close();
    }
}
