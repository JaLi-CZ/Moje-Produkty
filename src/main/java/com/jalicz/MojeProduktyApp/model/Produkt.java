package com.jalicz.MojeProduktyApp.model;

import com.jalicz.MojeProduktyApp.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;

public class Produkt {

    /*
      Filename will look like: "[ID].produkt"
      Structure of file will look like:
        name
        parentId
        weight
        manufacturer
        registrationDate (dd-mm-yyyy/hh-nn-ss)
        notes
     */

    public int type = TypeID.PRODUKT;
    public final int id; // rooms have negative id's
    public final String name;
    public final int parentId;
    /*
        parentId > 0  when it's parent is an skladovy-objekt
        parentId < 0  when it's parent is a room
        parentId == 0 when it's a room and has no parent
    */
    public final int weight;
    /*
        -1 == non-specified
        this value represents number of grams
    */
    public final String manufacturer;
    public final LocalDateTime registration;
    public final int daysFromRegistration;
    public final String registrationString;
    public final ArrayList<String> notes;
    public final String notesString;

    private String pozice;

    public Produkt(int id, String name, int parentId, int weight, String manufacturer, LocalDateTime registration, String registrationString,
                   ArrayList<String> notes, String notesString) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.weight = weight;
        this.manufacturer = manufacturer;
        this.registration = registration;
        this.daysFromRegistration = registration == null ? Integer.MIN_VALUE : (int) ChronoUnit.DAYS.between(Time.get(), registration);
        this.registrationString = registrationString;
        this.notes = notes;
        this.notesString = notesString;
    }

    public String getPozice() {
        if(pozice != null) return pozice;
        ArrayList<String> path = new ArrayList<>();
        SkladovyObjekt parent = SkladovyObjekt.getParentById(parentId);
        while (true) {
            if(parent == null) break;
            path.add(parent.name + " #" + parent.id);
            int nextID = parent.parentId;
            if(nextID < 0) break;
            parent = SkladovyObjekt.getParentById(nextID);
        }
        Collections.reverse(path);
        StringBuilder builder = new StringBuilder();
        for(String place: path) builder.append(place).append(" > ");
        final String result = builder.isEmpty() ? null : builder.substring(0, builder.length()-3);
        return pozice = result;
    }
}
