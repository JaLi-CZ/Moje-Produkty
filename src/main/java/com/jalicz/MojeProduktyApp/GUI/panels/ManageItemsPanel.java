package com.jalicz.MojeProduktyApp.GUI.panels;

import com.jalicz.MojeProduktyApp.GUI.components.ComboBox;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.components.Text;
import com.jalicz.MojeProduktyApp.GUI.components.TextField;
import com.jalicz.MojeProduktyApp.GUI.components.Button;
import com.jalicz.MojeProduktyApp.GUI.frames.AddItemErrorFrame;
import com.jalicz.MojeProduktyApp.GUI.frames.ManageItemsSettingsFrame;
import com.jalicz.MojeProduktyApp.GUI.frames.ManageNotesFrame;
import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.Time;
import com.jalicz.MojeProduktyApp.files.FileManager;
import com.jalicz.MojeProduktyApp.files.search.SearchEngine;
import com.jalicz.MojeProduktyApp.model.Potravina;
import com.jalicz.MojeProduktyApp.model.Produkt;
import com.jalicz.MojeProduktyApp.model.SkladovyObjekt;
import com.jalicz.MojeProduktyApp.model.TypeID;

import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ManageItemsPanel extends Panel {

    public static HashMap<String, String> settingsMap = FileManager.getAppFileMap(FileManager.addItemSettingsFilePath);

    private static final Color
            requiredColor = Color.YELLOW,

            correctColor = new Color(129, 255, 43),
            warningColor = new Color(238, 255, 0),
            wrongColor = new Color(255, 72, 0);

    private static final String[]
            actions = new String[]{"Přidat předmět", "Spravovat předmět"},
            itemTypes = new String[]{"Produkt", "Potravina", "Skladový objekt"};

    private static final ArrayList<String> manufacturers = FileManager.getManufacturers();

    public ArrayList<String> currentNotes;

    public ManageItemsPanel() {
        super(new BorderLayout(8, 8));

        final ComboBox whatToDo, itemType;
        final TextField itemName, itemID;
        final TextField itemParentID;
        final TextField itemWeight, itemFoodPartWeight;
        final TextField itemManufacturer;
        final Button manageNotes;

        final Panel itemExpirationDatePanel;
        final TextField itemExpirationDay, itemExpirationMonth, itemExpirationYear;

        final Button addItemToDB, clearAll, settings;

        final Panel actionPanel; // Co chcete udělat? + (Typ předmětu / Zadejte ID předmětu)
        {
            actionPanel = new Panel(new GridLayout(1, 2));

            whatToDo = new ComboBox("Co chcete udělat?", actions);
            whatToDo.label.setForeground(Color.GREEN);

            itemType = new ComboBox("Typ předmětu:", itemTypes);
            itemType.label.setForeground(requiredColor);
            itemType.comboBox.setSelectedItem(itemTypes[1]); // set default selected item to Potravina

            actionPanel.add(whatToDo);
            actionPanel.add(itemType);
        }

        final Panel formPanel;
        {
            formPanel = new Panel(new GridLayout(6, 1));

            final Panel itemNameAndID;
            {
                itemNameAndID = new Panel(new GridLayout());

                itemName = new TextField("Název:", 280);
                itemID = new TextField("ID:", 85);

                itemName.label.setForeground(requiredColor);
                itemID.label.setForeground(requiredColor);

                itemNameAndID.add(itemName);
                itemNameAndID.add(itemID);
            }

            itemParentID = new TextField("ID nadřazeného skladového objektu:", 85);

            final Panel itemWeightAndNotesPanel;
            {
                itemWeightAndNotesPanel = new Panel(new GridLayout(1, 2));

                itemWeight = new TextField("Váha předmětu (g):", 95);

                final Panel notes;
                {
                    notes = new Panel(new GridLayout(1, 2));

                    final Text notesLabel = new Text("Poznámky:");
                    manageNotes = new Button("Spravovat");

                    notes.add(notesLabel);
                    notes.add(manageNotes);
                }

                itemWeightAndNotesPanel.add(itemWeight);
                itemWeightAndNotesPanel.add(notes);
            }

            final Panel itemManufacturerAndFoodPartWeightPanel;
            {
                itemManufacturerAndFoodPartWeightPanel = new Panel(new GridLayout(1, 2));

                itemManufacturer = new TextField("Výrobce:", 240);
                itemFoodPartWeight = new TextField("Váha potraviny (g):", 110);

                itemManufacturerAndFoodPartWeightPanel.add(itemManufacturer);
                itemManufacturerAndFoodPartWeightPanel.add(itemFoodPartWeight);
            }

            // itemExpirationDatePanel
            {
                itemExpirationDatePanel = new Panel(new GridLayout(1, 5));

                final Text expirationLabel = new Text("Datum expirace:");

                itemExpirationDay = new TextField("Den:", 75);
                itemExpirationMonth = new TextField("Měsíc:", 75);
                itemExpirationYear = new TextField("Rok:", 110);

                itemExpirationDatePanel.add(expirationLabel);
                itemExpirationDatePanel.add(itemExpirationDay);
                itemExpirationDatePanel.add(itemExpirationMonth);
                itemExpirationDatePanel.add(itemExpirationYear);
            }

            final Panel buttonsPanel;
            {
                buttonsPanel = new Panel(new GridLayout(1, 3, 8, 8));

                addItemToDB = new Button("Přidat do databáze");
                clearAll = new Button("Smazat vše");
                settings = new Button("Nastavení");

                addItemToDB.setForeground(correctColor);
                addItemToDB.setBorder(new LineBorder(correctColor, 1));

                clearAll.setForeground(wrongColor);
                clearAll.setBorder(new LineBorder(wrongColor, 1));

                settings.setForeground(Color.CYAN);
                settings.setBorder(new LineBorder(Color.CYAN, 1));

                buttonsPanel.add(settings);
                buttonsPanel.add(clearAll);
                buttonsPanel.add(addItemToDB);
            }

            formPanel.add(itemNameAndID);
            formPanel.add(itemParentID);
            formPanel.add(itemWeightAndNotesPanel);
            formPanel.add(itemManufacturerAndFoodPartWeightPanel);
            formPanel.add(itemExpirationDatePanel);
            formPanel.add(buttonsPanel);
        }

        add(actionPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        // Document Listeners - check if written value is correct
        {
            itemName.textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    final int len = itemName.textField.getText().length();
                    itemName.textField.setForeground(len > 3 && len < 30 ? correctColor : warningColor);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    final int len = itemName.textField.getText().length();
                    itemName.textField.setForeground(len > 3 && len < 30 ? correctColor : warningColor);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    final int len = itemName.textField.getText().length();
                    itemName.textField.setForeground(len > 3 && len < 30 ? correctColor : warningColor);
                }
            });


            final DocumentListener itemParentIDListener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    int id = -1, childID = -1;
                    try {
                        id = Integer.parseInt(itemParentID.textField.getText());
                        childID = Integer.parseInt(itemID.textField.getText());
                    } catch (Exception ignored) { }
                    itemParentID.textField.setForeground(id > 0 && (childID == -1 || childID != id) &&
                            SearchEngine.isItemExisting(id, TypeID.SKLADOVY_OBJEKT) ? correctColor : wrongColor);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    int id = -1, childID = -1;
                    try {
                        id = Integer.parseInt(itemParentID.textField.getText());
                        childID = Integer.parseInt(itemID.textField.getText());
                    } catch (Exception ignored) { }
                    itemParentID.textField.setForeground(id > 0 && (childID == -1 || childID != id) &&
                            SearchEngine.isItemExisting(id, TypeID.SKLADOVY_OBJEKT) ? correctColor : wrongColor);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    int id = -1, childID = -1;
                    try {
                        id = Integer.parseInt(itemParentID.textField.getText());
                        childID = Integer.parseInt(itemID.textField.getText());
                    } catch (Exception ignored) { }
                    itemParentID.textField.setForeground(id > 0 && (childID == -1 || childID != id) &&
                            SearchEngine.isItemExisting(id, TypeID.SKLADOVY_OBJEKT) ? correctColor : wrongColor);
                }
            };

            itemID.textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    int id = -1;
                    try { id = Integer.parseInt(itemID.textField.getText()); } catch (Exception ignored) { }
                    itemID.textField.setForeground(id > 0 && !SearchEngine.isItemExisting(id, TypeID.INVALID) ? correctColor : wrongColor);
                    itemParentIDListener.changedUpdate(null);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    int id = -1;
                    try { id = Integer.parseInt(itemID.textField.getText()); } catch (Exception ignored) { }
                    itemID.textField.setForeground(id > 0 && !SearchEngine.isItemExisting(id, TypeID.INVALID) ? correctColor : wrongColor);
                    itemParentIDListener.changedUpdate(null);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    int id = -1;
                    try { id = Integer.parseInt(itemID.textField.getText()); } catch (Exception ignored) { }
                    itemID.textField.setForeground(id > 0 && !SearchEngine.isItemExisting(id, TypeID.INVALID) ? correctColor : wrongColor);
                    itemParentIDListener.changedUpdate(null);
                }
            });

            itemParentID.textField.getDocument().addDocumentListener(itemParentIDListener);


            final DocumentListener foodPartWeightListener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    int foodGrams = -1, totalGrams = -1;
                    try {
                        foodGrams = Integer.parseInt(itemFoodPartWeight.textField.getText());
                        totalGrams = Integer.parseInt(itemWeight.textField.getText());
                    } catch (Exception ignored) { }
                    itemFoodPartWeight.textField.setForeground(foodGrams >= 0 && foodGrams <= totalGrams ? correctColor : wrongColor);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    int foodGrams = -1, totalGrams = -1;
                    try {
                        foodGrams = Integer.parseInt(itemFoodPartWeight.textField.getText());
                        totalGrams = Integer.parseInt(itemWeight.textField.getText());
                    } catch (Exception ignored) { }
                    itemFoodPartWeight.textField.setForeground(foodGrams >= 0 && foodGrams <= totalGrams ? correctColor : wrongColor);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    int foodGrams = -1, totalGrams = -1;
                    try {
                        foodGrams = Integer.parseInt(itemFoodPartWeight.textField.getText());
                        totalGrams = Integer.parseInt(itemWeight.textField.getText());
                    } catch (Exception ignored) { }
                    itemFoodPartWeight.textField.setForeground(foodGrams >= 0 && foodGrams <= totalGrams ? correctColor : wrongColor);
                }
            };

            itemWeight.textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    int grams = -1;
                    final String gramsStr = itemWeight.textField.getText();
                    try {
                        grams = Integer.parseInt(gramsStr);
                        itemFoodPartWeight.textField.setText(gramsStr);
                    } catch (Exception ignored) { }
                    itemWeight.textField.setForeground(grams >= 0 ? correctColor : wrongColor);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    int grams = -1;
                    final String gramsStr = itemWeight.textField.getText();
                    try {
                        grams = Integer.parseInt(gramsStr);
                        itemFoodPartWeight.textField.setText(gramsStr);
                    } catch (Exception ignored) { }
                    itemWeight.textField.setForeground(grams >= 0 ? correctColor : wrongColor);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    int grams = -1;
                    final String gramsStr = itemWeight.textField.getText();
                    try {
                        grams = Integer.parseInt(gramsStr);
                        itemFoodPartWeight.textField.setText(gramsStr);
                    } catch (Exception ignored) { }
                    itemWeight.textField.setForeground(grams >= 0 ? correctColor : wrongColor);
                }
            });

            itemFoodPartWeight.textField.getDocument().addDocumentListener(foodPartWeightListener);

            itemManufacturer.textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    String manufacturer = itemManufacturer.textField.getText().toLowerCase();
                    if(manufacturer.isEmpty()) return;
                    manufacturer = firstCapital(manufacturer);
                    itemManufacturer.textField.setForeground(manufacturers.contains(manufacturer) ? correctColor : warningColor);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    String manufacturer = itemManufacturer.textField.getText().toLowerCase();
                    if(manufacturer.isEmpty()) return;
                    manufacturer = firstCapital(manufacturer);
                    itemManufacturer.textField.setForeground(manufacturers.contains(manufacturer) ? correctColor : warningColor);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    String manufacturer = itemManufacturer.textField.getText().toLowerCase();
                    if(manufacturer.isEmpty()) return;
                    manufacturer = firstCapital(manufacturer);
                    itemManufacturer.textField.setForeground(manufacturers.contains(manufacturer) ? correctColor : warningColor);
                }
            });


            final DocumentListener expirationDayListener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    int day = -1, month = -1, year = -1;
                    try {
                        day = Integer.parseInt(itemExpirationDay.textField.getText());
                        month = Integer.parseInt(itemExpirationMonth.textField.getText());
                        year = Integer.parseInt(itemExpirationYear.textField.getText());
                    } catch (Exception ignored) {}
                    itemExpirationDay.textField.setForeground(Time.isValidDate(day, month, year, true) ? correctColor : wrongColor);

                    if(settingsMap.getOrDefault(ManageItemsSettingsFrame.autoExpirationFieldSwitch, ManageItemsSettingsFrame.yes)
                            .equals(ManageItemsSettingsFrame.yes) && itemExpirationDay.textField.getForeground().equals(correctColor) &&
                            itemExpirationDay.textField.getText().length() == 2) itemExpirationMonth.textField.requestFocusInWindow();
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    int day = -1, month = -1, year = -1;
                    try {
                        day = Integer.parseInt(itemExpirationDay.textField.getText());
                        month = Integer.parseInt(itemExpirationMonth.textField.getText());
                        year = Integer.parseInt(itemExpirationYear.textField.getText());
                    } catch (Exception ignored) {}
                    itemExpirationDay.textField.setForeground(Time.isValidDate(day, month, year, true) ? correctColor : wrongColor);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    int day = -1, month = -1, year = -1;
                    try {
                        day = Integer.parseInt(itemExpirationDay.textField.getText());
                        month = Integer.parseInt(itemExpirationMonth.textField.getText());
                        year = Integer.parseInt(itemExpirationYear.textField.getText());
                    } catch (Exception ignored) {}
                    itemExpirationDay.textField.setForeground(Time.isValidDate(day, month, year, true) ? correctColor : wrongColor);
                }
            };

            itemExpirationDay.textField.getDocument().addDocumentListener(expirationDayListener);

            itemExpirationMonth.textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    int month = -1;
                    try { month = Integer.parseInt(itemExpirationMonth.textField.getText()); } catch (Exception ignored) { }
                    itemExpirationMonth.textField.setForeground(month >= 1 && month <= 12 ? correctColor : wrongColor);
                    expirationDayListener.changedUpdate(null);

                    if(settingsMap.getOrDefault(ManageItemsSettingsFrame.autoExpirationFieldSwitch, ManageItemsSettingsFrame.yes)
                            .equals(ManageItemsSettingsFrame.yes) && itemExpirationMonth.textField.getForeground().equals(correctColor) &&
                            itemExpirationMonth.textField.getText().length() == 2) itemExpirationYear.textField.requestFocusInWindow();
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    int month = -1;
                    try { month = Integer.parseInt(itemExpirationMonth.textField.getText()); } catch (Exception ignored) { }
                    itemExpirationMonth.textField.setForeground(month >= 1 && month <= 12 ? correctColor : wrongColor);
                    expirationDayListener.changedUpdate(null);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    int month = -1;
                    try { month = Integer.parseInt(itemExpirationMonth.textField.getText()); } catch (Exception ignored) { }
                    itemExpirationMonth.textField.setForeground(month >= 1 && month <= 12 ? correctColor : wrongColor);
                    expirationDayListener.changedUpdate(null);
                }
            });

            itemExpirationYear.textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    int year = -1;
                    try { year = Integer.parseInt(itemExpirationYear.textField.getText()); } catch (Exception ignored) { }
                    itemExpirationYear.textField.setForeground(year < 0 || year > 1000000 ? wrongColor : (year >= 1990 && year <= 2150 ? correctColor : warningColor));
                    expirationDayListener.changedUpdate(null);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    int year = -1;
                    try { year = Integer.parseInt(itemExpirationYear.textField.getText()); } catch (Exception ignored) { }
                    itemExpirationYear.textField.setForeground(year < 0 || year > 1000000 ? wrongColor : (year >= 1990 && year <= 2150 ? correctColor : warningColor));
                    expirationDayListener.changedUpdate(null);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    int year = -1;
                    try { year = Integer.parseInt(itemExpirationYear.textField.getText()); } catch (Exception ignored) { }
                    itemExpirationYear.textField.setForeground(year < 0 || year > 1000000 ? wrongColor : (year >= 1990 && year <= 2150 ? correctColor : warningColor));
                    expirationDayListener.changedUpdate(null);
                }
            });
        }

        // Function and logic Listeners
        {
            itemType.comboBox.addItemListener(e -> {
                String item = (String) e.getItem();
                if(item.equals(itemTypes[0])) { // Produkt
                    itemFoodPartWeight.setVisible(false);
                    itemExpirationDatePanel.setVisible(false);
                    itemManufacturer.setVisible(true);
                } else if(item.equals(itemTypes[1])) { // Potravina
                    itemFoodPartWeight.setVisible(true);
                    itemExpirationDatePanel.setVisible(true);
                    itemManufacturer.setVisible(true);
                } else if(item.equals(itemTypes[2])) { // Skladový objekt
                    itemFoodPartWeight.setVisible(false);
                    itemExpirationDatePanel.setVisible(false);
                    itemManufacturer.setVisible(false);
                } else { // shouldn't be called
                    Log.warn("Ve vybíracím boxu s titulkem '" + itemType.label.getText() + "', je vybraná nesprávná hodnota '" + item + "'.");
                }
            });

            manageNotes.addActionListener(e -> new ManageNotesFrame(this));

            addItemToDB.addActionListener(e -> {
                ArrayList<String> errors = new ArrayList<>();

                final int type = getItemTypeBySelectedString((String) itemType.comboBox.getSelectedItem());
                if(type == TypeID.INVALID) errors.add("Zvolený typ předmětu je nějaký divný, nemohu ho rozpoznat. " +
                        "Zkuste ho změnit na nějaký jiný a potom zase zpátky, pokud to nepomůže, kontaktujte nás.");

                final String name = itemName.textField.getText();
                if(name == null || name.isEmpty()) errors.add("Název předmětu je povinný, prosím vyplňte toto pole.");

                final String idStr = itemID.textField.getText();
                Integer id = null;
                if(idStr.isEmpty()) errors.add("Nezadali jste ID (identifikační číslo) předmětu. Toto pole je povinné, prosím vyplňte ho.");
                else {
                    try { id = Integer.parseInt(idStr); } catch (Exception ignored) { }
                    if(id == null) errors.add("ID (identifikační číslo) předmětu je špatně zapsané, může obsahovat pouze číslice (0 až 9).");
                    else if(id < 1) errors.add("ID (identifikační číslo) předmětu je menší než 1, povolený rozsah ID je 1 až 2147483647.");
                    else if(SearchEngine.isItemExisting(id, TypeID.INVALID))
                        errors.add("Takové ID (identifikační číslo) předmětu již existuje, všechny ID musí být unikátní.");
                }

                final String parentIdStr = itemParentID.textField.getText();
                Integer parentId = null;
                if(!parentIdStr.isEmpty()) {
                    try { parentId = Integer.parseInt(parentIdStr); } catch (Exception ignored) { }
                    if(parentId == null) errors.add("ID nadřazeného skladového objektu je špatně zapsané, může obsahovat pouze číslice (0 až 9). " +
                            "Jestliže má být předmět umístěn na nejvyšším stupni předmětového stromu, toto pole není třeba vyplňovat.");
                    else if(parentId < 1) errors.add("ID nadřazeného skladového objektu je menší než 1, povolený rozsah všech ID je 1 až 2147483647. " +
                            "Jestliže má být předmět umístěn na nejvyšším stupni předmětového stromu, toto pole není třeba vyplňovat.");
                    else if(parentId.equals(id)) errors.add("ID nadřazeného skladového objektu je stejné jako ID předmětu. " +
                            "Předmět v sobě logicky nemůže skladovat sám sebe, tyto dvě ID musí být odlišné. " +
                            "Jestliže má být předmět umístěn na nejvyšším stupni předmětového stromu, toto pole není třeba vyplňovat.");
                    else if(!SearchEngine.isItemExisting(parentId, TypeID.SKLADOVY_OBJEKT)) errors.add("ID nadřazeného skladového objektu odkazuje na předmět, " +
                            "který buď neexistuje, nebo je jiného typu než skladového objektu. ID nadřazeného skl. objektu musí odkazovat na předmět s typem " +
                            "skladového objektu, který existuje. " +
                            "Jestliže má být předmět umístěn na nejvyšším stupni předmětového stromu, toto pole není třeba vyplňovat.");
                }

                final String weightStr = itemWeight.textField.getText();
                Integer weight = null;
                if(!weightStr.isEmpty()) {
                    try { weight = Integer.parseInt(weightStr); } catch (Exception ignored) { }
                    if(weight == null) errors.add("Váha předmětu (v gramech) je špatně zapsaná, může obsahovat pouze číslice (0 až 9). " +
                            "Pokud nechcete uvádět váhu předmětu, toto pole není třeba vyplňovat.");
                    else if(weight < 0) errors.add("Váha předmětu (v gramech) je zapsána negativní hodnotou. Pokud předmět nelevituje, změňte prosím tuto " +
                            "hodnotu na pozitivní číslo. Pokud nechcete uvádět váhu předmětu, toto pole není třeba vyplňovat.");
                }

                Integer foodPartWeight = null;
                if(type == TypeID.POTRAVINA) {
                    String foodPartWeightStr = itemFoodPartWeight.textField.getText();

                    if(!foodPartWeightStr.isEmpty()) {
                        try { foodPartWeight = Integer.parseInt(foodPartWeightStr); } catch (Exception ignored) { }
                        if(foodPartWeight == null) errors.add("Váha potraviny (v gramech) je špatně zapsaná, může obsahovat pouze číslice (0 až 9). " +
                                "Pokud nechcete uvádět váhu potraviny, toto pole není třeba vyplňovat. Vyplní se samo podle standartní váhy předmětu.");
                        else if(foodPartWeight < 0) errors.add("Váha potraviny (v gramech) je zapsána negativní hodnotou. Pokud hmotnost potraviny nelevituje, " +
                                "změňte prosím tuto hodnotu na číslo pozitivní. " +
                                "Pokud nechcete uvádět váhu potraviny, toto pole není třeba vyplňovat. Vyplní se samo podle standartní váhy předmětu.");
                        else if(weight != null && foodPartWeight > weight) errors.add("Váha potraviny (v gramech) je větší než celková váha předmětu. " +
                                "To je samozřejmě nesmysl, změňte prosím váhu potraviny na číslo stejné či menší než je celková váha předmětu. " +
                                "Pokud nechcete uvádět váhu potraviny, toto pole není třeba vyplňovat. Vyplní se samo podle úplné váhy předmětu.");
                    }
                    foodPartWeight = weight;
                }

                String manufacturer = null;
                if(type != TypeID.SKLADOVY_OBJEKT) {
                    manufacturer = itemManufacturer.textField.getText();
                    if(manufacturer.isEmpty()) manufacturer = null;
                    else manufacturer = firstCapital(manufacturer);
                }

                Integer expirationDay = null, expirationMonth = null, expirationYear = null;
                if(type == TypeID.POTRAVINA) {
                    final String expirationDayStr = itemExpirationDay.textField.getText(), expirationMonthStr = itemExpirationMonth.textField.getText(),
                            expirationYearStr = itemExpirationYear.textField.getText();
                    if(!expirationYearStr.isEmpty()) {
                        try { expirationYear = Integer.parseInt(expirationYearStr); } catch (Exception ignored) { }
                        if(expirationYear != null) {
                            try { expirationMonth = Integer.parseInt(expirationMonthStr); } catch (Exception ignored) { }
                            if(expirationMonth == null) expirationMonth = 12;

                            try { expirationDay = Integer.parseInt(expirationDayStr); } catch (Exception ignored) { }
                            if(expirationDay == null) {
                                    for(int d=31; d>=28; d--) {
                                        if(Time.isValidDate(d, expirationMonth, expirationYear, false)) {
                                            expirationDay = d;
                                            break;
                                        }
                                    }
                                    if(expirationDay == null) expirationDay = 28; // shouldn't happen
                                }
                        }
                    }
                }
                if(expirationYear != null) {
                    if(expirationYear < 0 || expirationYear > 1000000)
                        errors.add("Rok expirace může být v rozsahu (0 až 1000000), hodnota '" + expirationYear + "' tedy není povolena.");
                    else if(!Time.isValidDate(expirationDay, expirationMonth, expirationYear, false)) errors.add("Takové datum expirace neexistuje.");
                }

                if(errors.isEmpty()) {

                    if(parentId == null) parentId = -1;
                    if(weight == null) weight = -1;
                    if(foodPartWeight == null) foodPartWeight = -1;

                    if(currentNotes != null && currentNotes.isEmpty()) currentNotes = null;

                    final String notesString;
                    if(currentNotes != null) {
                        StringBuilder builder = new StringBuilder();
                        for(String note: currentNotes) builder.append(note).append(FileManager.noteSeparator);
                        notesString = builder.substring(0, builder.length()-FileManager.noteSeparator.length());
                    } else notesString = null;

                    final String typeBent;
                    Produkt produkt;
                    switch (type) {
                        case TypeID.PRODUKT:
                            typeBent = "Produkt";
                            produkt = new Produkt(id, name, parentId, weight, manufacturer, Time.get(), Time.getProductFileFormat(), currentNotes, notesString);
                            break;

                        case TypeID.POTRAVINA:
                            typeBent = "Potravina";
                            LocalDate expiration;
                            if(expirationYear == null) expiration = null;
                            else expiration = LocalDate.of(expirationYear, expirationMonth, expirationDay);
                            final String expirationString = expiration == null ? null : expirationDay + "-" + expirationMonth + "-" + expirationYear;
                            produkt = new Potravina(id, name, parentId, weight, manufacturer, Time.get(),
                                Time.getProductFileFormat(), currentNotes, notesString, foodPartWeight, expiration, expirationString);
                            break;

                        case TypeID.SKLADOVY_OBJEKT:
                            typeBent = "Skladový objekt";
                            produkt = new SkladovyObjekt(id, name, parentId, weight, Time.get(), Time.getProductFileFormat(), currentNotes, notesString);
                            break;

                        default: return;
                    }

                    FileManager.writeProdukt(produkt);
                    Log.info(typeBent + " '" + produkt.name + "' s ID #" + produkt.id + " byl" + (type == TypeID.POTRAVINA ? "a":"") + " přidán do databáze :).");

                    if(manufacturer != null && !manufacturers.contains(manufacturer)) {
                        manufacturers.add(manufacturer);
                        FileManager.setManufacturers(manufacturers);
                        Log.info("Nový výrobce předmětů '" + manufacturer + "' byl zapsán do souboru '" + FileManager.manufacturersFilePath + "'.");
                    }

                    final boolean
                            incrementID = settingsMap.getOrDefault(ManageItemsSettingsFrame.incrementIDAfterAddition, ManageItemsSettingsFrame.yes)
                                .equals(ManageItemsSettingsFrame.yes),
                            deleteAll = settingsMap.getOrDefault(ManageItemsSettingsFrame.deleteAllAfterAddition, ManageItemsSettingsFrame.no)
                                    .equals(ManageItemsSettingsFrame.yes);

                    if(deleteAll) clearAll.doClick();
                    else if(incrementID) itemID.increment();

                    if(settingsMap.getOrDefault(ManageItemsSettingsFrame.clearNotesAfterAddition, ManageItemsSettingsFrame.yes)
                            .equals(ManageItemsSettingsFrame.yes)) currentNotes = null;
                } else {
                    new AddItemErrorFrame(errors);
                }
            });

            clearAll.addActionListener(e -> {
                itemType.comboBox.setSelectedItem(itemTypes[1]);
                itemName.textField.setText("");
                itemID.textField.setText("");
                itemParentID.textField.setText("");
                itemWeight.textField.setText("");
                itemFoodPartWeight.textField.setText("");
                itemManufacturer.textField.setText("");
                if(currentNotes != null) currentNotes.clear();
                itemExpirationDay.textField.setText("");
                itemExpirationMonth.textField.setText("");
                itemExpirationYear.textField.setText("");
            });

            settings.addActionListener(e -> new ManageItemsSettingsFrame());
        }
    }

    private static int getItemTypeBySelectedString(String selectedItem) {
        if(selectedItem == null) return TypeID.INVALID;
        if(selectedItem.equals(itemTypes[0])) return TypeID.PRODUKT;
        else if(selectedItem.equals(itemTypes[1])) return TypeID.POTRAVINA;
        else if(selectedItem.equals(itemTypes[2])) return TypeID.SKLADOVY_OBJEKT;
        else return TypeID.INVALID;
    }

    private static String firstCapital(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
