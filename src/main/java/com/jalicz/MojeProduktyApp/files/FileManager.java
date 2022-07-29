package com.jalicz.MojeProduktyApp.files;

import com.jalicz.MojeProduktyApp.Time;
import com.jalicz.MojeProduktyApp.model.Potravina;
import com.jalicz.MojeProduktyApp.model.Produkt;
import com.jalicz.MojeProduktyApp.model.SkladovyObjekt;
import com.jalicz.MojeProduktyApp.model.TypeID;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    public static final String
            serverSettingsFilePath = "nastaveni-serveru.txt",
            addItemSettingsFilePath = "nastaveni-pridavani-predmetu.txt",
            manufacturersFilePath = "vyrobci.txt";

    public static final String
            produktExtension = "produkt",
            potravinaExtension = "potravina",
            skladovyObjektExtension = "skladovy-objekt";

    public static final File
            logsFolder = new File("logs"), logFile = getLogFile(),
            databaseFolder = new File("databaze"),
            webFolder = new File("web"),
            appDataFolder = new File("data-aplikace"),
            webStructure = new File(appDataFolder.getPath() + "/struktura-webu"),
            imagesFolder = new File(appDataFolder.getPath() + "/obrazky");

    public static final String
            noteSeparator = "</&!>",
            keyValueLineSeparator = ": ",
            requestCommandArgumentSeparator = "<&>",
            unspecified = "N";

    private static final PrintWriter logger = getLogger();

    // creates file/folder if it already doesn't exist
    public static void createIfNotExists(File file) {
        if(file.exists()) return;
        final boolean isFile = file.getName().contains(".");
        try {
            if(isFile) {
                if(!file.createNewFile()) Log.error("Nepodařilo se vytvořit soubor \"" + file.getAbsolutePath() + "\".");
            } else {
                if(!file.mkdirs()) Log.error("Nepodařilo se vytvořit složku \"" + file.getAbsolutePath() + "\".");
            }
        } catch (Exception e) {
            Log.error("Něco se pokazilo při vytváření souboru \"" + file.getAbsolutePath() + "\".", e);
        }
    }

    // reads an entire file
    public static String read(File file) {
        try {
            final FileReader r = new FileReader(file);
            final StringBuilder b = new StringBuilder();
            int c;
            while ((c = r.read()) != -1) b.append((char) c);
            return b.toString();
        } catch (Exception e) {
            Log.error("Něco se pokazilo při čtení souboru \"" + file.getAbsolutePath() + "\".", e);
            return null;
        }
    }

    // reads all the lines from a file and returns them as a ArrayList of Strings
    public static ArrayList<String> readLines(File file) {
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            final ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) lines.add(line);
            return lines;
        } catch (Exception e) {
            Log.error("Něco se pokazilo při čtení všech řádků souboru \"" + file.getAbsolutePath() + "\".", e);
            return null;
        }
    }

    public static void writeLines(File file, ArrayList<String> lines) {
        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(file));
            for(String line: lines) writer.println(line);
            writer.close();
        } catch (Exception e) {
            Log.error("Něco se pokazilo při psaní " + lines.size() + " řádků do souboru \"" + file.getAbsolutePath() + "\".", e);
        }
    }

    // writes to a file
    public static void write(File file, String s) {
        try {
            createIfNotExists(file);
            final FileWriter writer = new FileWriter(file);
            writer.write(s);
            writer.close();
        } catch (Exception e) {
            final String text = (s.length() > 20 ? s.substring(0, 20) + "..." : s).replaceAll("\n", "<br>");
            Log.error("Něco se pokazilo při psaní textu (\"" + text + "\") do souboru \"" + file.getAbsolutePath() + "\".", e);
        }
    }

    // returns a log file
    private static File getLogFile() {
        createIfNotExists(logsFolder);
        int i = 1;
        File logFile;
        final String pathBeginning = logsFolder.getPath() + "/log-";
        while ((logFile = new File(pathBeginning + i + ".txt")).exists()) i++;
        createIfNotExists(logFile);
        return logFile;
    }

    // returns a PrintWriter which writes to the log file
    private static PrintWriter getLogger() {
        try {
            PrintWriter logger = new PrintWriter(new FileWriter(logFile));
            logger.println("Tento výpisový soubor byl vytvořen " + Time.getLogFileFormat());
            logger.flush();
            return logger;
        } catch (Exception e) {
            Log.error("Nelze vytvořit souborový vypisovač (PrintWriter)!", e);
            return null;
        }
    }

    // logs a new line to the log file
    public static void log(String s) {
        if(logger == null) return;
        logger.println(s);
        logger.flush();
    }

    // returns an app-data file map
    public static HashMap<String, String> getAppFileMap(String fileName) {
        File file = null;
        try {
            createIfNotExists(appDataFolder);
            file = new File(appDataFolder.getPath() + "/" + fileName);
            createIfNotExists(file);

            final HashMap<String, String> map = new HashMap<>();
            final ArrayList<String> lines = readLines(file);
            if(lines == null) return map;

            for(String line: lines) {
                String key = line.split(keyValueLineSeparator)[0], value = line.substring(key.length());
                if(value.isEmpty()) value = null;
                else if(value.startsWith(keyValueLineSeparator)) value = value.substring(keyValueLineSeparator.length());
                map.put(key, value);
            }
            return map;

        } catch (Exception e) {
            Log.error("Chyba při mapování souboru '" + (file == null ? fileName : file.getAbsolutePath()) + "'!", e);
            return null;
        }
    }

    // writes to a app-data file information from a map
    // if a map contains null value, it will be set to the last value
    public static void saveAppFileMap(String fileName, HashMap<String, String> map) {
        File file = null;
        try {
            createIfNotExists(appDataFolder);
            file = new File(appDataFolder.getPath() + "/" + fileName);
            createIfNotExists(file);

            final HashMap<String, String> existingMap = getAppFileMap(fileName);
            if(existingMap != null)
                for(Map.Entry<String, String> entry: existingMap.entrySet()) {
                    try {
                        if(map.containsKey(entry.getKey()) && map.get(entry.getKey()) == null) map.put(entry.getKey(), entry.getValue());
                    } catch (Exception ignored) { }
            }

            boolean empty = true;
            final StringBuilder builder = new StringBuilder();
            for(Map.Entry<String, String> entry: map.entrySet()) {
                empty = false;
                builder.append(entry.getKey()).append(keyValueLineSeparator).append(entry.getValue()).append("\n");
            }

            final String text = empty ? "" : builder.substring(0, builder.length()-1);
            write(file, text);

        } catch (Exception e) {
            Log.error("Chyba při ukládání informací z mapy do souboru '" + (file == null ? fileName : file.getAbsolutePath()) + "'!", e);
        }
    }

    public static BufferedImage getImage(String filePath) {
        try {
            createIfNotExists(imagesFolder);
            final File imageFile = new File(imagesFolder.getPath() + "/" + filePath);
            return ImageIO.read(imageFile);
        } catch (Exception e) {
            Log.error("Něco se pokazilo při čtení obrázku '" + filePath + "'!", e);
            return null;
        }
    }

    public static String getExtensionByType(int type) {
        return switch (type) {
            case TypeID.PRODUKT -> produktExtension;
            case TypeID.POTRAVINA -> potravinaExtension;
            case TypeID.SKLADOVY_OBJEKT -> skladovyObjektExtension;
            default -> null;
        };
    }

    public static int getTypeByExtension(String extension) {
        return switch (extension) {
            case produktExtension -> TypeID.PRODUKT;
            case potravinaExtension -> TypeID.POTRAVINA;
            case skladovyObjektExtension -> TypeID.SKLADOVY_OBJEKT;
            default -> TypeID.INVALID;
        };
    }

    // returns a Produkt instance read from a file
    public static Produkt readProdukt(File file) {
        String[] filenameParts = file.getName().split("\\.");
        if(filenameParts.length != 2) {
            Log.warn("Zvláštní název souboru: \"" + file.getAbsolutePath() + "\"" +
                    "\n  Měl by se nazývat takhle: [ID].{" + produktExtension + "/" + potravinaExtension + "/" + skladovyObjektExtension + "}, " +
                    "takže například \"174.potravina\"" +
                    "\n  K této chybě by za normálních okolností docházet nemělo, pravděpodobně tento soubor někdo upravil nežádoucím způsobem." +
                    "\n  Prozatím budu tento problém ignorovat a s tímto souborem nebudu pracovat.");
            return null;
        }

        final String frontName = filenameParts[0], extension = filenameParts[1];

        int type = getTypeByExtension(extension);
        if(type == TypeID.INVALID) {
            Log.warn("Zvláštní koncovka souboru (\"" + extension + "\"): \"" + file.getAbsolutePath() + "\"" +
                    "\n  Koncovka by měla vypadat jako jedna z těchto: \"" + produktExtension + "\", \"" + potravinaExtension + "\", \"" +
                    skladovyObjektExtension + "\"." +
                    "\n  K této chybě by za normálních okolností docházet nemělo, pravděpodobně tento soubor někdo upravil nežádoucím způsobem." +
                    "\n  Prozatím budu tento problém ignorovat a s tímto souborem nebudu pracovat.");
            return null;
        }
        int id;
        try {
            id = toInt(frontName);
        } catch (Exception e) {
            Log.warn("Špatné ID (Identifikační číslo) u souboru \"" + file.getAbsolutePath() + "\" (ID = \"" + frontName + "\")" +
                    "\n  ID musí být přirozené číslo v rozsahu (1 až 2147483647)" +
                    "\n  K této chybě by za normálních okolností docházet nemělo, pravděpodobně tento soubor někdo upravil nežádoucím způsobem." +
                    "\n  Prozatím budu tento problém ignorovat a s tímto souborem nebudu pracovat.");
            return null;
        }

        final String typeBent = switch (type) {
            case TypeID.PRODUKT -> "produktu";
            case TypeID.POTRAVINA -> "potravině";
            case TypeID.SKLADOVY_OBJEKT -> "skladovém objektu";
            default -> "--NEZNÁMÝ TYP PRODUKTU--"; // shouldn't be possible
        };

        final ArrayList<String> lines = readLines(file);
        if(lines == null || lines.isEmpty()) {
            Log.warn("Soubor \"" + file.getAbsolutePath() + "\" neobsahuje žádné informace o " + typeBent +
                    "\n  K této chybě by za normálních okolností docházet nemělo, pravděpodobně tento soubor někdo upravil nežádoucím způsobem." +
                    "\n  Prozatím budu tento problém ignorovat a s tímto souborem nebudu pracovat.");
            return null;
        }

        final int size = lines.size();

        String name =                                     lines.get(0),
               parentIdString =         size < 2 ? null : lines.get(1),
               weightString =           size < 3 ? null : lines.get(2),
               manufacturer = null, registrationDateString = null, notesString = null, foodWeightString = null, expirationDateString = null;

        switch (type) {
            case TypeID.PRODUKT -> {
                manufacturer = size < 4 ? null : lines.get(3);
                registrationDateString = size < 5 ? null : lines.get(4);
                notesString = size < 6 ? null : lines.get(5);
            }
            case TypeID.POTRAVINA -> {
                manufacturer = size < 4 ? null : lines.get(3);
                registrationDateString = size < 5 ? null : lines.get(4);
                notesString = size < 6 ? null : lines.get(5);
                foodWeightString = size < 7 ? null : lines.get(6);
                expirationDateString = size < 8 ? null : lines.get(7);
            }
            case TypeID.SKLADOVY_OBJEKT -> {
                registrationDateString = size < 4 ? null : lines.get(3);
                notesString = size < 5 ? null : lines.get(4);
            }
        }

        if(parentIdString != null         && (parentIdString.equals(unspecified)         || parentIdString.isEmpty())        ) parentIdString = null;
        if(weightString != null           && (weightString.equals(unspecified)           || weightString.isEmpty())          ) weightString = null;
        if(manufacturer != null           && (manufacturer.equals(unspecified)           || manufacturer.isEmpty())          ) manufacturer = null;
        if(registrationDateString != null && (registrationDateString.equals(unspecified) || registrationDateString.isEmpty())) registrationDateString = null;
        if(notesString != null            && (notesString.equals(unspecified)            || notesString.isEmpty())           ) notesString = null;
        if(foodWeightString != null       && (foodWeightString.equals(unspecified)       || foodWeightString.isEmpty())      ) foodWeightString = null;
        if(expirationDateString != null   && (expirationDateString.equals(unspecified)   || expirationDateString.isEmpty())  ) expirationDateString = null;

        if(manufacturer != null && manufacturer.isEmpty()) manufacturer = null;

        int parentId = -1, weight = -1;
        final LocalDateTime registration;
        try {
            if(parentIdString != null) parentId = toInt(parentIdString);
        } catch (Exception e) {
            Log.warn("Soubor \"" + file.getAbsolutePath() + "\" obsahuje špatně zapsané ID nadřazeného skladového objektu (\"" + parentIdString + "\")" +
                    "\n  Toto ID musí být přirozené číslo v rozsahu (1 až 2147483647)" +
                    "\n  K této chybě by za normálních okolností docházet nemělo, pravděpodobně tento soubor někdo upravil nežádoucím způsobem." +
                    "\n  Prozatím budu tento problém ignorovat a s tímto souborem nebudu pracovat.");
            return null;
        }
        try {
            if(weightString != null) weight = toInt(weightString);
        } catch (Exception e) {
            Log.warn("Soubor \"" + file.getAbsolutePath() + "\" obsahuje špatně zapsanou váhu (\"" + parentIdString + "\")" +
                    "\n  Váha musí být zapsána pozitivním přirozeným číslem v gramech" +
                    "\n  K této chybě by za normálních okolností docházet nemělo, pravděpodobně tento soubor někdo upravil nežádoucím způsobem." +
                    "\n  Prozatím budu tento problém ignorovat a s tímto souborem nebudu pracovat.");
            return null;
        }
        try {
            final String[] parts = registrationDateString.split("/");
            final String[] mainPart = parts[0].split("-"), detailPart = parts[1].split("-");
            int year = toInt(mainPart[2]), month = toInt(mainPart[1]), day = toInt(mainPart[0]), hours = toInt(detailPart[0]),
                    minutes = toInt(detailPart[1]), seconds = toInt(detailPart[2]);
            registration = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.of(hours, minutes, seconds));
        } catch (Exception e) {
            Log.warn("Soubor \"" + file.getAbsolutePath() + "\" obsahuje špatně zapsaný datum registrace (\"" + registrationDateString + "\")" +
                    "\n  Správný formát datumu: dd-mm-yyyy/hh-nn-ss, takže například 24-12-2021/18-09-31" +
                    "\n  K této chybě by za normálních okolností docházet nemělo, pravděpodobně tento soubor někdo upravil nežádoucím způsobem." +
                    "\n  Tato informace je automaticky generovaná programem a uživatel ji nikam nezadává" +
                    "\n  Prozatím budu tento problém ignorovat a s tímto souborem nebudu pracovat.");
            return null;
        }

        ArrayList<String> notes = notesString == null ? null : new ArrayList<>();
        if(notes != null) {
            notes.addAll(Arrays.asList(notesString.split(noteSeparator)));
            if(notes.isEmpty()) notes = null;
        }

        if(type == TypeID.POTRAVINA) {
            int foodWeight = -1;
            final LocalDate expirationDate;
            try {
                if(foodWeightString != null) foodWeight = toInt(foodWeightString);
            } catch (Exception e) {
                Log.warn("Soubor \"" + file.getAbsolutePath() + "\" obsahuje špatně zapsanou váhu potraviny " + " (\"" + foodWeightString + "\")" +
                        "\n  Váha musí být zapsána pozitivním přirozeným číslem v gramech" +
                        "\n  K této chybě by za normálních okolností docházet nemělo, pravděpodobně tento soubor někdo upravil nežádoucím způsobem." +
                        "\n  Prozatím budu tento problém ignorovat a s tímto souborem nebudu pracovat.");
                return null;
            }
            try {
                final String[] parts = expirationDateString.split("-");
                final int day = toInt(parts[0]), month = toInt(parts[1]), year = toInt(parts[2]);
                expirationDate = LocalDate.of(year, month, day);
            } catch (Exception e) {
                Log.warn("Soubor \"" + file.getAbsolutePath() + "\" obsahuje špatně zapsaný datum expirace (\"" + expirationDateString + "\")" +
                        "\n  Správný formát datumu expirace: dd-mm-yyyy, takže například 24-12-2021" +
                        "\n  K této chybě by za normálních okolností docházet nemělo, pravděpodobně tento soubor někdo upravil nežádoucím způsobem." +
                        "\n  Prozatím budu tento problém ignorovat a s tímto souborem nebudu pracovat.");
                return null;
            }

            return new Potravina(id, name, parentId, weight, manufacturer, registration, registrationDateString, notes, notesString, foodWeight,
                    expirationDate, expirationDateString);

        } else if(type == TypeID.SKLADOVY_OBJEKT) return new SkladovyObjekt(id, name, parentId, weight, registration, registrationDateString, notes, notesString);
        else return new Produkt(id, name, parentId, weight, manufacturer, registration, registrationDateString, notes, notesString);
    }

    // writes a Produkt instance to a file
    public static void writeProdukt(Produkt produkt) {
        createIfNotExists(databaseFolder);
        if(produkt == null) return;
        final String extension = getExtensionByType(produkt.type);
        if(extension == null) return;
        final File file = new File(databaseFolder.getPath() + "/" + produkt.id + "." + extension);
        final String
                parentId = produkt.parentId == -1 ? unspecified : produkt.parentId+"",
                weight = produkt.weight == -1 ? unspecified : produkt.weight+"",
                manufacturer = produkt.manufacturer == null ? unspecified : produkt.manufacturer,
                registrationString = produkt.registrationString == null ? unspecified : produkt.registrationString,
                notesString = produkt.notesString == null ? unspecified : produkt.notesString;
        final String content;
        switch (produkt.type) {
            case TypeID.PRODUKT -> content = produkt.name + "\n" + parentId + "\n" + weight + "\n" + manufacturer + "\n" + registrationString + "\n" + notesString;
            case TypeID.POTRAVINA -> {
                final Potravina potravina = (Potravina) produkt;
                final String
                        foodWeight = potravina.foodWeight == -1 ? unspecified : potravina.foodWeight+"",
                        expirationString = potravina.expirationString == null ? unspecified : potravina.expirationString;
                content = produkt.name + "\n" + parentId + "\n" + weight + "\n" + manufacturer + "\n" +
                        registrationString + "\n" + notesString + "\n" + foodWeight + "\n" + expirationString;
            }
            case TypeID.SKLADOVY_OBJEKT -> content = produkt.name + "\n" + parentId + "\n" + weight + "\n" + registrationString + "\n" + notesString;
            default -> content = null;
        }
        if(content == null) return;
        write(file, content);
    }

    // return all the files in database folder
    public static File[] getItemFiles() {
        createIfNotExists(databaseFolder);
        final File[] files = databaseFolder.listFiles();
        if(files == null) {
            Log.error("Něco se pokazilo při získávání souborů ze složky '" + databaseFolder.getPath() + "'.");
            return new File[0];
        }
        return files;
    }

    // converts String type to an Integer
    private static int toInt(String s) {
        return Integer.parseInt(s);
    }

    public static ArrayList<String> getManufacturers() {
        final ArrayList<String> manufacturers = readLines(new File(appDataFolder.getPath() + "/" + manufacturersFilePath));
        final ArrayList<String> notEmptyManufacturers = new ArrayList<>();
        if(manufacturers == null) return notEmptyManufacturers;
        for(String manufacturer: manufacturers) if(!manufacturer.isEmpty()) notEmptyManufacturers.add(manufacturer);
        return notEmptyManufacturers;
    }

    public static void setManufacturers(ArrayList<String> manufacturers) {
        writeLines(new File(appDataFolder.getPath() + "/" + manufacturersFilePath), manufacturers);
    }

    public static File getWebFile(String filePath) {
        return new File(webFolder.getPath() + "/" + filePath);
    }
}
