package art.psyson.util;

import art.psyson.Main;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static art.psyson.util.CODES.RED;
import static art.psyson.util.CODES.RESET;

public class Functions {

    static Logger l = new Logger(new Functions());

    private Functions() {
    }

    public static int getLength(String s) {
        byte[] bytes = s.getBytes();
        return new String(bytes, StandardCharsets.UTF_8).length();
    }


    public static String formatAsHeader(String header) {
        return "---" + header.toUpperCase(Locale.ROOT) + "---";

    }

    public static void printList(List<String> list) {
        list.forEach(System.out::println);
    }

    public static void printMapToLog(Map<String, String> map) {
        for (var entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    public static void logSet(Set<String> set) {
        l.log("Set includes...");
        for (String s : set) {
            l.log(s);
        }
    }

    public static String toUtfString(String s) {
        byte[] bytes = s.getBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String replacePatternWith(String testInput, Pattern pattern, String startTag, String finishTag) {
        String s = "";
        Matcher matcher = pattern.matcher(testInput);

        boolean found = matcher.find();
//        try {
//            l.log("Section found = " + found);
//            l.log("Start at " + matcher.start());
//            l.log("End  at " + matcher.end());
//            matcher.reset();
//        } catch (IllegalStateException e) {
//            l.log(RED + "Error: " + RESET + e.getMessage());
//        }

        if (found) {
            while (matcher.find()) {
                s = matcher.replaceFirst(startTag);
                matcher = pattern.matcher(s);
                matcher.find();
                s = matcher.replaceFirst(finishTag);
                matcher = pattern.matcher(s);
            }
        } else {
            s = testInput;
        }


//        l.log("Result is = " + s);
        return s;
    }
}
