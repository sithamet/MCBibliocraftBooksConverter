import art.psyson.Main;
import art.psyson.util.Functions;
import art.psyson.util.Logger;

import java.util.regex.Pattern;

public class test {

    final static String MC_BOLD = "§l";
    final static String MC_ITAL = "§o";
    final static String MC_RESET = "§r";

    static Logger l = new Logger(new Main());

    public static void main(String[] args) {


        String testInput = "А если ***жирный*** нежирный *италиксы* и дальше **снова жирный**";


//        testInput = Functions.toUtfString(testInput);
        System.out.println(testInput);

        //translating bold

        Pattern pattern = Pattern.compile("\\*\\*");
        testInput = Functions.replacePatternWith(testInput, pattern, "11", "22");

        pattern = Pattern.compile("\\*");
        testInput = Functions.replacePatternWith(testInput, pattern, "<i>", "</i>");

        pattern = Pattern.compile("\\*");
        testInput = Functions.replacePatternWith(testInput, pattern, "<i>", "</i>");




//        String[] blocks = testInput.split("\\*\\*");
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < blocks.length; i++) {
//                builder.append(MC_BOLD);
//                builder.append(blocks[i]);
//                builder.append(MC_RESET);
//        }
//
//        l.log(builder.toString());


        int matchCount = 1;
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


    }

}
