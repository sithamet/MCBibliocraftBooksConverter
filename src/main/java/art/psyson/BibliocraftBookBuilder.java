package art.psyson;

import art.psyson.util.FileTool;
import art.psyson.util.Functions;
import art.psyson.util.Logger;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static art.psyson.Main.SEP;
import static art.psyson.Main.session;
import static art.psyson.util.CODES.*;
import static art.psyson.util.CODES.RESET;

public class BibliocraftBookBuilder {

    final int LINES = 21; //todo make use of fontSize and make size dynamic

    final int CHARS = 44;
    final Book book;

    final Logger l;

    ArrayList<String> lines;

    ArrayList<ArrayList<String>> pages;

    public BibliocraftBookBuilder(String fontSize, Book book) {

        this.book = book;
        l = new Logger(this);
        l.log("Book builder for book " + book.getPrettyTitle() + "is ready!");

    }

    public String build() {
        ArrayList<String> content = new ArrayList<>(book.content());
        BibliocraftBook finalBook = new BibliocraftBook();
        pages = finalBook.pages;
        lines = new ArrayList<>();
        pages.add(lines);


        for (String line : content) {
            String[] lineWords = line.split(" ");


            String currentLine = "";

            int index = 0;
            int end = lineWords.length;


            for (String s : lineWords) {
                int currentLen = Functions.getLength(currentLine);
                int wordLen = Functions.getLength(s);
                l.log("Adding word \"" + YELLOW + "%s" + RESET + "\"." +
                        "\nLL=%d," +
                        "\nWL=%d," +
                        "\nLnum=%d,\nLL+WL=%d", s, currentLen, wordLen, lines.size(), currentLen + wordLen);


                // if current line is shorter than limit
                if (currentLen < CHARS) {
                    l.log(YELLOW + "major LL < " + CHARS + RESET);

                    //if after adding next word with a space the limit is still available
                    if (currentLen + wordLen + 1 <= CHARS) {
                        l.log("minor LL + WL + 1 <" + CHARS);

                        currentLine = currentLine + s + " ";
                        index++;
                    } else

                        //if after adding next word, the limit is over
                        if (currentLen + wordLen == CHARS) {
                            l.log("minor LL + WL ==" + CHARS);
                            currentLine = currentLine + s;

                            //if there empty lines left on the page to add new one
                            //- add a new line
                            addNewLine(currentLine);
                            currentLine = "";
                            index++;

                        } else

                            //if the next word is too big, we need to create a new line
                            if (currentLen + wordLen + 1 > CHARS) {
                                l.log("minor LL + WL + 1 > " + CHARS);
                                addNewLine(currentLine);
                                currentLine = s + " ";
                                index++;
                            }

                } else if (currentLen == CHARS) {
                    l.log(YELLOW + " major LL == " + CHARS + RESET);
                    addNewLine(currentLine);
                    currentLine = s + " ";
                    index++;
                } else {
                    l.log(RED + "Fatal Error! " + RESET + " The case does not match any cases!");

                }


                if (index == end) {
                    l.log(YELLOW + "end of lineWords reached" + RESET);
                    addNewLine(currentLine);
                }

            }
        }


        String path = session.getTempSessionPath().toString() + SEP + "booklines " + book.ID + ".txt";
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


        return "";
    }

    private void addNewLine(String currentLine) {
        l.log("Adding a new line to a page with " + lines.size() + " lines");
        if (lines.size() < LINES) {
            lines.add(currentLine);
            // if no -- we need to create a new page aka lines list
        } else {
            pages.add(lines);
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


}

