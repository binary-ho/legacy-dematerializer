package binary.ho.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    public static String readFileContent(File file) {
        try {
            return Files.readString(Paths.get(file.getAbsolutePath()));
        } catch (Exception e) {
            throw new RuntimeException("file read error: " + file.getAbsolutePath(), e);
        }
    }
}
