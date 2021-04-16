# Spring Boot 자동설정 활용

### 직접 자동설정을 만들어 사용해보자
1. 자동생성을 담당할 프로젝트를 생성했다면, 가장 먼저 필요한 의존성들을 추가해 준다.

````java
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure-processor</artifactId>
        </dependency>
    </dependencies>
        
    // dependencyManagement 를 통해 버전관리  
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.4.4</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
````

2. 특정 빈을 자동설정하기 위해선 자동설정의 대상이 될 클래스가 필요함으로, 자동설정 대상이 될 클래스를 하나 만들어준다.
```java
public class DeveloperDescription {
    String name;
    String email;
    // getter, setter, toString
}

```
3. 자동설정을 담당할 자동설정파일(Configuration)을 생성한다. 
```java
@Configuration
public class DeveloperDescriptionConfiguration {

    @Bean
    public DeveloperDescription developerDescription(){
        DeveloperDescription developerDescription = new DeveloperDescription();
        developerDescription.setName("jaden");
        developerDescription.setEmail("jaden@gmail.com");
        return developerDescription;
    }

}
```
4. src/main/resource/META-INF 에 spring.factories 파일을 만들고 자동설정파일을 추가해준다.
   ![자동설정파일추가](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/autoconfigure/springfactories%EC%9E%90%EB%8F%99%EC%84%A4%EC%A0%95%ED%8C%8C%EC%9D%BC%EC%B6%94%EA%B0%80.png)  
      
5. 해당 프로젝트를 mvn install 하여 로컬 메이븐 저장소에 저장하고, 자동설정을 사용할 프로젝트에서 해당 프로젝트 의존성을 추가한다.

6. DeveloperDescription 빈이 자동설정 된 채 주입되어있는지 확인하면 정상적으로 주입된 것을 확인할 수 있다.

===> 여기까지만 설정을 하는 경우 발생하는 문제점이 있다. 만약, 자동설정을 사용하지 않고 직접 빈을 설정해 사용하고 싶어 직접 빈을 등록시키면 어떻게 될까?
앞서 설명했든 스프링 부트는 빈을 등록할때 **1.ComponentScan** 을 해서 빈을 등록하고 **2.EnableAutoConfiguration**을 통해 빈을 등록하게 된다.
따라서 직접 설정한 빈은 자동설정에 의해 overriding 되어 제대로 동작하지 않게된다.  
  
이를 해결하기 위해선 설정파일로 가서 @ConditionalOnMissingBean 을 추가하면 된다. 
```java
    @Bean
    @ConditionalOnMissingBean
    public DeveloperDescription developerDescription(){
        DeveloperDescription developerDescription = new DeveloperDescription();
        developerDescription.setName("jaden");
        developerDescription.setEmail("jaden@gmail.com");
        return developerDescription;
    }
```


7. application.properties (application.yml) 활용하여 자동설정을 지원하려면?
 - @ConfigurationProperties 을 사용하기 위해 의존성 추가 
```java
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-configuration-processor</artifactId>
<optional>true</optional>
</dependency>
```
 - @ConfigurationProperties 클래스 생성
```java

@ConfigurationProperties("developer")
public class DeveloperDescriptionProperties {

    private String name;

    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```
- 자동설정파일에 @EnableConfigurationProperties 추가, Properties 활용
```java
@Configuration
@EnableConfigurationProperties(DeveloperDescriptionProperties.class)
public class DeveloperDescriptionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DeveloperDescription developerDescription(DeveloperDescriptionProperties properties){
        DeveloperDescription developerDescription = new DeveloperDescription();
        if(properties.getName().equals("") || properties.getName() == null){
            developerDescription.setName("jaden");
        } else {
            developerDescription.setName(properties.getName());
        }
        if(properties.getEmail().equals("") || properties.getEmail() == null){
            developerDescription.setEmail("jaden@gmail.com");
        } else {
            developerDescription.setEmail(properties.getEmail());
        }
        return developerDescription;
    }

}
```

> 요약   
> 1. 자동설정을 담당할 @Configuration 클래스를 작성  
> 2. src/main/resource/META-INF 에 spring.factories 파일을 생성, 자동설정 클래스 지정  
> 
> TIP  
> * 조건에 따른 자동설정 사용여부를 지정할 수 있다. @ConditionalOnXxx
> * @ConfigurationProperties / @EnableConfigurationProperties 를 설정하면 application.properties 를 활용해 간단히 등록할 빈을 설정할 수 있다. 


>참고  
> https://javacan.tistory.com/entry/spring-boot-auto-configuration
> https://www.slideshare.net/sbcoba/2016-deep-dive-into-spring-boot-autoconfiguration-61584342
> 인프런 <스프링 부트 개념과 활용> - 백기선

