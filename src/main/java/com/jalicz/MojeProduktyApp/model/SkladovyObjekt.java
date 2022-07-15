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

    public ArrayList<Produkt> items = new ArrayList<>();
    public int totalItemsCount, produktyCount, potravinyCount, skladoveObjektyCount;
    public int totalSubItemsCount, subProduktyCount, subPotravinyCount, subSkladoveObjektyCount;
    public int totalItemsWeight, foodContentWeight;
    public int totalSubItemsWeight, subFoodContentWeight;

    public SkladovyObjekt(int id, String name, int parentId, int weight, LocalDateTime registration, String registrationString,
                          ArrayList<String> notes, String notesString) {
        super(id, name, parentId, weight, null, registration, registrationString, notes, notesString);

        this.type = TypeID.SKLADOVY_OBJEKT;
    }

    // updates all the contained items statistics, even subItems, subSubItems etc.
    public void updateContainedItemsData() {
        totalItemsCount = 0;            totalSubItemsCount = 0;
        produktyCount = 0;              subProduktyCount = 0;
        potravinyCount = 0;             subPotravinyCount = 0;
        skladoveObjektyCount = 0;       subSkladoveObjektyCount = 0;
        totalItemsWeight = 0;           totalSubItemsWeight = 0;
        foodContentWeight = 0;          subFoodContentWeight = 0;

        for(Produkt produkt: items) {
            totalItemsCount++;
            totalItemsWeight += produkt.weight;

            switch (produkt.type) {
                case TypeID.PRODUKT -> produktyCount++;
                case TypeID.POTRAVINA -> {
                    final Potravina potravina = (Potravina) produkt;
                    potravinyCount++;
                    foodContentWeight += potravina.foodWeight;
                }
                case TypeID.SKLADOVY_OBJEKT -> {
                    final SkladovyObjekt s = (SkladovyObjekt) produkt;
                    s.updateContainedItemsData();
                    skladoveObjektyCount++;

                    totalSubItemsCount += s.totalSubItemsCount;
                    subProduktyCount += s.subProduktyCount;
                    subPotravinyCount += s.subPotravinyCount;
                    subSkladoveObjektyCount += s.subSkladoveObjektyCount;
                    totalSubItemsWeight += s.totalSubItemsWeight;
                    subFoodContentWeight += s.subFoodContentWeight;
                }
            }
        }
        totalSubItemsCount += totalItemsCount;
        subProduktyCount += produktyCount;
        subPotravinyCount += potravinyCount;
        subSkladoveObjektyCount += skladoveObjektyCount;
        totalSubItemsWeight += totalItemsWeight;
        subFoodContentWeight += foodContentWeight;
    }
}
