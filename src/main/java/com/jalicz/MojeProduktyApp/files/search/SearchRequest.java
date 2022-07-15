package com.jalicz.MojeProduktyApp.files.search;

import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.files.FileManager;

import java.util.ArrayList;

public class SearchRequest {

    /*
    Request-command structure:
    Filters must be at the very end!
    <&> = FileManager.requestCommandArgumentSeparator
    MP:psw=abcd1234<&>text=brambory<&>search-in=1,2,5,6,9<&>order=1<&>limit=10<&>filters=#Filters#
    */

    public static final int // searchable item values
            SEARCH_NAME = 1,
            SEARCH_ID = 2,
            SEARCH_PARENT_ID = 3,
            SEARCH_WEIGHT = 4,
            SEARCH_MANUFACTURER = 5,
            SEARCH_REGISTRATION_DATE = 6,
            SEARCH_NOTES = 7,
            SEARCH_FOOD_WEIGHT = 8,
            SEARCH_EXPIRATION_DATE = 9;

    private static final String prefix = "MP:";

    public final boolean valid;
    public String password = null, text = "";
    public int[] searchItemValues = new int[]{ SEARCH_NAME, SEARCH_ID, SEARCH_MANUFACTURER, SEARCH_REGISTRATION_DATE, SEARCH_NOTES, SEARCH_EXPIRATION_DATE };
    public int orderId = 0;
    public int maxResults = -1;
    public Filters filters = new Filters();

    public SearchRequest(String requestCommand) {
        valid = requestCommand.startsWith(prefix);
        if(!valid) return;
        requestCommand = requestCommand.substring(prefix.length());

        final String[] args = requestCommand.split(FileManager.requestCommandArgumentSeparator);
        boolean filtersExists = false;
        int filtersBegin = 0;
        l:for(String arg: args) {
            final String key = arg.split("=")[0], value = arg.substring(key.length()+1);
            try {
                switch (key) {
                    case "text" -> text = value;
                    case "order" -> orderId = Integer.parseInt(value);
                    case "search-in" -> {
                        final String[] searchPartsStr = value.split(",");
                        final ArrayList<Integer> searchParts = new ArrayList<>();
                        for(String partStr: searchPartsStr) {
                            int part = Integer.parseInt(partStr);
                            if(part >= SEARCH_NAME && part <= SEARCH_EXPIRATION_DATE) searchParts.add(part);
                        }
                        searchItemValues = new int[searchParts.size()];
                        for(int i=0; i<searchItemValues.length; i++) searchItemValues[i] = searchParts.get(i);
                    }
                    case "filters" -> {
                        filtersBegin += key.length()+1;
                        filtersExists = true;
                        break l;
                    }
                    case "psw" -> password = value;
                }
            } catch (Exception e) {
                Log.error("Něco se pokazilo při zpracovávání dotazovacího příkazu '" + requestCommand + "', něco je špatně zapsané v " +
                        "argumentu: '" + arg + "'.", e);
            }
            filtersBegin += arg.length() + FileManager.requestCommandArgumentSeparator.length();
        }
        if(filtersExists) filters = new Filters(requestCommand.substring(filtersBegin));
    }
}