package art.psyson;

import art.psyson.util.FileTool;
import art.psyson.util.Functions;
import art.psyson.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static art.psyson.util.CODES.*;
import static art.psyson.util.FileTool.createNewFile;

public class Main {


    static Logger l = new Logger(new Main());
    public static String SEP = File.separator;
    public static final String INPUT = "." + SEP + "INPUT";
    public static final String TEMP = "." + SEP + "TEMP";
    public static final String OUTPUT = "." + SEP + "OUTPUT";

    private static final String CHEST_CODE = INPUT + SEP + "FORCECODE" + SEP + "forceChestCode.txt";

    public static Session session;

    public static void main(String[] args) throws IOException {

        if (requiresFolderCreation()) return;

        startSession();

        while (session.getChestCode() == null) {
            try {
                initChestCode();
            } catch (IllegalArgumentException e) {
                System.out.println("Please try again.");
            }
        }


        getSessionFiles(INPUT);

        BookBuilder builder = new BookBuilder(session);

        builder.buildBooks();

        System.out.println(GREEN + session.getBooks().size() + " books have been built! Check OUTPUT folder in the launch directory!" + RESET);


    }

    private static void initChestCode() throws FileNotFoundException {

        File chestCode = new File(CHEST_CODE);
        if (!chestCode.exists()) {
            FileTool.createNewFile(CHEST_CODE);
            getChestCode();
        }
        Scanner scanner = new Scanner(chestCode);
        if (!scanner.hasNextLine()) {
            getChestCode();
        } else {
            getChestCodeFromFile(chestCode);
        }
        scanner.close();
    }

    private static boolean requiresFolderCreation() {
        if (!new File(INPUT).exists()) {
            System.out.println(YELLOW + "Creating INPUT directory..." + RESET);
            FileTool.createNewFile(CHEST_CODE);
            System.out.println(GREEN + "Please re-launch the app" + RESET);

            return true;
        }
        return false;
    }

    private static void getChestCodeFromFile(File chestCode) {
        System.out.println(
                "\u001B[32m"
                        + "Welcome to BiblioCraft book converter!"
                        + "\u001B[0m");

        System.out.println(
                "\u001B[32m"
                        + "Found a code in \"\\INPUT\\FORCECODE\\code.txt\""
                        + "\u001B[0m");


        Scanner scanner = null;
        StringBuilder builder = new StringBuilder();

        try {
            scanner = new Scanner(chestCode, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }

        String input = builder.toString();

        if (input.equals("")) {
            System.out.println(
                    "\u001B[31m"
                            + "Please paste the code!"
                            + "\u001B[0m");
        } else if (!input.matches("(.*)id:\"minecraft:chest\"(.*)")) {
            System.out.println(
                    "\u001B[31m"
                            + "Please paste the valid code!" + RESET + " It must have " + YELLOW_B + "\"id:\"minecraft:chest\"," + RESET + " and empty " + YELLOW_B + "Items[]" + RESET + " array");
        }


        session.setChestCoordinateX(parseChestCoordinates(input, "x"));
        session.setChestCoordinateY(parseChestCoordinates(input, "y"));
        session.setChestCoordinateZ(parseChestCoordinates(input, "z"));


        l.log(session.getChestCode());

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
                        + "Please copy an NBT code from an empty in-game chest, paste it into the console and press ENTER 2 times \n Or paste the code into \"\\INPUT\\FORCECODE\\code.txt\""
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


            checkChestCodeValid(input);

//                l.log("Input before truncation");
//                l.log(input);



            String path = INPUT + SEP + "chestCode" + SEP + session.getSessionTimeStamp() + SEP + "code.txt";
            FileTool.createNewFile(path);
            PrintWriter writer = null;

            try {
                writer = new PrintWriter(new File(path));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            writer.println(input);
            writer.close();

            session.setChestCoordinateX(parseChestCoordinates(input, "x"));
            session.setChestCoordinateY(parseChestCoordinates(input, "y"));
            session.setChestCoordinateZ(parseChestCoordinates(input, "z"));

            gotCode = true;

        } while (!gotCode);
    }

    private static void checkChestCodeValid(String input) throws IllegalArgumentException {
        if (input.equals("")) {
            System.out.println(
                    "\u001B[31m"
                            + "Please paste the code!"
                            + "\u001B[0m");
            throw new IllegalArgumentException();
        } else if (!input.matches("(.*)id:\"minecraft:chest\"(.*)")) {
            System.out.println(
                    "\u001B[31m"
                            + "Please paste the valid code!" + RESET + " It must have " + YELLOW_B +
                            "\"id:\"minecraft:chest\"," + RESET + " and empty " + YELLOW_B + "Items[]" + RESET + " array");
            throw new IllegalArgumentException();
        }
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

    private static int parseChestCoordinates(String chestCode, String coordinate) {
       Matcher matcher = Pattern.compile(coordinate + ":(|\\-)\\d+").matcher(chestCode);
        matcher.find();
        String result = matcher.group();
        l.log("Found coords: " +result);

        return Integer.parseInt(result.substring(2));
    }

}