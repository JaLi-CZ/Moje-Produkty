package com.jalicz.MojeProduktyApp.files.search;

import com.jalicz.MojeProduktyApp.model.Produkt;
import com.jalicz.MojeProduktyApp.response.MPCode;

import java.util.ArrayList;

public class SearchResult {

    public final MPCode code;
    public final String title, subtitle;
    public final ArrayList<Produkt> items;

    public SearchResult(MPCode code, String title, String subtitle, ArrayList<Produkt> items) {
        this.code = code;
        this.title = title;
        this.subtitle = subtitle;
        this.items = items;
    }
}
