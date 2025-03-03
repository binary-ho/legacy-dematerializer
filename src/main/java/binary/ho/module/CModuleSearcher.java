package binary.ho.module;

import binary.ho.file.FileReader;
import binary.ho.function.Function;
import binary.ho.function.FunctionParser;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CModuleSearcher {

    private static final String C_EXTENSION = ".c";
    private static final String PC_EXTENSION = ".pc";

    private final FunctionParser functionParser;
    private final List<CModule> modules;

    private CModuleSearcher(FunctionParser functionParser) {
        this.functionParser = functionParser;
        this.modules = new LinkedList<>();
    }

    public static List<CModule> searchFrom(String rootPath, FunctionParser functionParser) {
        CModuleSearcher CModuleSearcher = new CModuleSearcher(functionParser);
        return CModuleSearcher.search(rootPath);
    }

    private List<CModule> search(String rootPath) {
        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("directory path invalid error: " + rootPath);
        }
        searchDirectory(rootDir);
        return modules;
    }

    private void searchDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        List<CModule> cModules = parseModules(files);
        modules.addAll(cModules);

        for (File subdirectory : getSubdirectories(files)) {
            searchDirectory(subdirectory);
        }
    }

    private List<CModule> parseModules(File[] files) {
        return Arrays.stream(files)
            .filter(file -> file.isFile() && isCOrPCFile(file.getName()))
            .map(this::createModule)
            .collect(Collectors.toList());
    }

    private CModule createModule(File file) {
        String content = FileReader.readFileContent(file);
        List<Function> functions = functionParser.parse(content);
        return new CModule(file.getPath(), functions);
    }

    private boolean isCOrPCFile(String fileName) {
        return fileName.endsWith(C_EXTENSION) || fileName.endsWith(PC_EXTENSION);
    }

    private List<File> getSubdirectories(File[] files) {
        return Arrays.stream(files)
            .filter(File::isDirectory)
            .collect(Collectors.toList());
    }
}
