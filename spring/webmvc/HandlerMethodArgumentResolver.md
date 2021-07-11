# Spring web MVC#3 HandlerMethodArgumentResolver

Spring web MVC 를 사용 해왔다면 Handler 에서 @PathVariable, @ModelAttribute,
@RequestParam, @RequestBody 와 같은 어노테이션들을 통해 인자 값을 바인딩해서 받는 경험을 했을 것이다.
Primitive type 은 물론 Reference Type 에도 바인딩이 잘 되는 것을 경험해 보았을 텐데
**_HandlerMethodArgumentResolver_** 를 통해 처리가 되는 것이다.


## Custom HandlerMethodArgumentResolver


### #1 바인딩 받을 객체 생성
우선 바인딩하기 위한 객체를 생성해 보자. 간단하게 다음과 같이 만들어 주었다
```java
public class MemberRequest {

    private String name;
    private Integer age;

    public MemberRequest(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }
}
```

요청은 다음과 같이 날라온다고 가정하였다.  
GET /member/name/{name}/age/{age}

해당 요청을 위에 생성한 객체로 바인딩 하기 위해 HandlerMethodArgumentResolver 를 커스터마이징 해보자.


### #2 HandlerMethodArgumentResolver 커스텀

```java
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class MemberHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(MemberRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String requestUri = nativeRequest.getRequestURI();

        Map<String, String> parameters = getParameters(requestUri);

        return new MemberRequest(parameters.get("name"), Integer.valueOf(parameters.get("age")));

    }

    private Map<String, String> getParameters(String requestUri) {
        Map<String, String> params = new HashMap<>();
        
        requestUri = requestUri.replaceAll("/member/name/", "");
        requestUri = requestUri.replaceAll("/age/", "@");
        
        params.put("name", requestUri.split("@")[0]);
        params.put("age", requestUri.split("@")[1]);
        return params;
    }
}
```
커스텀한 HandlerMethodArgumentResolver 를 만들기 위해 HandlerMethodArgumentResolver 인터페이스를 구현하면 되는데,
오버라이딩 해야하는 2개의 메소드는 다음과 같다.

---
supportsParameter   
주어진 method parameter 가 이 resolver 에 의해 지원되는지에 대한 여부를 return 한다.

Whether the given method parameter is supported by this resolver.
Params:
parameter – the method parameter to check
Returns:
true if this resolver supports the supplied parameter; false otherwise

---

resolveArgument
method parameter 로 넘어온 값들을 주어진 요청의 인수 값으로 확인한다.

Resolves a method parameter into an argument value from a given request.

즉 해당 메소드에서 실질적으로 parameter 들을 받아 바인딩 처리를 하는 로직을 작성하면 된다.

---

### #3 커스텀한 HandlerMethodArgumentResolver 등록
생성한 resolver 를 사용하기 위해서는 등록해주어야 한다.
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MemberHandlerMethodArgumentResolver());
    }
}
```
### #4 사용
```java
@RestController
public class TestController {
    
    @GetMapping("member/name/{name}/age/{age}")
    public String getMember(MemberRequest memberRequest){
        String x = memberRequest.getName() + " " + memberRequest.getAge();
        return x;
    }

}
```








      