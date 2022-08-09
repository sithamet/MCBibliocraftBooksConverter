package art.psyson;

import art.psyson.util.FileTool;
import art.psyson.util.Functions;
import art.psyson.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static art.psyson.Main.SEP;
import static art.psyson.Main.session;
import static art.psyson.util.CODES.*;
import static art.psyson.util.CODES.RESET;

public class BibliocraftBookBuilder {

    final int LINES = 21; //todo make use of fontSize and make size dynamic

    final int CHARS = 38;
    final Book book;

    final Logger l;

    ArrayList<String> lines;

    ArrayList<ArrayList<String>> pages;

    int slot;

    public BibliocraftBookBuilder(String fontSize, Book book, int slot) {

        this.book = book;
        this.slot = slot;
        l = new Logger(this);
        l.log("Book builder for book " + book.getPrettyTitle() + "is ready!");

    }

    public String build() {
        translateRawStringsToBiblioFormat();

        logBookLinesToFile();

        StringBuilder builder = new StringBuilder();
        builder.append("{");

        builder.append(String.format(ITEM_START, slot));

        //todo decompose this mess!
        l.log(builder.toString());


        builder.append("tag:{\n"); //tag contains pagesCurrent, pagesTotal, author, signed, pages (pageN + pageScaleN arrays), chapters array, display (description array, name string)
        builder.append("pagesCurrent:0,\n");
        builder.append(String.format("pagesTotal:%d,\n", pages.size()));

        if (book.author() == null) {
            builder.append("author: \"Издательство Скрипторес\",\n");
        } else {
            builder.append("author: \"" + book.author() + "\",\n");
        }

        if (book.getIcon() != null) {
            builder.append("texture: " + book.getIcon() + ",\n");
        }

        builder.append("signed: 1,\n");

        //building pages
        builder.append("pages: {\n");

        for (int i = 0; i < pages.size(); i++) {

            List<String> page = pages.get(i);
            StringBuilder pageBuilder = new StringBuilder();

            //adding page array start
            pageBuilder.append(String.format("page%d:[", i));

            //for each page line
            for (int k = 0; k < page.size(); k++) {

                pageBuilder.append("\"" + page.get(k) + "\"");
                //final line
                if (k != page.size() - 1) {
                    pageBuilder.append(",");
                }
            }
            pageBuilder.append("],\n");
            pageBuilder.append("\n");
//                    l.log(pageBuilder.toString());
            builder.append(pageBuilder.toString());

            // building page scale. //todo font-related adjustment
            builder.append(String.format(PAGE_SCALE_ARRAY, i));
            //final line comma

            builder.append(",");
            builder.append("\n"); //page break after scale
            builder.append("\n");

        }
        builder.append("},\n"); // close pages
//                l.log(BLUE + "current state " + RESET + "\n" + builder.toString());

        //todo add chapter search functionality

        builder.append("display:{\n"); //open display (aka rename)

        builder.append("Lore:[\n");//open lore

        for (int i = 0; i < book.description().size(); i++) {

            builder.append("\"" + book.description().get(i) + "\"");

            if (i != book.description().size() - 1) {
                builder.append(",");
            }
        }

        builder.append("],\n"); // close lore

        builder.append("Name:\"" + book.Title() + "\"");

        builder.append("}\n"); //close display

        builder.append("}\n"); //close tag
        builder.append("}\n"); //close item
        l.log("ITEM CODE \n" + builder.toString());
        return builder.toString();
    }

    private void translateRawStringsToBiblioFormat() {
        ArrayList<String> content = new ArrayList<>(book.content());
        BibliocraftBook finalBook = new BibliocraftBook();
        pages = finalBook.pages;
        lines = new ArrayList<>();


        parseIntoBibliolines(content);

        l.log(BLUE + "Book parsing finished. Adding last page..." + RESET);
        pages.add(lines);
    }

