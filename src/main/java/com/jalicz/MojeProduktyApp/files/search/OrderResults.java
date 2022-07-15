package com.jalicz.MojeProduktyApp.files.search;

import com.jalicz.MojeProduktyApp.model.Potravina;
import com.jalicz.MojeProduktyApp.model.Produkt;

import java.util.Comparator;
import java.util.Map;

public enum OrderResults {
    BY_BEST_RESULTS(1, Texts.byBestResults, (p1, p2) -> {
        return p1.getValue().compareTo(p2.getValue());
    }),
    ALPHABETICALLY(2, Texts.alphabetically, (p1, p2) -> {
        return p1.getKey().name.compareTo(p2.getKey().name);
    }),
    BY_ID(3, Texts.byId, (p1, p2) -> {
        return Integer.compare(p1.getKey().id, p2.getKey().id);
    }),
    BY_WEIGHT(4, Texts.byWeight, (p1, p2) -> {
        return Integer.compare(p1.getKey().weight, p2.getKey().weight);
    }),
    BY_EXPIRATION_DATE(5, Texts.byExpirationDate, (p1, p2) -> {
        Potravina t1 = null, t2 = null;
        if(p1 instanceof Potravina) t1 = (Potravina) p1;
        if(p2 instanceof Potravina) t2 = (Potravina) p2;

        if(t1 == null && t2 == null) return 0;
        if(t1 == null) return -1;
        if(t2 == null) return 1;

        if(t1.expiration == null && t2.expiration == null) return 0;
        if(t1.expiration == null) return -1;
        if(t2.expiration == null) return 1;

        return t1.expiration.compareTo(t2.expiration);
    }),
    BY_REGISTRATION_DATE(6, Texts.byRegistrationDate, (p1, p2) -> {
        if(p1.getKey().registration == null && p2.getKey().registration == null) return 0;
        if(p1.getKey().registration == null) return -1;
        if(p2.getKey().registration == null) return 1;
        return p1.getKey().registration.compareTo(p2.getKey().registration);
    }),

    BY_BEST_RESULTS_INVERTED(-1, Texts.byBestResults, (p1, p2) -> {
        return p2.getValue().compareTo(p1.getValue());
    }),
    ALPHABETICALLY_INVERTED(-2, Texts.alphabetically, (p1, p2) -> {
        return p2.getKey().name.compareTo(p1.getKey().name);
    }),
    BY_ID_INVERTED(-3, Texts.byId, (p1, p2) -> {
        return Integer.compare(p2.getKey().id, p1.getKey().id);
    }),
    BY_WEIGHT_INVERTED(-4, Texts.byWeight, (p1, p2) -> {
        return Integer.compare(p2.getKey().weight, p1.getKey().weight);
    }),
    BY_EXPIRATION_DATE_INVERTED(-5, Texts.byExpirationDate, (p1, p2) -> {
        Potravina t1 = null, t2 = null;
        if(p1 instanceof Potravina) t1 = (Potravina) p1;
        if(p2 instanceof Potravina) t2 = (Potravina) p2;

        if(t1 == null && t2 == null) return 0;
        if(t1 == null) return 1;
        if(t2 == null) return -1;

        if(t1.expiration == null && t2.expiration == null) return 0;
        if(t1.expiration == null) return 1;
        if(t2.expiration == null) return -1;

        return t2.expiration.compareTo(t1.expiration);
    }),
    BY_REGISTRATION_DATE_INVERTED(-6, Texts.byRegistrationDate, (p1, p2) -> {
        if(p1.getKey().registration == null && p2.getKey().registration == null) return 0;
        if(p1.getKey().registration == null) return 1;
        if(p2.getKey().registration == null) return -1;
        return p2.getKey().registration.compareTo(p1.getKey().registration);
    });

    private static class Texts {
        public static final String
                byBestResults = "Nejlepších výsledků",
                alphabetically = "Abecedy",
                byId = "ID",
                byWeight = "Váhy",
                byExpirationDate = "Datumu expirace",
                byRegistrationDate = "Datumu registrace";
    }

    public static OrderResults DEFAULT = BY_BEST_RESULTS;

    public final int id;
    public final String text;
    public final Comparator<Map.Entry<Produkt, Integer>> comparator;
    public final boolean inverted;

    OrderResults(int id, String text, Comparator<Map.Entry<Produkt, Integer>> comparator) {
        this.id = id;
        this.text = text;
        this.comparator = comparator;
        this.inverted = id < 0;
    }

    public static OrderResults getById(int id) {
        for(OrderResults order: values()) if(order.id == id) return order;
        return DEFAULT;
    }

    @Override
    public String toString() {
        return text;
    }
}
