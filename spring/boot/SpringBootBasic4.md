# Spring Boot#4 독립적으로 실행 가능한 JAR

스프링 부트를 패키징하면 하나의 JAR 파일이 생기는 것을 확인 할 수 있다. 결과적으로 어플리케이션을 배포할 떄 해당 JAR 파일만 배포하면 
어플리케이션을 동작시킬 수 있다.  

## Spring Boot 배포 패키지 구조확인

1. 프로젝트를 mvn package 명령어를 통해 패키징한다. 
````text
mvn clean package
````

--> springboot-basic-0.0.1-SNAPSHOT.jar 가 생성된 것을 확인 할 수 있다.   


2. 패키징된 jar 파일을 unzip 한다
```text
 unzip -q springboot-basic-0.0.1-SNAPSHOT.jar 
```

아래와 같은 프로젝트 구조를 확인 할 수 있다.  
![프로젝트구조](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/jar%ED%8C%8C%EC%9D%BC%EA%B5%AC%EC%A1%B0.png)  
  
중요부분을 조금 살펴보자면  
- org.springframework.boot.loader 패키지에 로더관련 파일들이 들어간다.  
- BOOT-INF 안 classes 에 프로젝트를 위해 만든 클래스들이 들어간다. 
- BOOT-INF 안 lib 에 프로젝트를 위해 사용한 라이브러리들이 들어간다.  

![프로젝트구조](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/jar%ED%8C%8C%EC%9D%BC%EA%B5%AC%EC%A1%B02.png)  
 
스프링 부트는 내장 JAR 를 로딩하는 방법을 구현해둠으로써 손쉽게 실행 가능한 JAR 파일 하나로 프로젝트를 패키징 할 수 있게 된다.
- org.springframework.boot.loader.jar.JarFile 을 사용해 내장 JAR를 읽는다
- org.springframework.boot.loader.Launcher 를 사용해 실행한다. 

------
*참고: 이 포스팅은 백기선님의 스프링부트 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.whiteship.me/courses/)를 참고해보시기 바랍니다



 