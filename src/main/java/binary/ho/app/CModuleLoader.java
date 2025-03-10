package binary.ho.app;

import binary.ho.cache.CacheFileLoader;
import binary.ho.module.CModules;

public class CModuleLoader {

    public static CModules loadFromCacheFile(String cacheDirectory) {
        try {
            CModules cModules = loadFromCache(cacheDirectory);
            System.out.println("캐시 파일 로드 완료");
            return cModules;
        } catch (Exception e) {
            throw new IllegalStateException("캐시 파일 로드 실패", e);
        }
    }

    private static CModules loadFromCache(String cacheDirectory) {
        CacheFileLoader<CModules> cModuleLoader = new CacheFileLoader<>(
            cacheDirectory, CModules.class);
        if (cModuleLoader.isNotCached()) {
            throw new IllegalStateException("CModule 파일이 존재하지 않습니다. 먼저 캐시 파일을 생성하세요.");
        }
        return cModuleLoader.loadFromFile();
    }
}
