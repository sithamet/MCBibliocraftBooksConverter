package art.psyson.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

public class Logger {
    boolean ENABLED = true;
    Object loggingObject;
    public static final Logger STATIC_LOGGER = new Logger(new Object()); //todo create static logger subclass that accepts static class name string as an argument


    public static Set<String> LOG_MAP = Set.of("advisor.command.Action", "advisor.command.Command", "advisor.command.Command$Builder",
            "advisor.command.User", "advisor.net.actions.ExchangeAction", "advisor.net.requests.RequestReceiver",
            "advisor.net.requests.RequestReceiver$Builder", "advisor.util.Functions", "advisor.Main", "advisor.TestMain",
            "advisor.util.Logger", "advisor.net.Authorizer", "java.lang.Object", "advisor.net.requests.RequestSender");

    public void log(String message, Object... variables) {

        if (!LOG_MAP.contains(loggingObject.getClass().getName()) && ENABLED) {

            Date date = Date.from(Instant.now());
            String pattern = "dd-MM-yyyy HH:mm:ss";
            String time = new SimpleDateFormat(pattern).format(date);

            String className = loggingObject.getClass().getSimpleName();

            if (variables.length > 0) {

                System.out.printf("[LOG] "
                        + "(" + className
                        + " at " + time + ") - "
                        + message + "\n", variables);
            } else {
                System.out.println("[LOG] "
                        + "(" + className
                        + " at " + time + ") - "
                        + message);
            }
        }
    }

    public Logger(Object loggingObject) {
        this.loggingObject = loggingObject;
//        STATIC_LOGGER.log("Logger created for " + loggingObject.getClass().getSimpleName());


//        System.out.println(loggingObject.getClass().getName());
//        this.log("Logger attached. uwu");
    }

    //todo make a static logger for static classes

//    public Logger (String className) {
//
//    }
}
