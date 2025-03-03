package binary.ho.app;

import binary.ho.cache.CacheFileWriter;
import binary.ho.config.ConfigManager;
import binary.ho.function.FunctionCallFinder;
import binary.ho.function.FunctionDefinitionFinder;
import binary.ho.function.FunctionParser;
import binary.ho.module.CModule;
import binary.ho.module.CModuleSearcher;
import binary.ho.module.CModules;
import binary.ho.query.ProC_QueryParser;
import binary.ho.query.ProC_QueryRemover;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class CacheCreator {

    private static final String CACHE_DIRECTORY = "cache/modules.ser";

    public static void main(String[] args) {
        System.out.println("=== 캐시 파일 생성 ===");

        if (Files.exists(Paths.get(CACHE_DIRECTORY))) {
            System.out.println("에러: 캐시 파일이 이미 존재합니다: " + CACHE_DIRECTORY);
            System.out.println("다시 생성하려면 직접 삭제하셔야 합니다.");
            System.out.println("프로그램을 종료합니다.");
            System.exit(0);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("소스 코드 루트 입력: ");
        String sourcePath = scanner.nextLine().trim();

        validateSourcePath(sourcePath);

        System.out.println("설정 로드 중...");
        ConfigManager config = new ConfigManager();
        FunctionParser functionParser = loadFromConfig(config);

        // 모듈 검색 시작
        System.out.println("모듈 검색 시작...");
        long startTime = System.currentTimeMillis();

        List<CModule> modules = CModuleSearcher.searchFrom(sourcePath, functionParser);
        CModules cModules = new CModules(modules);

        long endTime = System.currentTimeMillis();
        System.out.println("모듈 검색 완료. 총 " + modules.size() + "개의 모듈을 찾았습니다.");
        System.out.println("소요 시간: " + (endTime - startTime) / 1000.0 + "초");

        // 캐시 파일 저장
        System.out.println("캐시 파일 저장 중...");
        CacheFileWriter<CModules> writer = new CacheFileWriter<>(CACHE_DIRECTORY);
        writer.write(cModules);

        System.out.println("캐시 파일 저장 완료: " + CACHE_DIRECTORY);
        System.out.println("프로그램을 종료합니다.");
    }

    private static FunctionParser loadFromConfig(ConfigManager config) {
        FunctionDefinitionFinder functionDefinitionFinder = getFunctionDefinitionFinder(config);
        return new FunctionParser(functionDefinitionFinder);
    }

    private static FunctionDefinitionFinder getFunctionDefinitionFinder(ConfigManager config) {
        ProC_QueryParser proCQueryParser = new ProC_QueryParser(config.getProcSqlPattern());
        ProC_QueryRemover proCQueryRemover = new ProC_QueryRemover(config.getProcSqlPattern());

        FunctionCallFinder functionCallFinder = new FunctionCallFinder(
            config.getFunctionCallPattern(),
            config.getExcludeFunctions()
        );

        return new FunctionDefinitionFinder(
            config.getFunctionDefinitionPattern(),
            config.getExcludeFunctions(),
            functionCallFinder,
            proCQueryParser,
            proCQueryRemover
        );
    }

    private static void validateSourcePath(String rootPath) {
        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.err.println("오류: 유효한 디렉토리 경로가 아닙니다: " + rootPath);
            System.exit(1);
        }
    }
}
