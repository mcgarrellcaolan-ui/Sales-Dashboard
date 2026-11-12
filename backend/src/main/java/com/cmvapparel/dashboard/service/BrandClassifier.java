package com.cmvapparel.dashboard.service;

import java.util.regex.Pattern;

// Gets the brand from the item description, same patterns as stats.js on the website
public class BrandClassifier {

    private static final Pattern STONE_ISLAND = Pattern.compile("\\bstone\\s*island\\b|\\bsi\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern CP_COMPANY = Pattern.compile("\\bcp\\b|\\bc\\.p\\.?\\s*company\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern NAPAPIJRI = Pattern.compile("\\bnapa(pijri)?\\b", Pattern.CASE_INSENSITIVE);

    public static String brandOf(String description) {
        if (description == null) {
            return "Other";
        }
        if (STONE_ISLAND.matcher(description).find()) {
            return "Stone Island";
        }
        if (CP_COMPANY.matcher(description).find()) {
            return "CP Company";
        }
        if (NAPAPIJRI.matcher(description).find()) {
            return "Napapijri";
        }
        return "Other";
    }
}
