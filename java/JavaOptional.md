# Java#2 Optional

---

해당 포스팅은 인프런 백기선님의 [더 자바, Java 8](https://www.whiteship.me/courses/) 강좌를 참고하여 재구성 하였습니다.

---

[Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) 
은 Java 8 에 추가된 새로운 Interface 이다  
Optional 이전에는 필요에 따라 if 문을 활용하여 null check 를 해주어야 했다. 
혹은 null을 리턴하지 않고 에러를 throw 하여 처리해야 하는데 이 경우 statck trace  가 남는다. 
그리고 그 만큼 리소스가 낭비되게 된다. 

Java 8 에 Optional 이 등장하여, 비어있는 값이 전달될 수 있는 경우 Optional 에 감싸서 전달할 수 있다.
다만, **return 값으로만 쓰기가 권장된다.**

### Optional 주의사항 
* 리턴값으로만 쓰기를 권장한다. (메소드 매개변수 타입, 맵의 키 타입, 인스턴스 필드 타입으로 쓰지말 것)
* Optinal을 리턴하는 메소드에서 null을 리턴하지말 것 (Optional 의 의미가 무색해진다.)
* Primitive 타입용 Optional 이 따로 있다. (OptionalInt, OptionalLong ..)


### Optional API 사용예시 
* Optional 에 값이 있는지 없는지 확인
    * isPresent()
    * isEmpty()
````java
 List<OnlineClass> springClasses = new ArrayList<>();
        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(3, "rest api development", false));

        Optional<OnlineClass> spring = springClasses.stream().filter(oc -> oc.getTitle().startsWith("spring")).findFirst();
        Optional<OnlineClass> jpa = springClasses.stream().filter(oc -> oc.getTitle().startsWith("jpa")).findFirst();

        System.out.println(spring.isPresent()); // true 
        System.out.println(jpa.isEmpty());      // true 
````
* Optional 에서 값 가져오기
  * get()
````java
   public static void main(String[] args) {
        List<OnlineClass> springClasses = new ArrayList<>();
        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(3, "rest api development", false));

        Optional<OnlineClass> spring = springClasses.stream().filter(oc -> oc.getTitle().startsWith("spring")).findFirst();
        Optional<OnlineClass> jpa = springClasses.stream().filter(oc -> oc.getTitle().startsWith("jpa")).findFirst();

        System.out.println(spring.get().getTitle());   // spring boot
        System.out.println(jpa.get().getTitle());      // NoSuchElementException 
    }
````
* Optional API 활용
  * ifPresent() : 있는 경우 수행 
  ```java
  // spring 에 값이 있는 경우 title 을 찍어라
  spring.ifPresent(oc -> System.out.println(oc.getTitle()));
  ``` 
  * orElse() : 있으면 가져오고 없다면 수행해라 
  ```java
  // jpa 가 있다면 반환하고 없다면 createNewClass() 수행결과 반환 
  OnlineClass onlineClass = jpa.orElse(createNewClasss());
  // 단, createNewClass() 는 jpa에 값이 있더라도 수행은 된다. 
  ```
  * orElseGet() : 있으면 가져오고 없다면 가져와라
  ```java
  // jpa 가 있다면 가져오고 없다면 createNewClass() 수행
  OnlineClass onlineClass = jpa.orElseGet(() -> createNewClasss());
  // orElse()와 다르게 없는경우에만 수행 
  ```
  * orElseThroe() : 있으면 가져오고 없으면 Error throw 
  ```java
  OnlineClass onlineClass = jpa.orElseThrow(IllegalArgumentException::new);
  ``` 
  * filter() : filter 한 결과가 있으면 optional 반환, 없으면 비어있는 optional 
  ```java
  Optional<OnlineClass> onlineClass = jpa.filter(OnlineClass::isClosed);
  System.out.println(onlineClass.isEmpty());     // true
  onlineClass = spring.filter(OnlineClass::isClosed);
  System.out.println(onlineClass.isPresent());   // true
  ```
  




