package art.psyson;

import art.psyson.util.FileTool;
import art.psyson.util.Functions;
import art.psyson.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

import static art.psyson.Main.*;
import static art.psyson.util.CODES.RED;
import static art.psyson.util.CODES.RESET;

public class Session {

    public String getSessionTimeStamp() {
        return sessionTimeStamp;
    }

    public Path getTempSessionPath() {
        return tempSessionPath;
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<File> getFiles() {
        return files;
    }

    private final String sessionTimeStamp;
    private final Path tempSessionPath;
    private final List<Book> books;
    private final List<File> files;

    private final int partitionSize;

    public int getPartitionSize() {
        return  partitionSize;
    }

    public final HashMap<String, String> CATEGORIES;

    public int getChestCoordinateX() {
        return chestCoordinateX;
    }

    public void setChestCoordinateX(int chestCoordinateX) {
        this.chestCoordinateX = chestCoordinateX;
    }

    public int getChestCoordinateY() {
        return chestCoordinateY;
    }

    public void setChestCoordinateY(int chestCoordinateY) {
        this.chestCoordinateY = chestCoordinateY;
    }

    public int getChestCoordinateZ() {
        return chestCoordinateZ;
    }

    public void setChestCoordinateZ(int chestCoordinateZ) {
        this.chestCoordinateZ = chestCoordinateZ;
    }

    Integer chestCoordinateX;
    Integer chestCoordinateY;
    Integer chestCoordinateZ;


    private Logger l;


    public String CHEST_CODE() {
        if (chestCoordinateX == null || chestCoordinateY == null || chestCoordinateZ == null) {
            return null;
        } else {
            return String.format(CHEST_CODE, chestCoordinateX, chestCoordinateY, chestCoordinateZ);
        }
    }

    final String CHEST_CODE = "{\n x:%d,\n" +
            "  y:%d,\n" +
            "  id:\"minecraft:chest\",\n" +
            "  z:%d,\n" +
            "  Lock:\"\",\n" +
            "  ForgeCaps:{\n" +
            "    \"misca:lock\": {\n" +
            "      secret:0,locked:0b,type:0b\n" +
            "    },\n" +
            "    \"misca:supplies\":{\n" +
            "      Last:0L,Price:1,MaxSeq:0,Batches:[],Int:1L}\n" +
            "  },\n" +
            "  Items:[";

    public Session(String sessionTimeStamp, int partitionSize) {
        this.partitionSize = partitionSize;
        CATEGORIES = new HashMap<>();
        initCategories();
        this.sessionTimeStamp = sessionTimeStamp;
        this.tempSessionPath = Path.of(TEMP + SEP + sessionTimeStamp);
        books = new ArrayList<>();
        this.files = new ArrayList<>();
        l = new Logger(this);
        tempSessionPath.toFile().mkdirs();
        l.log("New session at path%s created", tempSessionPath.toAbsolutePath());
    }

    private void initCategories() {
        Scanner scanner = null;

        try {
            scanner = new Scanner(
                    new FileReader(
                            new File(INPUT + SEP + "CATEGORIES" + SEP + "CATS.txt"), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println(RED + "Warning! " + RESET + "No categories file is found. Created one for you for next builds.");
            FileTool.createNewFile(INPUT + SEP + "CATEGORIES" + SEP + "CATS.txt");
            return;
        }

        while (scanner.hasNextLine()) {
            CATEGORIES.put(scanner.nextLine(), scanner.nextLine());
        }

        Functions.printMapToLog(CATEGORIES);
    }
}
