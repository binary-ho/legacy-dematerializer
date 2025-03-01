package binary.ho.util;

public class FileNameExtractor {

    private static final char DELIMETER = '_';

    public static String extractLastPart(String fileName) {
        String removedExtension = removeFileExtension(fileName);
        return extractLastPartByDelimeter(removedExtension);
    }

    private static String removeFileExtension(String fileName) {
        return fileName.replaceAll("\\.(?:pc|c)$", "");
    }

    private static String extractLastPartByDelimeter(String fileName) {
        return fileName.substring(fileName.lastIndexOf(DELIMETER) + 1);
    }
}
