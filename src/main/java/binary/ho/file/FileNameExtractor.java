package binary.ho.file;

public class FileNameExtractor {

    private static final char DELIMETER = '_';
    private static final char PATH_DELIMETER = '/';

    public static String extractLastPart(String filePath) {
        String fileName = getFileName(filePath);
        String removedExtension = removeFileExtension(fileName);
        return extractLastPartByDelimeter(removedExtension);
    }

    public static String getFileName(String filePath) {
        int fileNameIndex = filePath.lastIndexOf(PATH_DELIMETER) + 1;
        return filePath.substring(fileNameIndex);
    }

    private static String removeFileExtension(String fileName) {
        return fileName.replaceAll("\\.(?:pc|c)$", "");
    }

    private static String extractLastPartByDelimeter(String fileName) {
        return fileName.substring(fileName.lastIndexOf(DELIMETER) + 1);
    }
}
