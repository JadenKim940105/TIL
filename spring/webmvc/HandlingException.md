# Spring web MVC#3 익셉션 핸들링

스프링 부트는 여러가지 Exception 을 제공하고, 이런 Exception이 발생했을 때 핸들링 하게 되어있다.
하지만 필요에 의해 Exception 을 만들어서 사용하는 경우도 있다.   
이번엔 커스텀한 익셉션을 만들고, 또 이 커스텀한 익셉션을 처리하는 핸들러를 생성해 보도록 한다. 

----

우선, 익셉션을 발생시킬 요청을 받을 Controller 를 하나 생성하도록 하겠다
```java
@RestController
public class HomeController {
    
    @GetMapping("exception")
    public String getException(){
        // todo throw RequestException
    }
    
}
```

다음으로, 커스텀한 익셉션을 만들어 보도록 하자 
커스텀한 익셉션을 만드는 방법은 간단하다. 
Exception 을 상속하는 클래스를 생성하면 되는데 
RuntimeException 을 상속하는 클래스를 생성하겠다.   
```java
public class ApiRequestException extends RuntimeException{

    public ApiRequestException(String message) {
        super(message);
    }

    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

Exception 이 발생했을 때 payload 와 에러 details 를 담을 클래스를 생성하자.
````java
public class ApiException {
    private final String message;
    private final HttpStatus httpStatus;
    private final LocalDateTime timestamp;

    public ApiException(String message, HttpStatus httpStatus, LocalDateTime timestamp) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

````

다음으로, ApiRequestException 이 발생했을 때 이를 핸들링 할 핸들러를 생성해보자
```java

@ControllerAdvice // 어플리케이션 전반의 컨트롤러 AOP 
public class ApiRequestExceptionHandler {

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleRequestException(ApiRequestException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ApiException exception = new ApiException(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

        return new ResponseEntity<>(exception, badRequest);
    }

}
```

마지막으로 컨트롤러에서 익셉션을 던지면,
```java
@RestController
public class HomeController {
    
    @GetMapping("exception")
    public String getException(){
        throw new ApiRequestException("error!!");
    }
    
}
```
다음과 같이 잘 처리된 것을 확인할 수 있다. 
```text
{
  "message": "error!!",
  "httpStatus": "BAD_REQUEST",
  "timestamp": "2021-07-03T04:06:31.969306"
}
```


----
참고자료 - https://jeong-pro.tistory.com/195


    