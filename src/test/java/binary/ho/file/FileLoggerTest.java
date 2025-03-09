package binary.ho.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileLoggerTest {
    
    public static void main(String[] args) {
        try {
            // 테스트 로그 기록
            System.out.println("로그 기록 테스트 시작...");
            FileLogger.log("테스트 로그 메시지 1");
            FileLogger.log("테스트 로그 메시지 2", "추가 정보");
            FileLogger.log("오류 발생:", "NullPointerException", "at line 42");
            
            // 로그 파일 확인
            String logFileName = "log/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
            File logFile = new File(logFileName);
            
            if (logFile.exists()) {
                System.out.println("로그 파일이 생성되었습니다: " + logFile.getAbsolutePath());
                String content = Files.readString(Paths.get(logFileName));
                System.out.println("로그 파일 내용:");
                System.out.println(content);
            } else {
                System.out.println("로그 파일이 생성되지 않았습니다.");
            }
            
            System.out.println("로그 기록 테스트 완료.");
        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
