package com.jalicz.MojeProduktyApp.files.search;

import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.files.FileManager;
import com.jalicz.MojeProduktyApp.model.Potravina;
import com.jalicz.MojeProduktyApp.model.Produkt;
import com.jalicz.MojeProduktyApp.model.TypeID;

import java.util.ArrayList;
import java.util.Map;

public class Filters {

    /*
    Filters body structure:
    types -. TypeID.*
    <&> = FileManager.requestCommandArgumentSeparator
    bool = Y/N
    types=1,2,3<&>min-points=3<&>(filter-by-weight ? no-weight=bool<&>min-weight=0<&>max-weight=500)
                                 (filter-by-exp ? expired=bool<&>no-exp=bool<&>min-exp-days=0<&>max-exp-days=7<&>)
                                 (filter-by-reg ? min-reg-days=0<&>max-reg-days=21<&>)
    */

    public int[] itemTypes = new int[]{ TypeID.PRODUKT, TypeID.POTRAVINA, TypeID.SKLADOVY_OBJEKT };
    public int minRelevancyPoints = 3;

    public boolean filterByWeight = false, showItemsWithoutWeight = true;
    public int minWeight = 100, maxWeight = 1000;

    public boolean filterByExpirationDate = false, showExpiredItems = true, showItemsWithoutExpirationDate = true;
    public int minExpirationDays = 0, maxExpirationDays = 30;

    public boolean filterByRegistrationDate = false;
    public int minDaysFromRegistration = 0, maxDaysFromRegistration = 90;

    public Filters() {
        this("");
    }

    public Filters(String requestCommandFilters) {
        final String[] args = requestCommandFilters.split(FileManager.requestCommandArgumentSeparator);
        for(String arg: args) {
            try {
                final String key = arg.split("=")[0], value = arg.substring(key.length()+1);
                switch (key) {
                    case "types" -> {
                        final String[] typesStr = value.split(",");
                        ArrayList<Integer> types = new ArrayList<>();
                        for (String typeStr : typesStr) {
                            int type = Integer.parseInt(typeStr);
                            if (type == TypeID.PRODUKT || type == TypeID.POTRAVINA || type == TypeID.SKLADOVY_OBJEKT)
                                types.add(type);
                        }
                        itemTypes = new int[types.size()];
                        for (int i = 0; i < itemTypes.length; i++) itemTypes[i] = types.get(i);
                    }
                    case "min-points" -> minRelevancyPoints = Integer.parseInt(value);

                    case "no-weight" -> showItemsWithoutWeight = !value.equals("N");
                    case "min-weight" -> {
                        filterByWeight = true;
                        minWeight = Integer.parseInt(value);
                    }
                    case "max-weight" -> {
                        filterByWeight = true;
                        maxWeight = Integer.parseInt(value);
                    }

                    case "expired" -> showExpiredItems = !value.equals("N");
                    case "no-exp" -> showItemsWithoutExpirationDate = !value.equals("N");
                    case "min-exp-days" -> {
                        filterByExpirationDate = true;
                        minExpirationDays = Integer.parseInt(value);
                    }
                    case "max-exp-days" -> {
                        filterByExpirationDate = true;
                        maxExpirationDays = Integer.parseInt(value);
                    }

                    case "min-reg-days" -> {
                        filterByRegistrationDate = true;
                        minDaysFromRegistration = Integer.parseInt(value);
                    }
                    case "max-reg-days" -> {
                        filterByRegistrationDate = true;
                        maxDaysFromRegistration = Integer.parseInt(value);
                    }
                }
            } catch (Exception e) {
                Log.error("Něco se pokazilo při zpracovávání filterů '" + requestCommandFilters + "' z dotazovacího příkazu, něco je špatně zapsané.", e);
            }
        }
    }

    // checks if an Produkt meets all the filters in this class instance
    public boolean isMeetingAllFilters(Map.Entry<Produkt, Integer> entry) {
        final Produkt p = entry.getKey();

        if(entry.getValue() < minRelevancyPoints) return false;

        boolean hasType = false;
        for(int type: itemTypes) {
            if(type == p.type) {
                hasType = true;
                break;
            }
        }
        if(!hasType) return false;

        if(filterByWeight) {
            if(!showItemsWithoutWeight && p.weight == -1) return false;
            if(p.weight < minWeight || p.weight > maxWeight) return false;
        }

        if(filterByExpirationDate && p.id == TypeID.POTRAVINA) {
            final Potravina t = (Potravina) p;
            if(!showExpiredItems && t.isExpired) return false;
            if(!showItemsWithoutExpirationDate && t.expiration == null) return false;
            if(t.daysToExpiration != Integer.MIN_VALUE && (t.daysToExpiration < minExpirationDays || t.daysToExpiration > maxExpirationDays)) return false;
        }

        if(filterByRegistrationDate) {
            if(p.daysFromRegistration != Integer.MIN_VALUE && (p.daysFromRegistration < minDaysFromRegistration ||
                    p.daysFromRegistration > maxDaysFromRegistration)) return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return null;
    }
}
