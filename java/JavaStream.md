# Java#1 Stream 

---

해당 포스팅은 인프런 백기선님의 [더 자바, Java 8](https://www.whiteship.me/courses/) 강좌를 참고하여 재구성 하였습니다. 

---

Java의 [Stream](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html) 
은 List 와 같은 Collection 데이터, 즉 연속된 데이터를 처리하는 Opertaion 의 모음이라 생각하면 된다.  
다시말해, **Stream 은 연속된 데이터를 소스로 사용해서 어떠한 처리를 하는 작업을 위한 API 이다.**

### Stream 의 특징 및 주의 사항
* Stream 은 데이터를 담는 저장소가 아니다.
* Functional in nature, 스트림이 처리하는 데이터 소스를 변경하지 않는다. 
````java
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("jaden");
        names.add("messi");

        Stream<String> stringStream = names.stream().map(String::toUpperCase);
        stringStream.forEach(System.out::println);
        System.out.println("===========");
        names.forEach(System.out::println);
    }
    ----------- consol
        JADEN
        MESSI
        ===========
        jaden
        messi
````
- 스트림으로 처리하는 데이터는 오직 한번만 처리한다. 
- 무제한일 수도 있다.(실시간으로 계속해서 stream 으로 데이터를 받아 처리가능) (+ Short Circuit 메소드를 사용하여 제한 가능하다)
- 중계 오퍼레이션은 근본적으로 lazy 하다.
- 손쉽게 병렬 처리할 수 있다.   
  -> 일반적으로 반복문을 돌릴경우 반복이 종료될 때 까지 병렬처리가 힘들다. 그에 반해 stream 은 parallelStream() 을 사용하면 JVM 이 stream을 쪼개서 병렬적으로 처리해준다. 
  
### Stream 파이프라인
0 개 이상의 중계 오퍼레이션(intermediate operation)과 한계의 종료 오퍼레이션(terminal operation) 으로 구성한다.  
**스트림의 데이터 소스는 오직 종료 오퍼레이션을 실행할 때에만 처리한다.**

### 중계 오퍼레이션 vs 종료 오퍼레이션
- 중계형 오퍼레이션은 return 이 Stream 이다.
- 종료형 오퍼레이션은 return 이 Stream 이 아니다. 
- 중계형 오퍼레이션은 종료형 오퍼레이션이 있어야 실행이 가능하다. 
````java
  public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("jaden");
        names.add("messi");

        // 종료형 오퍼레이션이 없고, 중계형 오퍼레이션만 실행한 경우 
        names.stream().map(s -> {
            System.out.println(s);
            return s.toUpperCase();
        });

        System.out.println("======");
        
        // 종료형 오퍼레이션이 있는 경우
        names.stream().map(s -> {
            System.out.println(s);
            return s.toUpperCase();
        }).collect(Collectors.toList());
    }

    ----------- consol

        ======
        jaden
        messi
````
- 중계 오퍼레이션 
    - filter
    - map
    - limit
    - skip
    - sorted
    - ...
- 종료 오퍼레이션
    - collect
    - allMatch 
    - count
    - forEach
    - min
    - max
    - ... 

#### Stream 사용 예제 
```java
 public static void main(String[] args) {
        List<OnlineClass> springClasses = new ArrayList<>();
        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(2, "spring data jpa", true));
        springClasses.add(new OnlineClass(3, "spring mvc", false));
        springClasses.add(new OnlineClass(4, "spring core", false));
        springClasses.add(new OnlineClass(5, "rest api development", false));

        System.out.println("spring 으로 시작하는 수업");
        springClasses.stream().filter(c -> c.getTitle().startsWith("spring")).forEach(c -> System.out.println(c.getTitle()));

        System.out.println("\nclose 되지 않은 수업");
        springClasses.stream().filter(c -> !c.isClosed()).forEach(c -> System.out.println(c.getTitle()));
        // method reference 사용
        System.out.println("---");
        springClasses.stream().filter(Predicate.not(OnlineClass::isClosed)).forEach(c -> System.out.println(c.getTitle()));

        System.out.println("\n수업 이름만 모아서 스트림 만들기");
        Stream<String> titleStream = springClasses.stream().map(OnlineClass::getTitle);
        titleStream.forEach(System.out::println);

        System.out.println("\n스프링 수업 중에 제목에 spring 이 들어간 제목만 모아서 list 로 만들기");
        List<String> spring = springClasses.stream()
                .filter(c -> c.getTitle().contains("spring"))
                .map(OnlineClass::getTitle)
                .collect(Collectors.toList());

        spring.forEach(System.out::println);


        List<OnlineClass> javaClasses = new ArrayList<>();
        javaClasses.add(new OnlineClass(6, "The Java, Test", true));
        javaClasses.add(new OnlineClass(7, "The Java, Code manipulation", true));
        javaClasses.add(new OnlineClass(8, "The java, 8 to 11", false));

        List<List<OnlineClass>> jadenCourseList = new ArrayList<>();
        jadenCourseList.add(springClasses);
        jadenCourseList.add(javaClasses);

        System.out.println("\n두 수업 목록에 들어있는 모든 수업 아이디 출력");
        jadenCourseList.stream().flatMap(Collection::stream)
                .forEach(c -> System.out.println(c.getId()));


        System.out.println("\n10부터 1씩 증가하는 무제한 스트림 중에서 앞에 10개 빼고 최대 10개 까지만");
        Stream.iterate(10, i -> i+1)
                    .skip(10)
                    .limit(10)
                .forEach(System.out::println);

        System.out.println("\n자바 수업 중에 Test가 들어있는 수업이 있는지 확인");
        System.out.println(javaClasses.stream().anyMatch(c -> c.getTitle().contains("Test")));
    }
```





