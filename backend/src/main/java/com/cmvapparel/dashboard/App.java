package com.cmvapparel.dashboard;

import com.cmvapparel.dashboard.api.ApiServer;
import com.cmvapparel.dashboard.io.ExcelLoader;
import com.cmvapparel.dashboard.model.Item;
import com.cmvapparel.dashboard.service.SalesService;

import java.util.List;

public class App {

    public static void main(String[] args) throws Exception {
        String path = "../data/sample-data.xlsx";
        if (args.length > 0) {
            path = args[0];
        }

        List<Item> items = ExcelLoader.load(path);
        System.out.println("Loaded " + items.size() + " items from " + path);

        new ApiServer(new SalesService(items)).start(8080);
        System.out.println("Running on http://localhost:8080/api/summary");
    }
}
