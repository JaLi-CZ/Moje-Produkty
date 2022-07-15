package com.jalicz.MojeProduktyApp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Potravina extends Produkt {

    /*
      Filename will look like: "[ID].potravina"
      Structure of file will look like:
        name
        parentId
        weight
        manufacturer
        registrationDate (dd-mm-yyyy/hh-nn-ss)
        notes
        foodWeight --EXTRA--
        expirationDate (dd-mm-yyyy) --EXTRA--
     */

    public final int foodWeight; // -1 == non-specified
    public final LocalDate expiration;
    public final int daysToExpiration;
    public final boolean isExpired;
    public final String expirationString;
    public final String manufacturer; // option box

    public Potravina(int id, String name, int parentId, int weight, String manufacturer, LocalDateTime registration, String registrationString,
                     ArrayList<String> notes, String notesString, int foodWeight, LocalDate expiration, String expirationString) {
        super(id, name, parentId, weight, manufacturer, registration, registrationString, notes, notesString);

        this.type = TypeID.POTRAVINA;
        this.foodWeight = foodWeight;
        this.expiration = expiration;
        this.daysToExpiration = expiration == null ? Integer.MIN_VALUE : (int) LocalDate.now().until(expiration, ChronoUnit.DAYS);
        this.isExpired = expiration != null && daysToExpiration < 0;
        this.expirationString = expirationString;
        this.manufacturer = manufacturer == null || manufacturer.isEmpty() ? null : manufacturer;
    }
}
