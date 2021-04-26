# Spring Boot#7 Spring Boot 프로파일  
## 프로파일?  
프로파일은 **특정한 환경** 정도로 생각하면 될 것 같다. 경우에 따라서 특정 Bean 들이 등록이 되어야 하고, 또 등록되지 않아야 하는 경우 사용할 수 있는 기능이다.
  
예를들어, 다음과 같이 개발환경을 구성한다고 생각해보자 DEV(개발환경) / TEST(테스트환경)  

개발환경에만 필요한 빈들이 있고 반대로 테스트 환경에서만 필요한 빈들이 있을 수 있다. 이런 경우에 프로파일을 설정함으로써 특정 환경에 맞는 구성을 할 수 있게된다.


### 프로파일 설정 방법 
@Configuration 혹은 @Component 와 함께 @Profile("프로파일명") 을 설정하면 된다. 

(예시)
```java
@Configuration
@Profile("dev")
public class DevConfig {

    @Bean
    public String hello(){
       return "dev mode hello";
    }
}
------------------------

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public String hello(){
        return "test mode";
    }
}
```

위와 같이 프로파일이 설정되면, 해당 프로파일 상태로 구동할 때만 빈으로 등록되게 된다.   
즉, DefConfig 는 dev 모드로 구동할때만 빈으로 등록되고 TestConfig 는 test 모드로 구동할때만 빈으로 등록된다.


### 구동시, 특정 프로파일 선택하기
어떤 프로파일을 활성화 시킬지에 관한 설정은 어떻게 할 수 있을까? 

1. 간단하게 application.properties 에서 설정할 수 있다.
```java
// dev 모드로 구동.
spring.profiles.active=dev  
```
2. commandLine 을 통해 설정할 수 있다. 
````java
// test 모드로 구동
java -jar 생성한프로젝트.jar --spring.profiles.active=test 
````
3. IDE 를 활용
![프로파일설정](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/%ED%94%84%EB%A1%9C%ED%8C%8C%EC%9D%BC%EC%84%A4%EC%A0%95.png)
   

   



--------
*참고: 이 포스팅은 백기선님의 스프링부트 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.whiteship.me/courses/)를 참고해보시기 바랍니다   
*참고: [Spring Boot Docs #24 Externalized Configuration](https://docs.spring.io/spring-boot/docs/1.5.22.RELEASE/reference/html/boot-features-external-config.html)  
