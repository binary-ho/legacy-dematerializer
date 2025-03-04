package binary.ho.file;

public class FileNameExtractor {

    private static final char WINDOW_PATH_DELIMITER = '\\';
    private static final char PATH_DELIMITER = '/';

    public static String extractFileName(String filePath) {
        String fileName = getFileName(filePath);
        return removeFileExtension(fileName);
    }

    private static String getFileName(String filePath) {
        String fileName = removePath(filePath, PATH_DELIMITER);
        return removePath(fileName, WINDOW_PATH_DELIMITER);
    }

    private static String removePath(String filePath, char pathDelimiter) {
        int fileNameIndex = filePath.lastIndexOf(pathDelimiter) + 1;
        return filePath.substring(fileNameIndex);
    }

    private static String removeFileExtension(String fileName) {
        String[] split = fileName.split("\\.");
        if (split.length == 0) {
            return "";
        }
        return split[0];
    }
}
