package com.jalicz.MojeProduktyApp.files.search;

import com.jalicz.MojeProduktyApp.GUI.frames.ServerSettingsFrame;
import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.files.FileManager;
import com.jalicz.MojeProduktyApp.model.Produkt;
import com.jalicz.MojeProduktyApp.model.SkladovyObjekt;
import com.jalicz.MojeProduktyApp.model.TypeID;
import com.jalicz.MojeProduktyApp.response.MPCode;

import java.io.File;
import java.util.*;

class RatedProdukt implements Comparable<RatedProdukt> {

    public final int points;
    public final Produkt produkt;

    public RatedProdukt(int points, Produkt produkt) {
        this.points = points;
        this.produkt = produkt;
    }

    @Override
    public int compareTo(RatedProdukt o) {
        return Integer.compare(points, o.points);
    }
}

public class SearchEngine {

    // checks if a product with such ID and type exists in the database folder
    // if type == TypeID.INVALID -> checks only if ID exists
    public static boolean isItemExisting(int id, int type) {
        FileManager.createIfNotExists(FileManager.databaseFolder);
        final File[] files = FileManager.getItemFiles();
        final boolean ignoreType = type == TypeID.INVALID;
        for(File file: files) {
            try {
                final String fileName = file.getName(), name = fileName.replaceFirst("[.][^.]+$", ""), extension = fileName.substring(name.length()+1);
                final int fileType = FileManager.getTypeByExtension(extension);
                int fileId;
                try {
                    fileId = Integer.parseInt(name);
                    if(fileId == id && (ignoreType || fileType == type)) return true;
                } catch (Exception ignored) { }
            } catch (Exception e) {
                Log.warn("Název souboru " + file.getPath() + " je nějaký divný.");
            }
        }
        return false;
    }

    public static Produkt getProdukt(int id) {
        File file = null;
        for(File f: FileManager.getItemFiles()) if(f.getName().startsWith(id+"")) {
            file = f;
            break;
        }
        if(file == null) return null;
        return FileManager.readProdukt(file);
    }

    public static ArrayList<Produkt> getAllItems() {
        final File[] files = FileManager.getItemFiles();
        final ArrayList<Produkt> items = new ArrayList<>();
        for(File file: files) {
            final Produkt produkt = FileManager.readProdukt(file);
            if(produkt == null) continue;
            items.add(produkt);
        }
        return items;
    }

    public static SearchResult search(SearchRequest request) {
        if(!request.valid) {
            Log.warn("Někdo se zeptal serveru neplatným příkazem: " + request.cmd + ". Všechny příkazy musí začínat '" + SearchRequest.prefix + "'.");
            return new SearchResult(MPCode.INVALID_REQUEST_COMMAND, "Všechny dotazovací příkazy musí začínat '" + SearchRequest.prefix + "'.",
                    "K této chybě by za normálních okolností docházet nemělo. Tento příkaz by za Vás JavaScript běžící ve Vašem prohlížeči měl" +
                            " vyřešit za Vás.", null);
        }
        final String password = getPassword();
        if(password != null && !password.isEmpty()) {
            if(request.password == null || !request.password.equals(password)) { // if incorrect password
                Log.warn("Někdo se se pokusil zeptat Vaší databáze na dotaz, ale zadal nesprávné heslo '" + request.password + "'.");
                final String title = request.password == null || request.password.isEmpty() ? "Nezadal jste žádné heslo, prosím vyplňte toto pole." :
                        "Heslo '" + request.password + "' je nesprávné.";
                return new SearchResult(MPCode.INVALID_PASSWORD, title, "Požádejte o heslo správce databáze.<br>" +
                        "Pokud jste správcem Vy, v počítačové aplikaci ho lze změnit (Menu > Nastavení serveru > Požadovat heslo: ANO > Heslo)", null);
            }
        }

        final ArrayList<Produkt> items = getAllItems();

        final ArrayList<Map.Entry<Produkt, Integer>> resultMap = new ArrayList<>();
        final Comparator<Map.Entry<Produkt, Integer>> comparator = OrderResults.getById(request.orderId).comparator;

        final SkladovyObjekt root = createItemTree(items);
        deepSearch(root, resultMap, request);

        resultMap.sort(comparator);

        final ArrayList<Produkt> result = new ArrayList<>();
        final boolean limitResults = request.maxResults >= 0;
        int i = 0;
        for(Map.Entry<Produkt, Integer> entry: resultMap) {
            if(limitResults && ++i > request.maxResults) break;
            result.add(entry.getKey());
        }

        if(result.isEmpty()) return new SearchResult(MPCode.NO_ITEM_FOUND, "Žádné výsledky.",
                "Nebyly nalezeny žádné výsledky, které by splňovaly všechny Vaše požadavky.", null);

        return new SearchResult(MPCode.SUCCESS, "Bylo nalezeno " + result.size() + " výsledků.", "Bylo prohledáno " +
                root.totalItemsCount + " položek, z toho jich " + (root.totalItemsCount - result.size()) + " nesplňovalo filtry.", result);
    }

