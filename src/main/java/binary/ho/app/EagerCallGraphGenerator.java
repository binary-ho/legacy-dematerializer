package binary.ho.app;

import binary.ho.file.FileNameExtractor;
import binary.ho.module.CModules;
import binary.ho.writer.EagerModuleCallGraphWriter;
import java.io.File;
import java.util.Scanner;

public class EagerCallGraphGenerator {

    private static final String OUTPUT_DIRECTORY = "output";
    private static final String EXCEL_EXTENSION = ".xlsx";

    private final CModules modules;
    private final String outputFilePrefix;

    public EagerCallGraphGenerator(CModules modules, String outputFilePrefix) {
        this.modules = modules;
        this.outputFilePrefix = outputFilePrefix;
    }

    public void generateCallGraph() {
        createOutputDirectory();

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

            writeCallGraph(moduleName);
        }
    }

    private void writeCallGraph(String moduleName) {
        String outputFilePath = getOutputFilePath(moduleName);
        try (EagerModuleCallGraphWriter writer = new EagerModuleCallGraphWriter(modules,
            outputFilePath)) {
            System.out.println("콜 그래프 생성 중...");
            writer.write(moduleName);
            System.out.println("콜 그래프 생성 완료: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("오류: 콜 그래프 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getOutputFilePath(String moduleName) {
        String sanitizedModuleName = FileNameExtractor.extractFileName(moduleName);
        return OUTPUT_DIRECTORY + File.separator +
            outputFilePrefix + sanitizedModuleName + EXCEL_EXTENSION;
    }

    private void createOutputDirectory() {
        File directory = new File(OUTPUT_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IllegalStateException("출력 디렉토리 생성 실패: " + OUTPUT_DIRECTORY);
            }
        }
    }
}
