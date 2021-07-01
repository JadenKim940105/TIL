# 테코톡#1 리눅스 파일시스템

---

해당 포스팅은 우아한Tech의 테크토크 [리눅스 파일 시스템 -오렌지](https://www.youtube.com/watch?v=oeuVjeeoLSQ) 영상을 참고하여 재구성 하였습니다.

---

## File & FileSystem 
File : 데이터, 프로그램 등을 담는 그릇  (추상화된 정보 단위)
FileSystem : file 들을 관리하는 시스템 

파일 시스템은 파일들을 관리하기 위한 **_정보_** 가 필요하다.
그러한 정보들을 **_메타데이터(MetaData)_** 라고 한다.  
메타데이터의 예시로는 파일의 이름, 확장자, 권한 정보와 같은 데이터들이 있다. 

## Linux FileSystem
#### Linux Directory & Inode 
리눅스 파일시스템은 파일을 쉽게 관리하기 위해 **_디렉토리(Directory)_** 라는 개념을 사용한다.
(Windows 의 folder 과 유사한 개념) 
또한, 이런 디렉토리 역시 파일시스템 입장에선 파일과 동일하다. 즉, 디렉토리는 파일들을 가지고 있는 파일 이라고 생각하면 된다.  

디렉토리 구조는 Tree 구조 인데, Tree 구조란 최상위(root) 로부터 하위 디렉토리가 있고, 
하위 디렉토리들이 또 하위디렉토리를 가지는 방식이다.

![트리구조](https://github.com/JadenKim940105/TIL/blob/master/techtalk/img/linuxDirectoryTree.png)

**_DirectoryEntry_** 란 디렉토리 표현하는 자료구조이다.  
_FileName + Inode 를 가르키는 포인터_ 로 구성되어 있다. 

**_Inode_** 란 파일 _이름을 제외한 파일의 메타데이터_ 를 의미한다. 

즉, DirectoryEntry 는 파일이름을 기준으로 파일정보에 대한 위치정보를 가지고 파일정보에 쉽게 접근할 수 있도록 도와주는 
자료구조인 것이다.
#### Link
링크에는 **_SoftLink(SymbolicLin) 와 HardLink_** 가 있다.  
SoftLink 는 파일의 경로만 복사한다. 반면에 HardLink 는 원본 파일을 그대로 복사하고 이름(경로) 만 다르다.

```
SoftLink
ln -s [원본 파일] [대상 파일(디렉토리)]

HardLink
ln [원본 파일] [대상 파일]
(softlink 와 달리 디렉토리를 향한 하드링크는 불가한데, 순환참조를 방지하기 위함이다.) 
```
#### Permission
파일 접근 권한 : 리눅스의 경우 사용자를 3 가지 타입으로 분류한다.  
- user - 소유자
- group - 소유자가 속한 그룹
- others - 그 이외

접근권한은 읽기(Read), 쓰기(Write), 실행(Execute) 가 있다. 
chmod 명령으로 접근 권한 변경이 가능하다.
```
chmod 675 temp 
chmod u+x,g=rw,o-x temp
(+ 는 해당 권한 추가, = 는 해당권할 할당, - 는 해당 권한 삭제) 
```
#### Mount
하드디스크, USB 등의 2차 저장장치 처럼 각각 그 자체로 루트가 있는 하나의 파일 시스템을  
'하나의 파일시스템' 으로 관리하는 방법이다. 

mount [장치명] [마운트 포인트] 명령어로 마운트를 할 수 있다.  
 
#### File Allocation
디스크에 파일을 효율적으로 저장하기 위해 공간을 관리하여야 한다.   
파일 할당 방법에는 연속, 연결, 색인 할당 방법등이 있는데, 리눅스는 **_색인 할당(Indexed Allocation)_** 을 사용한다.

- 연속할당은 말 그대로 파일 데이터를 연속으로 할당한다. 이 경우, 파일의 데이터가 늘어나거나 삭제됐을 때 추가적인 작업이 필요하다.  
- 연결할당은 LinkedList 를 떠올리면 된다.
- 색인할당은 데이터들이 저장된 위치(index)들을 저장한 블록을 사용한다. 

![색인할당](https://github.com/JadenKim940105/TIL/blob/master/techtalk/img/indexedallocation.png)

#### EXT (확장파일 시스템)
리눅스의 파일시스템에도 다양한 종류가 있는데 일반적으로 EXT3, EXT4 를 많이 사용한다.  
왜냐하면 **_저널링_** 을 지원하기 떄문인데 이를통해 캐시된 데이터와 원본 데이터간 싱크를 효율적으로 맞춰준다. 

