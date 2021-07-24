# JPA#7 프록시

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---

### 들어가기전.. 
Member 가 Team 을 참조하고 있다고 가정한 상태에서, Member 를 조회할 때 Team 도 함께 조회해야할까?  
1. 회원의 이름과 소속팀의 이름을 묻는다면? => 당연히 Team 도 조회해야한다.
2. 회원의 이름만 묻는다면? => Team 을 조회할 필요가 없다. 

경우에 따라 한번에 조회를 하는 경우가 최적의 선택일 수도, 한번에 조회하지 않는게 최적일 수도 있다.  



### 프록시 동작

![프록시](https://github.com/JadenKim940105/TIL/blob/master/jpa/img/proxy.png)

```text
프록시의 특징
- 프록시 객체는 처음 사용할 때 한번만 초기화
- 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님, 초기화 되면
  프록시 객체를 통해서 실제 엔티티에 접근 가능
- 프록시 객체는 원본 엔티티를 상속 받음, 따라서 타입 체크시 주의해야함(== 비교 실패, 대신 instance of 사용)
- 영속성 컨텍스트에 실제 엔티티가 이미 있으면, getReference()를 호출해도 실제 엔티티 반환 (반대로 프록시가 영속성 컨텍스트에 있으면 find 해도 프록시 반환) 
  => 같은 영속성 컨택스트 내에서 같은 데이터에 대한 == 은 보장되어야한다. 
- 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일때, 프록시를 초기화 하면 문제 발생 
  (하이버네이트의 경우 org.hibernate.LazyInitializationException 예외 발생) 
```

### 즉시로딩과 지연로딩 

#### 지연로딩
JPA 는 프록시를 활용한 지연로딩(LazyLoading)이 가능하다. 
```java
@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String username;
    
    @ManyToOne(fetch = FetchType.LAZY) // 지연로딩
    private Team team;
}
```
Member 엔티티를 다음과 같이 설정하고 Member 를 조회하면 team 은 프록시 객체를 가져오게 된다.
그리고 필요에 따라 team 의 필드값을 가져올 떄 DB에서 값을 가져와 프록시 객체를 초기화 해준다.

----

#### 즉시로딩

```java
@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String username;
    
    @ManyToOne(fetch = FetchType.EAGER) // 즉시로딩
    private Team team;
}
```
Member 엔티티를 다음과 같이 설정하고 Member 를 조회하면 team 은 Team 객체로 바로 가져온다.
  
  

### 주의사항(실무 활용)
- 가급적 지연로딩만 사용
- 즉시로딩을 적용하면 예상하지 못한 SQL이 발생
- 즉시로딩을 해도 JQPL 사용하면 N+1 문제 발생  
````java
Member 의 Team 이 eager 로 걸려있다고 가정. 
 em.createQuery("select m from Member m", Member.class)
=> JPA 는 JPQL 을 그대로 사용함으로 Member 를 그냥 가져옴 
그 이후에 eager 인 것을 확인하고 Team 을 조회해오는 방식으로 동작.
````
- @xxxToOne 의 기본전략은 EAGER 

### 영속성 전이(CASECADE)
```text
영속성 전이란 특정 엔티티를 영속상태로 만들 때 연관된 엔티티도 함께 영속상태로 만들고 싶을 때 사용 

영속성 전이는 연관관계를 매핑하는 것과는 아무런 관련이 없다. 
```

### 고아객체 제거(OrphanRemoval)
```text
부모 엔티티(참조하는 곳)와 연관관계가 끊겨진 자식 엔티티를 자동으로 삭제하는 기능

참조하는 곳이 하나일 떄만 사용
```
