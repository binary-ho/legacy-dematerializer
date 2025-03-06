package binary.ho.app;

import binary.ho.cache.CacheFileLoader;
import binary.ho.file.FileNameExtractor;
import binary.ho.graph.FunctionCallGraphSearcher;
import binary.ho.graph.Node;
import binary.ho.module.CModules;
import binary.ho.writer.CallGraphWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FunctionCallGraphGenerator {

    private static final String CACHE_DIRECTORY = "cache/modules.ser";
    private static final String OUTPUT_DIRECTORY = "output";
    private static final String CALL_GRAPH_FILE_PREFIX = "function_call_";
    private static final String EXCEL_EXTENSION = ".xlsx";

    public static void main(String[] args) {
        System.out.println("=== Function Call Graph 생성 ===\n");

        CModules cModules = loadModulesFromCacheFile();
        createOutputDirectory();
        run(cModules);
    }

    private static void run(CModules cModules) {
        System.out.println("함수 콜 그래프 생성을 시작합니다.\n");
        System.out.println("종료: 'exit'나, Ctrl + C 입력 \n");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\n모듈 이름을 입력하세요: ");
            String moduleName = scanner.nextLine().trim();

            if (moduleName.equalsIgnoreCase("exit")) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            try {
                generateCallGraph(cModules, moduleName);
            } catch (RuntimeException | IOException e) {
                System.err.println("오류: 콜 그래프 생성 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void generateCallGraph(CModules cModules, String moduleName) throws IOException {
        System.out.println("콜 그래프 생성 중...");

        Node rootNode = FunctionCallGraphSearcher.searchFromModule(moduleName, cModules);
        String outputFilePath = getOutputFilePath(moduleName);
        CallGraphWriter.write(rootNode, outputFilePath);
        System.out.println("콜 그래프 생성 완료: " + outputFilePath);
    }

    private static CModules loadModulesFromCacheFile() {
        try {
            CModules cModules = loadFromCache();
            System.out.println("캐시 파일 로드 완료");
            return cModules;
        } catch (Exception e) {
            throw new IllegalStateException("캐시 파일 로드 실패", e);
        }
    }

    private static CModules loadFromCache() {
        CacheFileLoader<CModules> cModuleLoader = new CacheFileLoader<>(CACHE_DIRECTORY,
            CModules.class);
        if (cModuleLoader.isNotCached()) {
            throw new IllegalStateException("CModule 파일이 존재하지 않습니다. 먼저 캐시 파일을 생성하세요.");
        }
        return cModuleLoader.loadFromFile();
    }

    private static String getOutputFilePath(String moduleName) {
        String sanitizedModuleName = FileNameExtractor.extractFileName(moduleName);
        return OUTPUT_DIRECTORY + File.separator +
            CALL_GRAPH_FILE_PREFIX + sanitizedModuleName + EXCEL_EXTENSION;
    }

    private static void createOutputDirectory() {
        File directory = new File(OUTPUT_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IllegalStateException("출력 디렉토리 생성 실패: " + OUTPUT_DIRECTORY);
            }
        }
    }
}