    private void parseIntoBibliolines(ArrayList<String> content) {
        for (String line : content) {

            //method to make tighter limit if they contain uppercase or unclosed code
            int limit = CHARS;

            String hasCode = "";
            Matcher matcher = Pattern.compile("^§l|^§m|^§o|—").matcher(line);
            if (matcher.find()) {
                l.log("Matcher found = " + matcher.group());
                hasCode = matcher.group();
                limit = CHARS-2;
            }

            String[] lineWords = line.split(" ");


            String currentLine = "";

            int index = 0;
            int end = lineWords.length;

            for (String s : lineWords) {


                int currentLen = Functions.getLength(currentLine);
                int wordLen = Functions.getLength(s);
                l.log("Adding word \"" + YELLOW + "%s" + RESET + "\"." + "\nLL=%d," + "\nWL=%d," + "\nLnum=%d,\nLL+WL=%d", s, currentLen, wordLen, lines.size(), currentLen + wordLen);


                // if current line is shorter than limit
                if (currentLen < limit) {
                    l.log(YELLOW + "major LL < " + limit + RESET);

                    //if after adding next word with a space the limit is still available
                    if (currentLen + wordLen + 1 <= limit) {
                        l.log("minor LL + WL + 1 <=" + limit);

                        currentLine = currentLine + s + " ";
                        index++;
                    } else

                        //if after adding next word, the limit is over
                        if (currentLen + wordLen == limit) {
                            l.log("minor LL + WL ==" + limit);
                            currentLine = currentLine + s;

                            //if there empty lines left on the page to add new one
                            //- add a new line
                            addNewLine(currentLine);
                            currentLine = "";
                            index++;

                        } else

                            //if the next word is too big, we need to create a new line
                            if (currentLen + wordLen + 1 > limit) {
                                l.log("minor LL + WL + 1 > " + limit);
                                addNewLine(currentLine); //приобрести
                                currentLine = s + " ";
                                index++;
                            }

                } else if (currentLen == limit) {
                    l.log(YELLOW + " major LL == " + limit + RESET);
                    addNewLine(currentLine);
                    currentLine = s + " ";
                    index++;
                } else {
                    l.log(RED + "Fatal Error! " + RESET + " The case does not match any cases!");

                }


                if (index == end) {
                    l.log(YELLOW + "end of raw String reached" + RESET);
                    addNewLine(currentLine);
                }

            }
        }
    }

    private void logBookLinesToFile() {
        String path = session.getTempSessionPath().toString() + SEP + "booklines" + SEP + book.ID + ".txt";
        FileTool.createNewFile(path);

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String startMessage = GREEN + "Logging converted strings in book " + "\"" + book.getPrettyTitle() + "\"" + pages.size() + " pages..." + RESET;
        assert writer != null;
        writer.println(startMessage);
        l.log(startMessage);

        for (int i = 0; i < pages.size(); i++) {
            ArrayList<String> lines = pages.get(i);
            String pageMessage = "Page " + i + " is " + lines.size() + " lines long...";
            writer.println(pageMessage);
            l.log(pageMessage);


            for (int k = 0; k < lines.size(); k++) {
                String message = k + ":" + lines.get(k);
                writer.println(message);
                l.log(message);

            }
        }
        writer.close();
    }

    /**
     * Adds new line to current page, or adds new page
     *
     * @param currentLine
     */
    private void addNewLine(String currentLine) {
        currentLine = currentLine.trim();
        l.log("Adding a new line to a page with " + lines.size() + " lines");
        if (lines.size() < LINES) {
            l.log("Adding line to the page");
            lines.add(currentLine);
            if (lines.size() == LINES) {
                l.log("Lines limit is reached=" + lines.size() +
                        ". Adding a new page to " + pages.size() + "-book");
                pages.add(lines);
                l.log("Adding a new page. Now pages num is " + pages.size());
                lines = new ArrayList<>();
            }
            // if no -- we need to create a new page aka lines list
        } else {
            l.log("ELSE Lines limit is reached=" + lines.size() +
                    ". Adding a new page to " + pages.size() + "-book");
            pages.add(lines);
            l.log("Adding a new fill page. Now pages num is " + pages.size());

            lines = new ArrayList<>();
            lines.add(currentLine);
        }
    }


    static class BibliocraftBook {
        ArrayList<ArrayList<String>> pages;

        BibliocraftBook() {
            pages = new ArrayList<>();
        }

    }

    static final String ITEM_START = "Slot:%db,\n" +
            "      ForgeCaps:{\"customnpcs:itemscripteddata\":{}},\n" +
            "      id:\"bibliocraft:bigbook\",\n" +
            "      Count:1b,\n" +
            "      Damage:0s,\n";


    static final String PAGE_SCALE_ARRAY = "pageScale%d:[I;3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3]";
}

