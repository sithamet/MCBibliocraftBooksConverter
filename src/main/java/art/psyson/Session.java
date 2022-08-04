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

    private Logger l;


    public String getChestCode() {
        return chestCode;
    }

    public void setChestCode(String chestCode) {
        this.chestCode = chestCode;
    }

    String chestCode;

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
