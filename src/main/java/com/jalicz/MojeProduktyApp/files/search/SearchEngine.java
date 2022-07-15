package com.jalicz.MojeProduktyApp.files.search;

import com.jalicz.MojeProduktyApp.GUI.frames.ServerSettingsFrame;
import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.files.FileManager;
import com.jalicz.MojeProduktyApp.model.Produkt;
import com.jalicz.MojeProduktyApp.model.SkladovyObjekt;
import com.jalicz.MojeProduktyApp.model.TypeID;
import com.jalicz.MojeProduktyApp.response.MPCode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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

        final SkladovyObjekt root = createItemTree(items);

        // FIX DODELAT
        return null;
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
