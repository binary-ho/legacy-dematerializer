package binary.ho.util;

public class FileNameParser {

    private static final char PATH_DELIMETER = '/';

    public static String getFileName(String filePath) {
        int fileNameIndex = filePath.lastIndexOf(PATH_DELIMETER) + 1;
        return filePath.substring(fileNameIndex);
    }
}
