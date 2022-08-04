package art.psyson;

import art.psyson.util.Functions;
import art.psyson.util.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static art.psyson.util.CODES.*;
import static art.psyson.util.Functions.getLength;

public class BookBuilder {

    final static String MC_BOLD = "§l";
    final static String MC_ITAL = "§o";
    final static String MC_RESET = "§r";

    Session session;

    Logger l;

    public BookBuilder(Session session) {
        l = new Logger(this);
        this.session = session;
    }

    public void buildBooks() {
        l.log("Starting the building process...");


        Map<Book, String> bookReport = new HashMap<>();

        parseBooksFromScritoriaFiles(bookReport);

        removeInvalidBooksFromBuild(bookReport);

        convertMarkdownToMinecraft(session.getBooks());


    }

    private void convertMarkdownToMinecraft(List<Book> books) {
        l.log(YELLOW + "Translating Markdown into Minecraft codes..." + RESET);
        for (Book book : books) {
            l.log(GREEN + "Converting a book " + book.getPrettyTitle() + RESET);
            for (int i = 0; i < book.content().size(); i++) {
                String line = book.content().get(i);

                //replace bold
                Pattern pattern = Pattern.compile("\\*\\*");
                line = Functions.replacePairPatternWith(line, pattern, "§l", "§r");

                //replace italics
                pattern = Pattern.compile("\\*");
                line = Functions.replacePairPatternWith(line, pattern, "§o", "§r");

                //replace strikethrough
                pattern = Pattern.compile("~~");
                line = Functions.replacePairPatternWith(line, pattern, "§m", "§r");

                book.content().set(i, line);
//                l.log(book.content().get(i));
            }
            l.log(GREEN + "Book converted" + RESET);
        }
    }

    /**
     * Parses all files from the Session and turns them into raw books
     * It gets title, Description list, and raw content lists.
     * todo It's planned that the Book is the default format for conversion to BiblioCraft
     * while the Book can be built from different sources
     * @param bookReport a Book-String map to store parse results: failed to not. Pay attention that results are surrounded by ANCII color codes
     */
    private void parseBooksFromScritoriaFiles(Map<Book, String> bookReport) {
        for (File file : session.getFiles()) {

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
                    if (!titleFound) {
                        titleFound = isTitleFound(section, i, line, file);
                        if (titleFound) {
                            book.setTitle(section.get(i + 5));
                        }
                    }

                    //search for description
                    if (!descriptionFound) {
                        descriptionFound = isDescriptionFound(section, i, line);
                        if (descriptionFound) {
                            parseBookDescription(section, book, i, file);
                        }
                    }

                    //search for content
                    if (Pattern.compile("^### \\[").matcher(line).find() && !contentFound) {
                        l.log("Content section is found: " + line);

                        String contentLine = section.get(i + 2);
                        l.log("Content's 1st line is " + contentLine);
                        for (int k = i + 2; k < section.size(); k++) {
                            book.content().add(section.get(k));
                        }
                        l.log("Book content is loaded; ");
                        contentFound = true;
                    }


                }

                l.log("Title found is " + titleFound);
                l.log("Description found is " + descriptionFound);
                l.log("Content found is " + contentFound);

                if (titleFound && descriptionFound && contentFound) {
                    session.getBooks().add(book);
                    System.out.println(YELLOW + "Book + \"" + book.getPrettyTitle() + "\" is done" + RESET);
                    bookReport.put(book, GREEN + "done" + RESET);
                } else {
                    System.out.println(RED + "Error: Building of this book failed." + RESET);
                    bookReport.put(book, RED + "failed" + RESET);
                }
            }


        }
    }

    /**
     * Remove books that failed to built (no title/description, invalid title/descr, too loong title/descr
     * @param bookReport a map of books with statuses
     */
    private void removeInvalidBooksFromBuild(Map<Book, String> bookReport) {
        l.log(GREEN + "Logging resulting books..." + RESET);
        List<Book> newBooks = new ArrayList<>();
        for (Book book : session.getBooks()) {
            l.log(book.getPrettyTitle() + " " + bookReport.get(book));
            if (!bookReport.get(book).equals(RED + "failed" + RESET)) {
                newBooks.add(book);
            }
        }
        session.getBooks().clear();
        for (Book book : newBooks) {
            session.getBooks().add(book);
        }
    }


    /**
     * Adds valid Description lines to the book, testing against the format & length limits
     * @param section raw MD file section (list of lines between first-level # headings)
     * @param i line position of the loop inside the section
     * @param file current MD file (to refer error to the file, as no title may be available to use
     */
    private void parseBookDescription(List<String> section, Book book, int i, File file) {
        for (int k = i + 5; k < section.size(); k++) {
            String s = section.get(k);
            if (s.startsWith("&")) {

                int length = getLength(s);

                String name = file.getName();
                byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
                String nameDome = new String(bytes);


                if (length > 61) {
                    System.out.println(RED + "Error! in file " + nameDome + RESET + "\nDescription is longer than allowed 61 char, length ="
                            + length + "\n Descr: " + RED + s + RESET);
                } else {
                    book.description().add(s);
                    l.log("Line added to description: " + s);

                }


            } else {
                l.log(YELLOW + "End of description." + RESET);
                break;
            }
        }
    }

    /**
     * Find Scriptoria-style description and verify it matches the standard
     * @param section raw MD file section (list of lines between first-level # headings)
     * @param i line position of the loop inside the section
     * @param line Current loop line to be tested against Scriptoria markup
     * @return true if description is valid
     */
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

    /**
     * Find Scriptoria-style book title and verify it matches the standard. Also tests Title length
     * @param section raw MD file section (list of lines between first-level # headings)
     * @param i line position of the loop inside the section
     * @param line Current loop line to be tested against Scriptoria markup
     * @param file current MD file (to refer error to the file, as no title may be available to use
     * @return
     */
    private boolean isTitleFound(List<String> section, int i, String line, File file) {
        if (Pattern.compile("Название предмета").matcher(line).find()) {
            l.log("Title section is found: " + line);

            String luaLine = section.get(i + 4);
            String contentLine = section.get(i + 5);

            int length = getLength(contentLine);


            if (length > 35) {
                System.out.println(RED + "Error! in file " + file.getName() + RESET + "\nTitle is longer than allowed 35 char, length ="
                        + length + "\n Title: " + RED + contentLine + RESET);
                return false;
            }


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

    /**
     * Utility to log the content of the sections-divided MD file
     * @param buffer
     */
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
