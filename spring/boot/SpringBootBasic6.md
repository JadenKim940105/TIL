# Spring Boot#6 Spring Boot 외부설정

## 외부설정파일? 
어플리케이션에서 사용하는 설정 값들을 세팅하는 파일로 대표적인 예시로 스프링에서의 application.properties 파일이 있다.  
해당 파일에 key - value 형태로 값을 정의하면 어플리케이션에서 해당 값을 참조해서 사용할 수 있다. 
> 참조는 어떻게?  
> 여러 방법이 있다. 가장 쉬운 방법은 @Value("$[키값}") 을 사용해 참조할 수 있다. 

````java
application.properties 파일에 다음과 같이 key - value 설정
test.property = test123        
-----------
    @Value("${test.property}")
    String test; // -> test123 
````

### 프로퍼티 우선순위 
앞서 살펴본 application.properties 파일을 사용하는 방법 이외에도 다양한 방법으로 어플리케이션이 사용할 프로퍼티를 제공할 수 있다.
만약 동일한 key 로 값을 제공하게 되면 어플리케이션은 어떤 값을 참조하게 될까? 당연히 우선순위가 높은 쪽을 택하게 된다.  
우선순위를 살펴보자면 다음과 같다.  
![우선순위](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/%EC%9A%B0%EC%84%A0%EC%88%9C%EC%9C%84.png)    
[참조](https://docs.spring.io/spring-boot/docs/1.5.22.RELEASE/reference/html/boot-features-external-config.html)   
> java/resources application.properties [vs] test/resources application.properties  
> 테스트 코드에서는 어떤 application.properties 를 사용하게될까?  
> test/resources application.properties 를 사용하게된다. 테스트 코드를 실행할 때 빌드되는 과정을 생각해보면 우선 src/main 내의 파일들이 클래스패스에 들어간다. 
> 그 다음 src/test 안의 파일들이 빌드되면서 application.properties 파일이 교체된다.  
>
>   
> src/main 의 application.properties 파일을 사용하지만 몇몇 프로퍼티 변경해 테스트용 으로 사용하고싶다면?
> 1. src/test/resources 에  application.properties 가 아닌 다른 이름의 properties 파일 생성 (ex: test.properties)  
> 2. @TestPropertySource(locations = "classpath:/test.properties")


## 프로퍼티 참조 방법 
1. @Value("${키값}")  
@Value 를 활용하여 참조할 수 있다. 

  
   
2. Environment  
스프링이 주입해주는  org.springframework.core.env.Environment 를 사용해서 참조할 수 있다. 
   

## @ConfigurationProperties
여러 프로퍼티를 묶어서 읽어올 수 있다.  

우선, 해당 어노테이션을 활용하려면 어노테이션을 기반으로 메타정보를 생성해주는 의존성을 추가해준다.
```java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
```

```java
application.properties 파일
        jaden.name = jaden
        jaden.age = 28
        jaden.nickname = jaden94
-------------

@Component // 빈으로 등록해야 사용가능 
@ConfigurationProperties("jaden")
public class JadenProperties {
    String name;
    int age;
    String nickname;
    .
    .
    .
    getter & setter 
}
```
> @Validated 역시 지원된다. 


--------
*참고: 이 포스팅은 백기선님의 스프링부트 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.whiteship.me/courses/)를 참고해보시기 바랍니다  
*참고: [Spring Boot Docs #24 Externalized Configuration](https://docs.spring.io/spring-boot/docs/1.5.22.RELEASE/reference/html/boot-features-external-config.html)




