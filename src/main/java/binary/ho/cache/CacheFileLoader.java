package binary.ho.cache;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CacheFileLoader<T extends Serializable> {

    private final String cacheFilePath;
    private final Class<T> cacheObjectType;

    public CacheFileLoader(String cacheFilePath, Class<T> cacheObjectType) {
        this.cacheFilePath = cacheFilePath;
        this.cacheObjectType = cacheObjectType;
    }

    public boolean isNotCached() {
        return !Files.exists(Paths.get(cacheFilePath));
    }

    public T loadFromFile() {
        if (isNotCached()) {
            throw new IllegalStateException("cache file not found: " + cacheFilePath);
        }

        try (
            FileInputStream fileInputStream = new FileInputStream(cacheFilePath);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream)) {
            return cacheObjectType.cast(objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("[ERROR] cache loading error: " + e.getMessage(), e);
        }
    }
}
