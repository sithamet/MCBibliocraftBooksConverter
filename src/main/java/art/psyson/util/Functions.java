package art.psyson.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Functions {

    static Logger l = Logger.STATIC_LOGGER;

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

}
