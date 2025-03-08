package binary.ho.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger {

    private static final String LOG_PATH = "log/";
    private static final String LOG_EXTENSION = ".log";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "yyyyMMdd:HHmmss");

    public static void log(String... contents) {
        try {
            createLogDirectoryIfNotExists();
            String logFileName = getLogFileName();
            String logContent = createLogContent(contents);
            appendToFile(logFileName, logContent);
        } catch (Exception e) {
            System.err.println("로깅 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createLogDirectoryIfNotExists() throws IOException {
        File directory = new File(LOG_PATH);
        if (!directory.exists()) {
            Files.createDirectories(Paths.get(LOG_PATH));
        }
    }

    private static String getLogFileName() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DATE_TIME_FORMATTER);
        return LOG_PATH + datePart + LOG_EXTENSION;
    }

    private static String createLogContent(String[] contents) {
        StringBuilder builder = new StringBuilder();
        for (String content : contents) {
            builder.append(content).append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
        return builder.toString();
    }

    private static void appendToFile(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(content);
            writer.flush();
        }
    }
}
