# Spring Boot#5 Spring Boot Features

### SpringApplication 
SpringApplication 클래스는 main() 메소드에서 시작되는 스프링 어플리케이션을 쉽게 bootstrap 할 수 있게 한다.
```java
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
```

### 로그레벨 변경
기본로그 레벨은 INFO 레벨로 설정되어 있는데, 로그레벨을 변경하고 싶다면

 - VM options (ex: -Ddebug) 
 - Program arguments (ex: --debug)
![로그레벨변경](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/%EB%A1%9C%EA%B7%B8%EB%A0%88%EB%B2%A8%EB%B3%80%EA%B2%BD.png)
를 활용하여 변경 할 수 있다. 
> debug 레벨 로그를 사용하면 자동설정이 적용됐는지, 안됐는지 안됐다면 왜 안됐는지에 대한 정보가 로그에 찍힌다. 

### 배너 설정
 - src/resources 에 banner.txt / banner.gif 와 같은 파일로 설정할 수 있다.  
    -> 다른 위치에 배너관련 파일을 설정하고 싶다면 application.properties 에서 spring.banner.location 값으로 설정가능   
   
  
 - 코드를 통한 배너 구현
```java
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBanner(new Banner() {
            @Override
            public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
                out.println("=====새로운 베너======");
            }
        });
        application.run(args);
    }

```
 - 배너 끄기   
 ```java
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
```


### Application Event 
스프링, 스프링 부트가 제공하는 여러 이벤트들이 있다. (여기서 이벤트라 함은 어플리케이션 구동과정에서의 특정 시점이라 생각하면 된다.)  
다양한 구동시점의 이벤트가 존재하지만 applicationContext 가 만들어지기 전/후 를 기준으로 이벤트 리스너를 설정하는 방법에 차이가 생긴다.
- applicationContext 가 만들어가진 이후에 발생하는 이벤트들의 이벤트리스너 구현시 **빈으로 등록하여 사용**
```java
@Component
public class SampleListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        System.out.println("====== ApplicationStartingEvent ======");
    }
}
```
- applicationContext 가 만들어 지기 전 발생하는 이벤트들의 이벤트리스너 구현시 **직접 등록**
```java
public class SampleListener implements ApplicationListener<ApplicationStartingEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartingEvent applicationStartingEvent) {
        System.out.println("===== ApplicationStartingEvent ======");
    }
}

-----------------

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new SampleListener()); // 직접 리스너 등록 
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
```
> 이벤트리스너를 등록하는 방법이 applicationContext 생성 시점을 기준으로 나뉘는 이유는 applicationContext 가 존재하지도 않는데 빈으로 등록할 수는 없으니 당연한 이유이다.

### ApplicationArguments
어플리케이션 아규먼트로 들어오는 인자들은 ApplicationArgument 로 받을 수 있다.
```java
 @Component
public class ArgumentTest {
    public ArgumentTest(ApplicationArguments arguments){
        System.out.println(arguments.getOptionNames());
    }
}
```

### ApplicationRunner 
어플리케이션이 실행된 뒤, 어떤 작업을 실행하고 싶을 떄 사용할 수 있다.
```java
@Component
public class SampleRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("뭔가 하고 싶은 작업");
    }
}
```


------
*참고: 이 포스팅은 백기선님의 스프링부트 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.whiteship.me/courses/)를 참고해보시기 바랍니다
*참고: [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
