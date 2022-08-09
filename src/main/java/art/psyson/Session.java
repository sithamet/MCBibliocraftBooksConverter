package art.psyson;

import art.psyson.util.FileTool;
import art.psyson.util.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static art.psyson.Main.SEP;
import static art.psyson.Main.TEMP;

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

    private String sessionTimeStamp;
    private Path tempSessionPath;
    private List<Book> books;
    private List<File> files;

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


    public String getChestCode() {
        if (chestCoordinateX == null || chestCoordinateY == null || chestCoordinateZ == null) {
            return null;
        } else {
            return String.format(chestCode, chestCoordinateX, chestCoordinateY, chestCoordinateZ);
        }
    }

    final String chestCode = "{\n x:%d,\n" +
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

    public Session(String sessionTimeStamp) {
        this.sessionTimeStamp = sessionTimeStamp;
        this.tempSessionPath = Path.of(TEMP + SEP + sessionTimeStamp);
        books = new ArrayList<>();
        this.files = new ArrayList<>();
        l = new Logger(this);
        tempSessionPath.toFile().mkdirs();
        l.log("New session at path%s created", tempSessionPath.toAbsolutePath());
    }
}
