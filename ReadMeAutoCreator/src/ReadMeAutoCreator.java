import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadMeAutoCreator {

    private final static String tilDirectoryPath = "/Users/jhkim/desktop/til";
    private final static ArrayList<String> studyPaths = new ArrayList<>();
    private final static ArrayList<String> studySubjects = new ArrayList<>();

    public static void main(String[] args){
        File file = new File("README.md");
        if(file.exists()){
            file.delete();
        }
        getStudyFilesSubject(tilDirectoryPath);
        createReadMeFile(studySubjects);
    }

    // md 파일 추적하여 README 파일 작성을 위한 데이터 수집 메서드
    private static void getStudyFilesSubject(String dirPath)  {

        File dir = new File(dirPath);
        File files[]  = dir.listFiles();

        for(int i = 0; i < files.length; i++){
            File file = files[i];
            if(file.isDirectory()){         // 파일이 디렉토리라면
                getStudyFilesSubject(file.getPath());  // 파일이 디렉토리가 아닐때 까지(파일일 떄까지) 재귀태움
            } else if(!file.getName().startsWith("README") && file.getName().endsWith("md")){ // 파일인 경우에 README 파일이 아니면서 마크다운 파일이면
                try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                    studyPaths.add(file.getPath());     // 파일 경로 저장
                    studySubjects.add(br.readLine());   // 파일 첫번째 라인(제목) 저장 -> 이를 통해 README 작성할 것이므로
                }  catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }


    // README 파일 생성 메서드
    private static void createReadMeFile(ArrayList<String> studySubjects){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("README.md", true))) {
            bw.write("# TIL\n");
            bw.write("## Spring\n");
            bw.write("1. 스프링 배치  \n");
            for(int i = 0 ; i < studySubjects.size(); i++){
                writeTitleAndLink(bw, i, "Spring Batch");
            }

            bw.write("2. 스프링 웹 MVC  \n");
            for(int i = 0 ; i < studySubjects.size(); i++){
                writeTitleAndLink(bw, i, "Spring web MVC");
            }

            bw.write("3. 스프링 부트  \n");
            for(int i = 0 ; i < studySubjects.size(); i++){
                writeTitleAndLink(bw, i, "Spring Boot");
            }

            bw.write("## Kotlin\n");
            for(int i = 0 ; i < studySubjects.size(); i++){
                writeTitleAndLink(bw, i, "Kotlin");
            }

            bw.write("## ETC\n");
            bw.write("1. 깃  \n");
            for(int i = 0 ; i < studySubjects.size(); i++){
                writeTitleAndLink(bw, i, "Git");
            }
            bw.write("2. ETC  \n");
            for(int i = 0 ; i < studySubjects.size(); i++){
                writeTitleAndLink(bw, i, "etc.");
            }


        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
    *   TIL md title convention
    *   # 주제 & 부제
    *   ex) # Spring Batch(주제) 배치프로세싱?(부제)
    *   writeTitleAndLink(bw, i, 주제)
    */
    private static void writeTitleAndLink(BufferedWriter bw, int i, String subject) throws IOException {
        if(studySubjects.get(i).startsWith("# " + subject)){
            String[] split = studySubjects.get(i).split("# " + subject);
            bw.write("["+subject + " - " + split[1] + "](." + studyPaths.get(i).split(tilDirectoryPath)[1] + ")  \n");
        }
    }
}
