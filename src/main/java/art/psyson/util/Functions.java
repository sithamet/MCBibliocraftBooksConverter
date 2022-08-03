package art.psyson.util;

import art.psyson.Main;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Functions {

    static Logger l = new Logger(new Functions());

    private Functions() {
    }


    public static String formatAsHeader(String header) {
        return "---" + header.toUpperCase(Locale.ROOT) + "---";

    }

    public static void printList (List<String> list) {
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
}
