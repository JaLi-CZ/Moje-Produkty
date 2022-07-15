package com.jalicz.MojeProduktyApp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SkladovyObjekt extends Produkt {

    /*
      Filename will look like: "[ID].skladovy-objekt"
      Structure of file will look like:
        name
        parentId
        weight
        registrationDate (dd-mm-yyyy/hh-nn-ss)
        notes
     */

    public ArrayList<Produkt> items;
    public int totalItemsCount, produktyCount, potravinyCount, skladoveObjektyCount;
    public int totalItemsWeight, foodContentWeight;

    public SkladovyObjekt(int id, String name, int parentId, int weight, LocalDateTime registration, String registrationString,
                          ArrayList<String> notes, String notesString) {
        super(id, name, parentId, weight, null, registration, registrationString, notes, notesString);

        this.type = TypeID.SKLADOVY_OBJEKT;
    }

    public void setContainedItems(ArrayList<Produkt> items) {
        this.items = items;

        int totalItemsCount = 0, produktyCount = 0, potravinyCount = 0, skladoveObjektyCount = 0;
        int totalItemsWeight = 0, foodContentWeight = 0;

        for(Produkt produkt: items) {
            totalItemsCount++;
            totalItemsWeight += produkt.weight;

            switch (produkt.type) {
                case TypeID.PRODUKT -> produktyCount++;
                case TypeID.POTRAVINA -> {
                    Potravina potravina = (Potravina) produkt;
                    potravinyCount++;
                    foodContentWeight += potravina.foodWeight;
                }
                case TypeID.SKLADOVY_OBJEKT -> skladoveObjektyCount++;
            }
        }
        this.totalItemsCount = totalItemsCount;
        this.produktyCount = produktyCount;
        this.potravinyCount = potravinyCount;
        this.skladoveObjektyCount = skladoveObjektyCount;
        this.totalItemsWeight = totalItemsWeight;
        this.foodContentWeight = foodContentWeight;
    }
}
