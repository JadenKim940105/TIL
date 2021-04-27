# Spring Boot#8 Spring Boot 로깅  

## 로깅 퍼사드(Facade)와 로거

대표적인 로깅 퍼사드는 Commons Logging, SLF4J 가 있다.  
로거에는 JUL, Log4j2, Logback 등이 있다.  

Commons Logging 의 경우 클래스로딩을 하는데 이슈가 발생한 적이 있다. 이로 인해 Commons Logging 을 기피하는 움직임이 있었고 구조적으로 더 간단하고 안전한 SLF4J 가 생겨난다.

다만, 스프링 코어 모듈 개발시점에 SLF4J 가 없었기 떄문에 Commons Logging 을 사용하였고 추후에 SLF4J를 사용하기 위해서 기존의 Commons Logging 을 exclusion 시키고 SLF4J 를 넣고 사용해야 했다.
  
스프링 5 부터는 Spring JCL 이라는 모듈이 추가되면서 Commons Logging 코드들이 컴파일 시점에 SLF4J 로 가게된다.   
(exclude 직접할 필요 X)

spring-boot-starter-web 의존성을 가져오면 추이적으로   
spring-boot-starter-logging 을 가져오는데 이때 사용하는 로거는 Logback 이다.  
만약 로거를 Log4j2 로 변경하고 싶다면,   
```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
``` 
  
위와 같이 의존성을 설정하여 변경할 수 있다. 

---- 
  

#### Facade?
Facade 란 '건물의 정면', '외관' 을 의미한다. 그리고 소프트웨어 공학에서 Facade Pattern 이란 서브 시스템들을 인터페이스로 감싸
간단하게 만드는 패턴을 의미한다.  
![퍼사드패턴](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/facade%ED%8C%A8%ED%84%B4.png)  
[이미지 참조](https://en.wikipedia.org/wiki/Facade_pattern)

SLF4J 에서 Facade 에 해당하는 객체는 Logger 이다. 단, Logger 는 구현체가 아니라 인터페이스이고 실제 Logger 구현체는 
Logback, Log4j2 ...등이 있고 의존성에 따라 사용하게 된다. 





--------
*참고: 이 포스팅은 백기선님의 스프링부트 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.whiteship.me/courses/)를 참고해보시기 바랍니다       
*참고: [Spring Boot Docs #24 Externalized Configuration](https://docs.spring.io/spring-boot/docs/1.5.22.RELEASE/reference/html/boot-features-external-config.html)  
