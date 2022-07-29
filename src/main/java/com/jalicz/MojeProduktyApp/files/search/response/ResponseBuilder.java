package com.jalicz.MojeProduktyApp.files.search.response;

import com.jalicz.MojeProduktyApp.Time;
import com.jalicz.MojeProduktyApp.files.FileManager;
import com.jalicz.MojeProduktyApp.model.Potravina;
import com.jalicz.MojeProduktyApp.model.Produkt;
import com.jalicz.MojeProduktyApp.model.TypeID;

import java.io.File;

public class ResponseBuilder {

    private static String nazev, typ, id, vaha, pevnyPodil, pozice, expirace, vyrobce, registrace, poznamky, itemStructure, basicPage;

    public static void updateWebStructure() {
        basicPage = getBasicPage();
        itemStructure = getItemStructure();
        String content;
        if((content = read("radky/nazev.txt")) != null) nazev = content;
        else nazev = "           <h3 class=\"name\">--NAME--</h3>";
        if((content = read("radky/typ.txt")) != null) typ = content;
        else typ = "<p class=\"type --TYPE--\">--TYPE-CAPITAL--</p>";
        if((content = read("radky/id.txt")) != null) id = content;
        else id = "<p class=\"id\">Identifikátor (ID): <span class=\"value\">--ID--</span></p>";
        if((content = read("radky/vaha.txt")) != null) vaha = content;
        else vaha = "<p class=\"weight\">Váha: <span class=\"value\">--WEIGHT--g<span class=\"no-scale\"> (--WEIGHT-FOOD--g pevný podíl)</span></span></p>";
        if((content = read("radky/pevny-podil.txt")) != null) pevnyPodil = content;
        else pevnyPodil = "<span class=\"no-scale\"> (--FOOD-WEIGHT--g pevný podíl)</span>";
        if((content = read("radky/pozice.txt")) != null) pozice = content;
        else pozice = "<p class=\"pos\">Pozice: <span class=\"value no-scale\">--POS--</span></p>";
        if((content = read("radky/expirace.txt")) != null) expirace = content;
        else expirace = "<p class=\"expiration\">--EXPIRATION-TEXT-- (<span class=\"date value\">--EXPIRATION--</span>)</p>";
        if((content = read("radky/vyrobce.txt")) != null) vyrobce = content;
        else vyrobce = "<p class=\"manufacturer\">Výrobce: <span class=\"value\">--MANUFACTURER--</span></p>";
        if((content = read("radky/registrace.txt")) != null) registrace = content;
        else registrace = "<p class=\"registration\">Registrováno: <span class=\"date value\">--REGISTRATION--</span></p>";
        if((content = read("radky/poznamky.txt")) != null) poznamky = content;
        else poznamky = """
                    <p class="notes">Poznámky:<br><span class="value no-scale">
                        --NOTES--
                    </span></p>""";
    }

    private final SearchResult searchResult;
    private String html = null;

    public ResponseBuilder(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    public String build() {
        if(html != null) return html;
        String page = basicPage;
        String items = buildItems();
        if(items == null) items = "";
        page = page.replaceFirst("--ITEMS--", items);
        return (html = page);
    }

    private String buildItems() {
        if(searchResult.items == null) return null;
        StringBuilder builder = new StringBuilder();
        for(Produkt produkt: searchResult.items) builder.append(buildItem(produkt)).append('\n');
        return builder.toString();
    }

    private static String buildItem(Produkt produkt) {
        String type = null, typeText = null;
        switch (produkt.type) {
            case TypeID.PRODUKT -> {
                type = "produkt";
                typeText = "Produkt";
            }
            case TypeID.POTRAVINA -> {
                type = "potravina";
                typeText = "Potravina";
            }
            case TypeID.SKLADOVY_OBJEKT -> {
                type = "skladovy-objekt";
                typeText = "Skladový objekt";
            }
        }
        if(type == null) return null;

        String result = itemStructure, NAZEV, TYP, ID, VAHA = "", POZICE = "", EXPIRACE = "", VYROBCE = "", REGISTRACE = "", POZNAMKY = "";

        NAZEV = nazev.replaceFirst("--NAME--", produkt.name);
        TYP = typ.replaceFirst("--TYPE--", type).replaceFirst("--TYPE-TEXT--", typeText);
        ID = id.replaceFirst("--id--", produkt.id + "");
        if(produkt.weight >= 0) {
            VAHA = vaha.replaceFirst("--WEIGHT--", produkt.weight + "");
            String PEVNY_PODIL = "";
            if(produkt.type == TypeID.POTRAVINA) {
                Potravina p = (Potravina) produkt;
                if(p.foodWeight >= 0) PEVNY_PODIL = pevnyPodil.replaceFirst("--FOOD-WEIGHT--", p.foodWeight+"");
            }
            VAHA = VAHA.replaceFirst("--FOOD-WEIGHT-TEXT--", PEVNY_PODIL);
        }
        if(produkt.getPozice() != null) POZICE = pozice.replaceFirst("--POS--", produkt.getPozice());
        if(produkt.type == TypeID.POTRAVINA) {
            Potravina p = (Potravina) produkt;
            if(p.expiration != null) EXPIRACE = expirace.replaceFirst("--EXPIRATION--", Time.dateToWebFormat(p.expiration));
        }
        if(produkt.manufacturer != null) VYROBCE = vyrobce.replaceFirst("--MANUFACTURER--", produkt.manufacturer);
        if(produkt.registration != null) REGISTRACE = registrace.replaceFirst("--REGISTRATION--", Time.timeToWebFormat(produkt.registration));

        if(produkt.notes != null) {
            StringBuilder notesB = new StringBuilder();
            for(String note: produkt.notes) notesB.append("- ").append(note).append("<br>\n");
            POZNAMKY = poznamky.replaceFirst("--NOTES--", notesB.substring(0, notesB.length()-1));
        }

        result = result.replaceFirst("--nazev--", NAZEV).
                        replaceFirst("--typ--", TYP).
                        replaceFirst("--id--", ID).
                        replaceFirst("--vaha--", VAHA).
                        replaceFirst("--pozice--", POZICE).
                        replaceFirst("--expirace--", EXPIRACE).
                        replaceFirst("--vyrobce--", VYROBCE).
                        replaceFirst("--registrace--", REGISTRACE).
                        replaceFirst("--poznamky--", POZNAMKY);

        return result;
    }

    private static String getBasicPage() {
        final String content = read("zaklad-stranky.txt");
        if(content == null) return """
                <!-- © All right reserved by jalicz.com -->
                <!-- © Všechna práva vyhrazena společností jalicz.com -->
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Moje Produkty</title>
                    <link rel="stylesheet" href="style.css">
                </head>
                <body>
                    <div class="top-panel">
                        <div class="app-info">
                            <h1>Moje Produkty</h1>
                            <h2>verze 1.1.0</h2>
                        </div>
                    </div>

                    <div class="items">
                        --ITEMS--
                    </div>
                </body>
                </html>""";
        else return content;
    }

    private static String getItemStructure() {
        final String content = read("predmet.txt");
        if(content == null) return """
                <div class="item">
                    --nazev--
                    --typ--
                    --id--
                    --vaha--
                    --pozice--
                    --expirace--
                    --vyrobce--
                    --registrace--
                    --poznamky--
                </div>""".indent(8);
        else return content;
    }

    private static String read(String relPath) {
        return FileManager.read(new File(FileManager.webStructure.getPath() + "/" + relPath));
    }
}