    private static int getPoints(Produkt produkt, String searchText, int[] searchParts) {
        return 3;
    }

    private static void deepSearch(SkladovyObjekt o, ArrayList<Map.Entry<Produkt, Integer>> resultMap, SearchRequest request) {
        for(Produkt p: o.items) {
            final int points = getPoints(p, request.text, request.searchItemValues);
            if(p.id > 0 && request.filters.isMeetingAllFilters(p, points)) resultMap.add(Map.entry(p, points));
            if(p.type == TypeID.SKLADOVY_OBJEKT) {
                SkladovyObjekt ob = (SkladovyObjekt) p;
                deepSearch(ob, resultMap, request);
            }
        }
    }

    private static <T extends Produkt> T getItemById(ArrayList<T> items, int id) {
        for(T item: items) if(item.id == id) return item;
        return null;
    }

    // returns the root skladovy-objekt
    public static SkladovyObjekt createItemTree(ArrayList<Produkt> items) {
        final SkladovyObjekt root = new SkladovyObjekt(0, null, -1, -1, null, null, null, null);
        final ArrayList<SkladovyObjekt> skObjs = new ArrayList<>();
        final ArrayList<Produkt> things = new ArrayList<>();
        for(Produkt p: items) {
            if(p.type == TypeID.PRODUKT || p.type == TypeID.POTRAVINA) things.add(p);
            else if(p.type == TypeID.SKLADOVY_OBJEKT) skObjs.add((SkladovyObjekt) p);
        }
        for(Produkt p: things) {
            if(p.parentId < 1) root.items.add(p);
            else {
                final SkladovyObjekt parent = getItemById(skObjs, p.parentId);
                if(parent != null) parent.items.add(p);
                else Log.warn("Předmět '" + p.name + "' s ID '" + p.id + "' má špatně zapsané ID nadřazeného skl. objektu ('" + p.parentId +
                        "'), žádný takový totiž neexistuje.");
            }
        }
        for(SkladovyObjekt s: skObjs) {
            if(s.parentId < 1) root.items.add(s);
            else {
                final SkladovyObjekt parent = getItemById(skObjs, s.parentId);
                if(parent != null) parent.items.add(s);
                else Log.warn("Skladový objekt '" + s.name + "' s ID '" + s.id + "' má špatně zapsané ID nadřazeného skl. objektu ('" + s.parentId +
                        "'), žádný takový totiž neexistuje.");
            }
        }
        root.updateContainedItemsData();
        return root;
    }

    private static String getPassword() {
        final HashMap<String, String> map = FileManager.getAppFileMap(FileManager.serverSettingsFilePath);
        if(map == null) return null;
        return map.getOrDefault(ServerSettingsFrame.password, null);
    }
}
