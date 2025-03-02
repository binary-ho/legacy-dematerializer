package binary.ho.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

    public Properties load(String configPath) {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new RuntimeException("설정 파일을 찾을 수 없습니다: " + configPath);
            }
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("설정 파일 로드 중 오류 발생: " + e.getMessage());
        }
    }
}
