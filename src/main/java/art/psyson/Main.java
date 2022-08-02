package art.psyson;

import art.psyson.util.FileTool;
import art.psyson.util.Logger;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Set;


import static art.psyson.util.FileTool.createNewFile;

public class Main {


    static Logger l = Logger.STATIC_LOGGER;
    public static String SEP = File.separator;
    public static final String INPUT = "." + SEP + "INPUT";
    public static final String TEMP = "." + SEP + "TEMP";
    public static final String OUTPUT = "." + SEP + "OUTPUT";

    public static void main(String[] args) throws IOException {

        Date date = Date.from(Instant.now());
        String pattern = "dd-MM-yyyy HH.mm.ss";
        String time = new SimpleDateFormat(pattern).format(date);

        Session session = new Session(time);

    Set<String> inputFiles = FileTool.listFilesInDirectory(INPUT);

//    for (String s : inputFiles) {
//
//        new ZipFile(INPUT + SEP + s).extractAll(TEMP);
//    }








    }
}