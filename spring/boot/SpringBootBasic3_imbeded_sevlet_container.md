# Spring Boot 내장 서블릿 컨테이너

### 사전지식 
Spring boot 를 사용하면 기본적으로 내장톰캣(Embedded Tomcat) 을 사용하게 된다. 내장톰캣이 어떻게 구현되는 것인지 알아보기 전에 자바코드를 통해 톰캣을 구현해보겠다.
```java
Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context context = tomcat.addContext("/", "/");

        HttpServlet helloServlet = new HttpServlet() {
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        writer.println("<html><head><title>");
        writer.println("Tomcat");
        writer.println("</title></head>");
        writer.println("<body><h1>Hello My Tomcat</h1></body");
        writer.println("</html>");
        }
        };
        String helloServletName = "helloServlet";

        tomcat.addServlet("/", helloServletName, helloServlet);

        context.addServletMappingDecoded("/hello", helloServletName);
        tomcat.getConnector();
        tomcat.start();
        tomcat.getServer().await();
```
위와 같은 방법으로 자바코드로 톰캣을 생성해 사용할 수 있는 것을 확인할 수 있다. netty, undertow 도 마찬가지로 
자바코드로 생성해 사용할 수 있다. 

## ServletWebServerFactoryAutoConfiguration
서블릿 웹서버를 자동설정하는 Configuration 클래스이다.
내장톰캣은 자동설정을 사용해 생성되어 추가된다는 것을 확인할 수 있다. 

## DispatcherServletAutoConfiguration
디스패처서블릿을 자동설정하는 Configuration 클래스이다. 
Spring MVC 의 핵심이라 할 수 있는 디스패처서블릿을 만들고, 서블릿 컨테이너에 등록하게 된다.  

> ServletWebServerFactoryAutoConfiguration 와 DispatcherServletAutoConfiguration 를 분리한 이유?  
> 생성한 서블릿이 서블릿 컨테이너에 종속될 필요가 없음으로, 서블릿을 생성하고 등록하는 과정과 서블릿 컨테이너를 생성하는 과정을 분리하는 것이 
> 한층 더 유연한 설계이기 때문이다.

## 다른 서블릿 컨테이너로 변경
ServletWebServerFactoryAutoConfiguration 와 DispatcherServletAutoConfiguration 분리함으로써 다양한 서블릿컨테이너를 사용할 수 있겠구나 라고 짐작할 수 있었다.
그렇다면 다른 서블릿 컨테이너로 변경은 어떻게 할 수 있을까?   
![톰캣의존성](https://github.com/JadenKim940105/TIL-images/blob/master/img/spring/boot/%EB%82%B4%EC%9E%A5%ED%86%B0%EC%BA%A31.png)    
1. 위의 사진에서 알 수 있듯이 기본적으로 spring-boot-starter-web 은 spring-boot-starter-tomcat 을 가져온다.  
따라서 이 의존성을 빼준다. 
````java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
````
2. 사용할 다른 서블릿 컨테이너 의존성을 추가해준다. 

````java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jetty</artifactId>
        </dependency>
````

로그를 살펴보면 tomcat 이 아닌 jetty 가 실행되는 모습을 확인할 수 있다.  
````java
2021-04-17 21:53:35.203 Jetty started on port(s) 8080 (http/1.1) with context path '/'
````

## 웹 서버를 사용하기 싫다면? 
다양한 방법이 있겠지만 가장 간단한 방법은 application.properties 에서 설정을 변경해주는 방법이 있다. 
```java
spring.main.web-application-type=none
```

