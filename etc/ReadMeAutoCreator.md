# etc. README 자동생성

TIL 을 작성하다보니, README 를 지속적으로 수정해주어야 하는 불편함이 있었다. 이를 자동화 하는 간단한 프로그램을 작성하였다  
```java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadMeCreator {

    private final static String tilDirectoryPath = "[TIL 디렉토리명]";
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

    private static void getStudyFilesSubject(String dirPath)  {

        File dir = new File(dirPath);
        File files[]  = dir.listFiles();

        for(int i = 0; i < files.length; i++){
            File file = files[i];
            if(file.isDirectory()){
                getStudyFilesSubject(file.getPath());
            } else if(!file.getName().startsWith("README") && file.getName().endsWith("md")){
                try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                    studyPaths.add(file.getPath());
                    studySubjects.add(br.readLine());
                }  catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    // 주제 : 부제 ex) Spring Batch - 배치프로세싱?

    private static void createReadMeFile(ArrayList<String> studySubjects){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("README.md", true))) {
            bw.write("# TIL\n");
            bw.write("## Spring\n");
            bw.write("1. 스프링 배치  \n");
            for(int i = 0 ; i < studySubjects.size(); i++){
                writeTitleAndLink(bw, i, "Spring Batch");
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


        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static void writeTitleAndLink(BufferedWriter bw, int i, String subject) throws IOException {
        if(studySubjects.get(i).startsWith("# " + subject)){
            String[] split = studySubjects.get(i).split("# " + subject);
            bw.write("["+subject + " - " + split[1] + "](." + studyPaths.get(i).split(tilDirectoryPath)[1] + ")  \n");
        }
    }
}

```