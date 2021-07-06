# Spring web MVC#0 Servlet 

---

이 포스팅은 백기선님의 스프링MVC 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.whiteship.me/courses/) 를 참고해보시기 바랍니다

---

## Web flux & Web MVC
Spring boot starter 중 웹과 관련된 스타터는 flux 와 mvc 가 있는 것을 알 수 있다.
그 중 Web MVC 는 servlet 기반의 웹 개발을 지원한다. 따라서 spring web mvc 를 잘 이해하기 위해서는 
servlet 에 대한 전반적인 지식이 필요하다. 

## Servlet 
서블릿은 요청이 오면 Thread 를 생성(일반적으로 Thread Pool 을 이용해 Pool 에서 가져오는 방식)하여 
요청을 처리하는 방식으로 동작한다.  
Servlet은 이전의 CGI 기술이 요청 당 프로세스를 생성하는 방식 이였던 것에 비해 많은 이점(속도, 보안, 이식성)들이 있어
웹 어플리케이션을 개발하는데 가장 많이 사용되는 기술 중 하나이다.

서블릿 어플리케이션은 서블릿 엔진(컨테이너) 위에서 동작하게 되는데 대표적인 예로 tomcat, jetty 와 같은
것 들이 있다. 
서블릿 컨테이너는 세션 관리, MIME 기반 메시지 인코딩-디코딩, 서블릿 생명주기 관리 같은 일을 해준다.


## Servlet Life Cycle
서블릿의 라이프 사이클은 크게 init(), service(), destroy() 로 구분할 수 있는데  
  
init() 은 어플리케이션이 구동 된 후 최초로 요청이 왔을 때 초기화 작업이 일어나는 구간이다.  
service() 는 init() 된 서블릿으로 요청이 다시 올 경우 처리하게 된다.  
destroy() 는 서블릿 컨테이너의 판단에 따라 서블릿을 메모리에서 내려야 할 시점에 동작하여 서블릿을 내려준다.


## 기존의 Servlet 프로그래밍
서블릿 어플리케이션을 만들 때 기존에는 HttpSevlet 을 상속하는 서블릿을 만들고,
web.xml 에 등록해두는 방식으로 사용했다. 
```java
// 서블릿 
public class HelloServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        System.out.println("init");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Hello");
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }
}

```
```xml
<!-- web.xml 에 servlet 등록 -->
<servlet>
<servlet-name>hello</servlet-name>
<servlet-class>me.summerbell.HelloServlet</servlet-class>
</servlet>

<servlet-mapping>
<servlet-name>hello</servlet-name>
<url-pattern>/hello</url-pattern>
</servlet-mapping>
```

## Servlet Listener & Servlet Filter 
서블릿 리스너는 웹 어플리케이션에서 발생하는 주요 이벤트들을 감지하고 각 이벤에 작업이 필요한 경우
사용할 수 있다.  
어떤 이벤트들에 관한 리스너가 있는지는 [이 블로그](https://sbsjavaprg.wordpress.com/2014/02/18/%EC%84%9C%EB%B8%94%EB%A6%BF-%EB%A6%AC%EC%8A%A4%EB%84%88listener-%EC%84%A4%EB%AA%85-%EB%B0%8F-%EC%82%AC%EC%9A%A9%EB%B0%A9%EB%B2%95/) 를 참고하면 될 것 같다.

````java
// ServletContextListener 리스너 (ServletContext 가 Initialize, Destroy 되는 이벤트) 
public class SampleListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ServletContext 초기화 감지");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ServletContext Destroy 감지");
    }
}
````
```xml
<!-- web.xml 에 리스너 등록 -->
<listener>
    <listener-class>me.summerbell.SampleListener</listener-class>
  </listener>
```


서블릿 필터의 경우 요청이 들어와 Servlet 의 doGet() 이 호출되기 전, 후에 특정 작업을 하고 싶을 때
사용할 수 있다. 특정 서블릿(들)이나 특정 URL 에 요청이 들어올 경우 필터처리를 할 수 있으며, 여러개의
필터가 이어진 체인형식으로 설계할 수 도 있다. 

```java
// 필터생성
public class SampleFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Filter Init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("SAMPLE FILTER");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        System.out.println("Filter Destroy");
    }
}
```

```xml
<!-- web.xml 에 필터 등록 -->
<filter>
    <filter-name>sample</filter-name>
    <filter-class>me.summerbell.SampleFilter</filter-class>
</filter>

<filter-mapping>
<filter-name>sample</filter-name>
<servlet-name>hello</servlet-name>
</filter-mapping>

```

## 서블릿 어플리케이션과 스프링 연동하기 

지금까지 만든 서블릿 어플리케이션에 스프링을 연동하여 Ioc 컨테이너를 활용하고 싶다면 
스프링이 제공하는 ContextLoaderListener 라는 리스너를 추가해주어야 한다.
해당 리스너는 IoC컨테이너(ApplicationContext) 를 서블리어플리케이션의 
생명주기에 맞춰서 바인딩해주는 역할을 한다.  
즉, 서블릿들이 ApplicationContext 를 사용할 수 있게 ApplicationContext 를 만들어 
ServletContext 에 등록해준다. 

ApplicationContext 를 생성할 때는 스프링 설정파일을 참조하여 생성하게 되는데 
기본적으로는 xml 설정파일을 참조하게 되어있다. 이를 자바 설정파일로 변경해보도록 하자

```xml
<!-- context-param 은 최상단에 위치시키자 --> 
    <!--  AnnotationConfigWebApplicationContext (Annotation 기반의 ApplicationContext)-->
    <context-param>
        <param-name>contextClass</param-name>
        <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
    </context-param>
    
    <!-- 설정파일 지정 -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>me.summerbell.AppConfig</param-value>
    </context-param>    


    <!-- ContextLoaderListener 추가 -->
    <listener>
      <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
```


---
지금까지 서블릿과 서블릿에 스프링을 연동시키는 작업을 해보았다. 그렇다고 해서 현재 spring mvc를 사용하고 있는 것은 아니다.
spring mvc 를 사용한다 함은 spring 이 제공하는 front controller 역할을 하는 dispatcherServlet 을
사용한다는 말과 동일하다. 다음 포스팅에서는 스프링이 제공하는 dispatcherServlet 에 대해 알아보도록 하자.


추가적으로 DispatcherServlet 은 ServletContext 에 올라가 있는 ApplicationContext(그래서 Root 라고함) 를 상속하는 
ApplicationContext 를 사용하게 된다. 이런 상속 구조를 통해서 DispatcherServlet 간 독자적인 ApplicationContext
를 사용할 수 있게끔 설계되어 있지만... 현재 대부분의 프로젝트를 보면 이런 상속 구조를 사용하지 않고, 즉 Root 를 따로 만들지 않고 사용하는 경우가 많다.  








