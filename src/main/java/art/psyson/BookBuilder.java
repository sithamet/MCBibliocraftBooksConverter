package art.psyson;

import art.psyson.util.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static art.psyson.util.CODES.RED;
import static art.psyson.util.CODES.RESET;

public class BookBuilder {

    Session session;

    Logger l;

    public BookBuilder(Session session) {
        l = new Logger(this);
        this.session = session;
    }

    public void buildBooks() {
        l.log("Starting the building process...");


        for (File file : session.getFiles()) {
            boolean fileBuildSucceed = false;

            List<List<String>> buffer = new ArrayList<>();
            scanRawBooksIntoBuffer(file, buffer);
            logBufferContent(buffer);


            for (List<String> section : buffer) {
                Book book = new Book();
                boolean titleFound = false;
                boolean descriptionFound = false;
                boolean contentFound = false;

                for (int i = 0; i < section.size(); i++) {
                    String line = section.get(i);

                    //search for title
                    titleFound = isTitleFound(section, i, line);
                    if (titleFound) {
                        book.setTitle(line);
                    }

                    //search for description
                    descriptionFound = isDescriptionFound(section, i, line);
                    if (descriptionFound) {
                        parseBookDescription(section, book, i);
                    }

                    //search for content


                }

                if (titleFound && descriptionFound && contentFound) {
                    session.getBooks().add(book);
                    l.log("Book + \"" + book.title.substring(3) + "\"");
                } else {
                    l.log("Building of this book failed.");
                }


            }


        }


    }

    private void parseBookDescription(List<String> section, Book book, int i) {
        for (int k = i + 5; k < section.size(); k++) {
            String s = section.get(k);
            if (s.startsWith("&")) {
//                if (s.toCharArray().length > 61) { //todo broken char calc
//                    l.log(RED + "Error! " + RESET + "Description is longer than allowed 61 char, length ="
//                            + s.toCharArray().length + "\n Descr: " + RED + s + RESET);
//                    break;
//                }

                book.description().add(s);
                l.log("Line added to description: " + s);
            } else {
                break;
            }
        }
    }

    private boolean isDescriptionFound(List<String> section, int i, String line) {
        if (Pattern.compile("Описание \\(").matcher(line).find()) {
            l.log("Description section is found: " + line);

            String luaLine = section.get(i + 4);
            String contentLine = section.get(i + 5);
            l.log("lua is " + luaLine);
            l.log("Description 1st line is " + contentLine);

            if (Pattern.compile("^```").matcher(luaLine).lookingAt()
                    &&
                    Pattern.compile("^&").matcher(contentLine).lookingAt()) {
                l.log("Valid description  found");
                return true;
            }
        }
        return false;
    }

    private boolean isTitleFound(List<String> section, int i, String line) {
        if (Pattern.compile("Название предмета").matcher(line).find()) {
            l.log("Title section is found: " + line);

            String luaLine = section.get(i + 4);
            String contentLine = section.get(i + 5);
//            if (contentLine.toCharArray().length > 35) { //todo broken char calculation
//                l.log(RED + "Error! " + RESET + "Title is longer than allowed 35 char, length ="
//                        + contentLine.toCharArray().length + "\n Title: " + RED + contentLine + RESET );
//                return false;
//            }

            l.log("lua is " + luaLine);
            l.log("Content is " + contentLine);

            if (Pattern.compile("^```").matcher(luaLine).lookingAt()
                    &&
                    Pattern.compile("^&").matcher(contentLine).lookingAt()) {
                l.log("Valid book title found");
                return true;
            }
        }
        return false;
    }

    private void logBufferContent(List<List<String>> buffer) {
        l.log("Logging buffer parts...");

        for (int i = 0; i < buffer.size(); i++) {
            l.log("BUFFER PART " + i);
            for (String s : buffer.get(i)) {
                System.out.println(s);
            }
            l.log("END OF PART " + i);
        }
    }

    /**
     * Scans .md files, find "# " 1st-level headings, and splits the file into raw parts.
     * Most likely, the first part will always be task description & checklist
     *
     * @param file   .md file from Notion export
     * @param buffer Arraylist of <code>ArrayList String</code> that holds lines
     */
    private void scanRawBooksIntoBuffer(File file, List<List<String>> buffer) {
        Scanner scanner = null;

        try {
            scanner = new Scanner(new FileReader(file));

        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> currentPart = new ArrayList<>();
        buffer.add(currentPart);

        int lineIndex = 0;

        while (true) {

            String line;
            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException e) {
                l.log("File is over");
                break;
            }

            Pattern pattern = Pattern.compile("^# ");
            Matcher matcher = pattern.matcher(line);
            if (matcher.lookingAt() && lineIndex != 0) {
                l.log("Found new chapter, creating a new buffer...");
                currentPart = new ArrayList<>();
                buffer.add(currentPart);
            }
            currentPart.add(line);
            lineIndex++;
            l.log("Added line: \"" + line + "\"");
        }
    }


}
