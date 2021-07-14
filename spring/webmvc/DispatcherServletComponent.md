# Spring web MVC#2 Spring MVC 구성요소

---

이 포스팅은 백기선님의 스프링MVC 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.whiteship.me/courses/) 를 참고해보시기 바랍니다

---

##DispatcherServlet 

스프링 MVC 의 핵심이라 할 수 있는 DispatcherServlet 이 사용하는 인터페이스들과 역할에 대해 확인해도록 하겠다.

![구성요소](https://github.com/JadenKim940105/TIL/blob/master/spring/webmvc/img/dispatcherservlet.png)
```text
1. MultiPartResolver 
   - 파일 업로드 요청 처리에 필요한 인터페이스 (MultiPartResolver 타입의 빈이 등록하면 DispatcherServlet 이 해당 빈을 사용하여 파일 업로드 요청을 처리)
   - HttpServletRequest 를 MultipartHttpServletRequest 로 변환해주어 요청이 담고 있는 File 을 꺼낼 수 있는 API 제공 
   - 스프링부트 사용시 기본적으로 StandardServletMultipartResolver 를 빈으로 등록해준다. 


2. LocalResolver 
   - 클라이언트의 위치(Locale) 정보를 파악하는 인터페이스 
   - 기본전략은 요청의 accept-language 를 보고 판단 (AcceptHeaderLocalResolver)
   
   
3. ThemeResovler  
   - 애플리케이션에 설정된 테마를 파악하고 변경할 수 있는 인터페이스  


4. HandlerMapping  
   - 요청을 처리할 핸들러를 찾는 인터페이스 
   - RequestMappingHandlerMapping (Annotation 정보를 바탕으로 핸들러를 찾아주는 구현체), BeanNameUrlHandlerMapping (Bean 이름 기반으로 핸들러를 찾아주는 구현체 ) 가 기본등록되어있다. 


5. HandlerAdaptor
   - HandlerMapping 이 찾아낸 핸들러를 처리하는 인터페이스  


6. HandlerExceptionResolver
   - 요청 처리 중에 발생한 에러를 처리하는 인터페이스
   - ExceptionHandlerExceptionResolver (@ExceptionHandler) 
   
   
7. RequestToViewNameTranslator
   - 핸들러에서 뷰 이름을 명시적으로 리턴하지 않은 경우, 요청을 기반으로 뷰 이름을 판단하는 인터페이스 
  
   
8. ViewResolver 
   - 뷰 이름에 해당하는 뷰를 찾아내는 인터페이스
   

9. FlashMapManger
   - FlashMap 인스턴스를 가져오고 저장하는 인터페이스 
   - FlashMap 은 주로 redirection 을 사용할 떄 요청 매개변수를 사용하지 않고 데이터를 전달하고 정리할때 사용한다.
```


### 동작원리 WRAP-UP
```text
Spring WEB MVC 를 사용한다는 것은 결국 DispatcherServlet 을 사용한다는 것.

DispatcherServlet 은 init 과정에서 특정 타입(MultiPartResolver, LocalResolver..) 의 빈을 찾아서 등록하는데 
별다른 빈을 주입하지 않은 경우 기본전략을 사용한다

스프링 부트를 사용하지 스프링 MVC
- 서블릿 컨테이너에 등록된 웹 어플리케이션에 DispatcherServlet 을 등록한다 
    * web.xml 에 서블릿 등록 (또는 WebApplicationInitializer 에 자바 코드로 등록)

스프링 부트를 사용하는 스프링 MVC
- 자바 어플리케이션에 내장 톰캣을 만들고 그 안에 DispatcherServlet 을 등록 
    * 스프링 부트 자동설정이 자동으로 해줌 
- 스프링 부트의 주관에 따라 여러 인터페이스 구현체를 빈으로 등록한다. 

```