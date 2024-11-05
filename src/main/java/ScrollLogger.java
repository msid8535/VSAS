import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ScrollLogger {

    // Public and static method so it can be accessed from other classes
    public static void logActionToFile(User user, String action, String scrollName) {
        Logger logger = Logger.getLogger("ScrollLogger");
        FileHandler fileHandler = null;

        try {
            // Create or append to the log file
            fileHandler = new FileHandler("scroll_actions.log", true);
            fileHandler.setFormatter(new Formatter() {
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                @Override
                public String format(LogRecord record) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(dateFormat.format(new Date(record.getMillis()))).append(" - "); // Date and time
                    builder.append(record.getLevel()).append(": ");
                    builder.append(formatMessage(record)).append("\n");
                    return builder.toString();
                }
            });

            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);  // Disable console logging

            // Log the action
            logger.info(user.getUsername() + " " + action + " " + scrollName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileHandler != null) {
                fileHandler.close();  // Close the file handler to release resources
            }
        }
    }
}
