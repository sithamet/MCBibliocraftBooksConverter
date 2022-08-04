package art.psyson;

import art.psyson.util.FileTool;
import art.psyson.util.Functions;
import art.psyson.util.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


import static art.psyson.util.CODES.*;
import static art.psyson.util.FileTool.createNewFile;

public class Main {


    static Logger l = new Logger(new Main());
    public static String SEP = File.separator;
    public static final String INPUT = "." + SEP + "INPUT";
    public static final String TEMP = "." + SEP + "TEMP";
    public static final String OUTPUT = "." + SEP + "OUTPUT";

    public static Session session;

    public static void main(String[] args) throws IOException {

        startSession();

//        getChestCode();
        getSessionFiles(INPUT);

        BookBuilder builder = new BookBuilder(session);

        builder.buildBooks();


    }

    /**
     * Gets session's .md files for scan from the specified location.
     *
     * @param location Location path string. session's temp location is Zip method is used, INPUT is .md method is used
     * @throws IOException
     */
    private static void getSessionFiles(String location) {
        System.out.println(GREEN + "Starting the scan..." + RESET);

        Set<String> inputFiles = FileTool.listFilesInDirectory(location);
        Functions.logSet(inputFiles);


        if (inputFiles.size() == 0) {
            try {
                System.out.println(RED + "No files found in " + session.getTempSessionPath().toFile().getCanonicalPath() + RESET);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        for (String s : inputFiles) {
            if (s.matches("(.*).md(.*)")) {
                session.getFiles().add(Path.of(location + SEP + s).toFile());
            }

        }

        for (File f : session.getFiles()) {
            FileTool.logFile(f);
        }
    }

    /**
     * Gets a NBT code from CustomNPC's NBT book. Used to generate a correct books file at the end
     */
    private static void getChestCode() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "\u001B[32m"
                        + "Welcome to BiblioCraft book converter!"
                        + "\u001B[0m");

        System.out.println(
                "\u001B[32m"
                        + "Please copy an NBT code from an empty in-game chest, paste it into the console and press ENTER"
                        + "\u001B[0m");
        System.out.print("\u001B[33m" + ">" + "\u001B[0m");

        boolean gotCode = false;
        do {
            String input = "";
            StringBuilder builder = new StringBuilder();


            while (scanner.hasNextLine()) {
                String newLine = scanner.nextLine();
                if (newLine.isEmpty()) {
                    break;
                }
                builder.append(newLine);
            }
            input = builder.toString();
            l.log("Got input...");


            if (input.equals("")) {
                System.out.println(
                        "\u001B[31m"
                                + "Please paste the code!"
                                + "\u001B[0m");
            } else if (!input.matches("(.*)id:\"minecraft:chest\"(.*)")) {
                System.out.println(
                        "\u001B[31m"
                                + "Please paste the valid code!" + RESET + " It must have " + YELLOW_B + "\"id:\"minecraft:chest\"," + RESET + " and empty " + YELLOW_B + "Items[]" + RESET + " array");
            } else {
//                l.log("Input before truncation");
//                l.log(input);

                StringBuffer buffer = new StringBuffer(input);
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.deleteCharAt(buffer.length() - 1);

//                l.log("Input after  truncation");
//                l.log(buffer.toString());

                session.setChestCode(buffer.toString());
                gotCode = true;
            }

        } while (!gotCode);
    }

    /**
     * Initializes a session and creates a unique folder in TEMP
     */
    private static void startSession() {
        Date date = Date.from(Instant.now());
        String pattern = "dd-MM-yyyy HH.mm.ss";
        String time = new SimpleDateFormat(pattern).format(date);

        session = new Session(time);
        l.log("Session path is %s", session.getTempSessionPath());
    }

}