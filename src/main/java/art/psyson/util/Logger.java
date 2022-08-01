package art.psyson.util;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

public class Logger {

    boolean ENABLED = true;
    Object loggingObject;
    public static final Logger STATIC_LOGGER;

    static {
        STATIC_LOGGER = new Logger(new Object()); //todo create static logger subclass that accepts static class name string as an argument
    }


    public static Set<String> LOG_MAP = Set.of("advisor.command.Action", "advisor.command.Command", "advisor.command.Command$Builder",
            "advisor.command.User", "advisor.net.actions.ExchangeAction", "advisor.net.requests.RequestReceiver",
            "advisor.net.requests.RequestReceiver$Builder", "advisor.util.Functions", "advisor.Main", "advisor.TestMain",
            "advisor.util.Logger", "advisor.net.Authorizer", "java.lang.Object", "advisor.net.requests.RequestSender");

    public void log(String message, Object... variables) {

        if (LOG_MAP.contains(loggingObject.getClass().getName()) && ENABLED) {

            if (variables.length > 0) {

                System.out.printf("[LOG] "
                        + "(" + loggingObject.toString()
                        + " at " + Date.from(Instant.now()) + ") — "
                        + message + "\n", variables);
            } else {
                System.out.println("[LOG] "
                        + "(" + loggingObject.toString()
                        + " at " + Date.from(Instant.now()) + ") — "
                        + message);
            }
        }
    }

    public Logger(Object loggingObject) {
        this.loggingObject = loggingObject;

//        System.out.println(loggingObject.getClass().getName());
//        this.log("Logger attached. uwu");
    }

    //todo make a static logger for static classes

//    public Logger (String className) {
//
//    }
}
