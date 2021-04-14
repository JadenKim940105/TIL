# Spring Boot 시작하기

## Spring Boot 는 의존성을 어떻게 가져오는가? (Maven 기준)  
![의존성상속구조](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/%EC%9D%98%EC%A1%B4%EC%84%B1%EC%83%81%EC%86%8D%EA%B5%AC%EC%A1%B0.png)  
스프링 부트 프로젝트를 생성하면 해당 프로젝트는  **spring-boot-dependencies** 를 상속한 
**spring-boot-starter-parent** 를 상속하여 만들어진다.
spring-boot-dependencies 의 dependencyManagement 에 어떤 버전을 사용할지에 대해 정의되어있는 것을 살펴볼 수 있다.  
이렇게 계층형 구조로 버전을 관리함으로써 직접 관리해야할 의존성의 수가 줄어들게 된다.  
- 만약 spring-boot-dependencies 가 제공하는 버전을 사용하고 싶지 않다면?
  
  => 직접 버전을 명시해주면 된다. (Overriding 됨)  
    

- 기본적으로 Parent POM 을 사용해 의존성을 들고오지만, 현재 프로젝트의 Parent POM을 spring-boot-starter-parent 가 아닌 다른 Parent POM 을 사용해야하는 경우?
   
  => 1. 다른 Parent POM 의 parent POM 을 spring-boot-starter-parent 로 한다.   
  => 2. Parent POM 을 활용하지 않고 dependencyManagement 만을 활용해 버전을 들고온다 
  (parentPOM 은 버전관리 외에도 컴팡일러 레벨설정, 각종 플러그인 설정 등등이 정의되어있고 dependencyManagement 만 활용한다면 당연히 그런 설정들은 자동설정되지 않는다.)
  
  
## Spring Boot 는 어떻게 자동설정을 지원하는가? 
@SpringBootApplication 어노테이션을 타고들어가면 다음 3개의 어노테이션이 포함되어 있는 것을 확인할 수 있다.
![@SpringBootApplication](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/SpringBootApplication%EC%96%B4%EB%85%B8%ED%85%8C%EC%9D%B4%EC%85%98.png)  
1. @SpringBootConfiguration (== @Configuration)
2. @ComponentScan  
   - 스캔범위내의 @Component 들을 빈으로 등록 (@Configuration, @Repository, @Service, @Controller, @RestController)  
   - 스캔범위는 기본적으로 @ComponentScan 이 붙은 클래스의 패키지 + 그 이하 패키지이다.   
   커스텀하고 싶다면 basePackageClasses 를 활용. 
3. @EnableAutoConfiguration  
   -> spring-boot-autoconfigure 프로젝트내의 META-INF 안 spring.factories 파일을 참조하여 @Configuration 들을 읽어들인다  
   - 단, 모든 @Configuration 들이 전부 등록되는 것이 아니라 @ConditionalOn 조건에 따라 빈을 등록할지 말지 결정된다.

스프링 부트로 프로젝트를 생성하고 구동시키면 @ComponentScan 을 통해 빈을 등록하고, @EnableAutoConfiguration 으로 읽어온 빈을 등록하게 된다.

> @ComponentScan 을 통해 읽어온 빈(Bean) 등록 -> @EnableAutoConfiguration 을 통해 읽어온 빈(Bean) 등록 
