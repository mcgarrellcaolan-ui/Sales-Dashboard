package com.cmvapparel.dashboard;

import java.util.ArrayList;

public class App {

    public static void main(String[] args) throws Exception {
        String file = "../data/sample-data.xlsx";
        if (args.length > 0) {
            file = args[0];
        }

        ArrayList<Item> items = ExcelReader.read(file);
        System.out.println("Loaded " + items.size() + " items from " + file);

        ApiServer server = new ApiServer(new SalesService(items));
        server.start(8080);
        System.out.println("Running on http://localhost:8080/api/summary");
    }
}
