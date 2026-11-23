package com.cmvapparel.dashboard;

// Works out the brand from the item description
public class Brand {

    public static String of(String description) {
        // spaces at each end so " si " also matches at the start or end
        String text = " " + description.toLowerCase().replace(".", "").replace(",", " ") + " ";

        if (text.contains("stone island") || text.contains(" si ")) {
            return "Stone Island";
        }
        if (text.contains("cp company") || text.contains(" cp ")) {
            return "CP Company";
        }
        if (text.contains(" napa")) {
            return "Napapijri";
        }
        return "Other";
    }
}
