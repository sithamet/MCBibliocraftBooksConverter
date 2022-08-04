import art.psyson.Main;
import art.psyson.util.FileTool;
import art.psyson.util.Functions;
import art.psyson.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static art.psyson.Main.SEP;
import static art.psyson.Main.TEMP;
import static art.psyson.util.CODES.*;

public class test {

    final static String MC_BOLD = "§l";
    final static String MC_ITAL = "§o";
    final static String MC_RESET = "§r";

    static Logger l = new Logger(new Main());
    static ArrayList<ArrayList<String>> pages;
    static ArrayList<String> lines;

    static int LEN = 44;
    static int LINES = 21;


    public static void main(String[] args) throws FileNotFoundException {

        pages = new ArrayList<>();
        lines = new ArrayList<>();
        pages.add(lines);

        String input = "Товарищ! В огромном мире Полотна живут " +
                "тысячи, а то и десятки тысяч существ" +
                " различного происхождения и расы. Они, попав в свои" +
                " мелкие островки, начинают так или иначе" +
                " менять мир по своему образу и подобию. В" +
                " их сердцах течёт, подобно реке, энтузиазм и " +
                "храбрые начинания.";

//        input = Functions.toUtfString(input);
        String test = input;


        l.log("Default length = " + test.length());
        l.log("UTF length = " + Functions.getLength(test));




        String[] content = input.split(" ");


        String currentLine = "";

        int index = 0;
        int end = content.length;
        for (String s : content) {
            int currentLen = Functions.getLength(currentLine);
            int wordLen = Functions.getLength(s);
            l.log("Adding word \""+ YELLOW +  "%s" + RESET + "\"." +
                    "\nLL=%d," +
                    "\nWL=%d," +
                    "\nLnum=%d,\nLL+WL=%d", s, currentLen, wordLen, lines.size(), currentLen + wordLen);


            // if current line is shorter than limit
            if (currentLen < LEN) {
                l.log(YELLOW + "major LL < " + LEN + RESET);

                //if after adding next word with a space the limit is still available
                if (currentLen + wordLen + 1 <= LEN) {
                    l.log("minor LL + WL + 1 <" + LEN);

                    currentLine = currentLine + s + " ";
                    index++;
                } else

                //if after adding next word, the limit is over
                if (currentLen + wordLen == LEN) {
                    l.log("minor LL + WL ==" + LEN);
                    currentLine = currentLine + s;

                    //if there empty lines left on the page to add new one
                    //- add a new line
                    addNewLine(currentLine);
                    currentLine = "";
                    index++;

                } else

                //if the next word is too big, we need to create a new line
                if (currentLen + wordLen + 1 > LEN) {
                    l.log("minor LL + WL + 1 > " + LEN);
                    addNewLine(currentLine);
                    currentLine = s + " ";
                    index++;
                }

            } else if (currentLen == LEN) {
                l.log(YELLOW + " major LL == " + LEN + RESET);
                addNewLine(currentLine);
                currentLine = s + " ";
                index++;
            } else {
                l.log(RED + "Fatal Error! " + RESET + " The case does not match any cases!");

            }


            if (index == end) {
                l.log(YELLOW + "end of content reached" + RESET);
                addNewLine(currentLine);
            }

        }


        l.log("Logging converted strings in " + pages.size() + " pages...");

        for (int i = 0; i < pages.size(); i++) {
            ArrayList<String> lines = pages.get(i);
            l.log("Page " + i + " is " + lines.size() +  " lines long...");

            for (int k = 0; k < lines.size(); k++) {
                l.log(k + ":" + lines.get(k));
            }
        }

    }

    private static void addNewLine( String currentLine) {
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


//        String testInput = "А если ***жирный*** нежирный *италиксы* и дальше **снова жирный**";
//
//
////        testInput = Functions.toUtfString(testInput);
//        System.out.println(testInput);
//
//        //translating bold
//
//        Pattern pattern = Pattern.compile("\\*\\*");
//        testInput = Functions.replacePairPatternWith(testInput, pattern, "11", "22");
//
//        pattern = Pattern.compile("\\*");
//        testInput = Functions.replacePairPatternWith(testInput, pattern, "<i>", "</i>");
//
//        pattern = Pattern.compile("\\*");
//        testInput = Functions.replacePairPatternWith(testInput, pattern, "<i>", "</i>");
//
//


//        String[] blocks = testInput.split("\\*\\*");
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < blocks.length; i++) {
//                builder.append(MC_BOLD);
//                builder.append(blocks[i]);
//                builder.append(MC_RESET);
//        }
//
//        l.log(builder.toString());


//        int matchCount = 1;
//        while (matcher.find()) {
//
//            l.log("Bold starts at index %d finishes index %d", matcher.start(), matcher.end());
//            String buf = new String();
//            if (matchCount % 2 != 0) {
//                buf = testInput.substring(0, matcher.start()) + MC_BOLD + testInput.substring(matcher.end(), testInput.length()-1);
//            } else {
//                buf = testInput.substring(0, matcher.start()) + MC_RESET + testInput.substring(matcher.end(), testInput.length()-1);
//            }
//            matchCount++;
//            l.log(GREEN + "Breaking down structure:" + RESET);
//            l.log(testInput.substring(0, matcher.start()));
//            l.log(testInput.substring(matcher.end(), testInput.length()-1));
////            l.log(buf.toString());
//            testInput = buf;
//        }

//        l.log(GREEN + testInput + RESET);


//    }

}
