# Spring web MVC#1 DispatcherServlet

---

이 포스팅은 백기선님의 스프링MVC 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.whiteship.me/courses/) 를 참고해보시기 바랍니다

---

앞선 포스팅에서 ContextLoaderListener 를 사용하여 ServletContext 가 생성되는 시점에 
ApplicationContext 를 ServletContext 에 올리는 방법을 살펴보았다. 그렇다면 단순히 스프링 
컨테이너를 사용하는 것을 넘어서서 스프링의 DispatcherServlet 을 사용하는 방법을 알아보자. 


## DispatcherServlet 등록

앞선 포스팅의 말미에 DispatcherServlet 은 "ServletContext 에 올라가 있는 
ApplicationContext(그래서 Root 라고함) 를 상속하는
ApplicationContext 를 사용하게 된다." 라고 하였는데 이런 계층구조를 활용해보도록 하겠다. 

스프링이 제공하는 디스패처서블릿을 등록하고, 설정파일을 생성해 디스패처서블릿이 해당 설정파일에 
의존하는 ApplicationContext 를 사용할 수 있도록 한다. 
```xml 
  <servlet>
    <servlet-name>app</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
      <param-name>contextClass</param-name>
      <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
    </init-param>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>me.summerbell.WebConfig</param-value>
    </init-param>
  </servlet>
  
  <servlet-mapping>
    <servlet-name>app</servlet-name>
    <url-pattern>/app/*</url-pattern> <!-- /app/ 이하 모든요청은 app servlet 이 처리하게됨-->
  </servlet-mapping>
```

설정파일의 경우 useDefaultFilters 설정을 false 로 하여, web 관련(Controller)이 아닌 빈들은
스캔하지 않도록 하고 includeFilters 를 통해 Controller 관련 빈만 스캔하도록 하였다.
(기존의 ServletContext 에 올라간 ApplicationContext 의 설정 클래스에는 excludeFilters 를 통해 Controller 관련 빈 스캔 제외)
이외에도 패키지를 나누어 컴퍼넌트 스캔 범위의 특성 이용해 설정할 수 도 있을 것 같다.
```java
// 설정파일 
@Configuration
@ComponentScan(useDefaultFilters = false, includeFilters = @ComponentScan.Filter(Controller.class))
public class WebConfig {
}
```

이렇게 하면 DispatcherServlet 이 사용하는 ApplicationContext 는 기본적으로 Root(즉, ServletContext 에 올라간 ApplicationContext)
를 상속하여 사용하게되고 독자적인 웹 관련 빈들을 사용할 수 있게된다. 
다시말해 또 다른 DispatcherServlet 을 생성해서 사용하면 DispatcherServlet 들의 Controller 는
공유되지 않고 Root 의 Service 빈들은 공유할 수 있게 만들 수 있다. 


```xml
<!-- 
다만, 굳이 이렇게 구조를 사용할 필요는 없다. 그리고 실제로 많은 프로젝트에서 여러 DispatcherServlet 을 
사용하는 경우는 많지 않으므로, DispatcherServlet 의 ApplicationContext 만 생성해서 사용하여도
무관하다. ( 그 경우 DispatcherServlet 의 ApplicationContext 가 Root 가 된다. ) 

사실상 다음의 xml 설정으로 사용하면 된다.  
-->

<web-app>
  <display-name>Archetype Created Web Application</display-name>

  <servlet>
    <servlet-name>app</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
      <param-name>contextClass</param-name>
      <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
    </init-param>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>me.summerbell.WebConfig</param-value>
    </init-param>
  </servlet>

  <servlet-mapping>
    <servlet-name>app</servlet-name>
    <url-pattern>/app/*</url-pattern>
  </servlet-mapping>

</web-app>

```

## Spring Boot 와의 차이점

현재까지 알아본 방식은 Spring Boot 의 방식과는 많은 차이가 있다.
앞선 방식에서는 Servlet 컨테이너가 뜨고, 컨테이너 안에 스프링을(디스패처서블릿을) 얹는 방식이였다.
하지만 Spring Boot 를 사용하면 스프링 컨테이너가 먼저 뜨고 그 안에서 embedded tomcat 을 
사용하여 디스패처서블릿을 내장톰캣에 등록하여 사용하는 방식이다. 