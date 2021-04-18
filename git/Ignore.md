# Git#1 Ignore
git 을 사용해 버전관리를 할 때, 버전 관리 대상에서 제외하고 싶은 파일들을 설정해야하는 경우가 있다. 이를테면, DB 연결정보를 담고 있는 .properties (혹은 .yml) 파일같은 경우 보안상 버전관리 대상에서 제외해야한다.    
버전관리 대상에서 제외하고 싶은 파일들을 .gitignore 파일로 관리할 수 있으며 ignore 설정이된 파일들은 git add 명령에서 제외된다. 


## ignore 문법(glob 파일)
> 예시는 [이 곳](https://nesoy.github.io/articles/2017-01/Git-Ignore) 을 참조하였습니다.
```
# 주석 (ignore 규칙을 정의하는 줄에는 # 적용 X) 

# 파일명으로 ignore
file.txt 
# file.txt 에 해당하는 파일 ignore (모든 경로의 'file.txt' 파일이 제외된다)

# 확장자별로 ignore 
*.txt
# .txt 확장자를 갖는 모든 파일이 ignore 

# 디렉토리 전체 ignore
git/ 
# git 디렉토리 자체와, 디렉토리 내의 모든 내용을 ignore 

-------------- 예시 ------------ 

# ignore .a files
*.a

# but do track lib.a, even though you're ignoring .a files above
!lib.a

# only ignore the TODO file in the current directory, not subdir/TODO
/TODO

# ignore all files in the build/ directory
build/

# ignore doc/notes.txt, but not doc/server/arch.txt
doc/*.txt

# ignore all .pdf files in the doc/ directory
doc/**/*.pdf

```

## ignore 적용 

.gitignore 을 적용하려면 .gitignore 파일을 .git 으로 관리하면된다.   
이미 git 저장소에 커밋 되어있는 파일들들 ignore 하려면 해당 파일을 index 에서 제거하면 된다.
```
# 예시 
git rm -r --cached .   
git add .             
git commit -m "ignore 적용" 
```
