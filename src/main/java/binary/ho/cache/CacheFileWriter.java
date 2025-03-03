package binary.ho.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CacheFileWriter<T extends Serializable> {

    private final String filePath;

    public CacheFileWriter(String filePath) {
        this.filePath = filePath;
    }

    public void write(T modules) {
        createParentDirectory();

        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream)) {
            objectOutputStream.writeObject(modules);
        } catch (IOException e) {
            throw new IllegalStateException("[ERROR] cache saving error: " + e.getMessage(), e);
        }
    }

    private void createParentDirectory() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}
