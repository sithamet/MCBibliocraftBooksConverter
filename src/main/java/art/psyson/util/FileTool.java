package art.psyson.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileTool {

    static Logger l = new Logger(new FileTool());

    private FileTool(){}

    public static void logFile(File file) throws RuntimeException {
        l.log("File name: " + file.getName());
        l.log("File path: " + file.getPath());
        l.log("File full path: " + file.getAbsolutePath());
        try {
            l.log("File full path: " + file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        l.log("Parent path: " + file.getParent());
        l.log("Is file: " + file.isFile());
        l.log("Is directory: " + file.isDirectory());
        l.log("Exists: " + file.exists());
    }

    public static void createNewFile(File file) {
//        logFile(file);

        File parentDir = file.getParentFile();

        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            if (file.createNewFile()) {
                l.log("File created: " + file.getName());
            } else {
                l.log("File already exists.");
            }
        } catch (IOException e) {
            l.log("An error occurred.");
            e.printStackTrace();
        }

    }

    public static void createNewFile(String path) {
        File file = new File(path);
        createNewFile(file);
    }

    public static void createNewFile(Path path) {
        File file = path.toFile();
        createNewFile(file);
    }



    public static Set<String> listFilesInDirectory(String directoryPath) {
        if(new File(directoryPath).listFiles() == null)  {
            l.log("Directory %s is empty", directoryPath);
            return new HashSet<>();
        }

        return Stream.of(new File(directoryPath).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }


    public static URI getURIfromPath(String input) {
        String UriPath = Paths.get(input).toString();

        StringBuilder builder = new StringBuilder();
        builder.append("file://");

        for (int i = 0; i < UriPath.length(); i++) {
            if (UriPath.charAt(i) == '\\') {
                builder.append('/');
            } else {
                builder.append(UriPath.charAt(i));
            }
        }

        return URI.create(builder.toString());

    }
}
