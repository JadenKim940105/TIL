# JPA#6 상속관계 매핑

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---

### 상속관계 매핑
```text
RDB 에는 OOP 의 상속관계와 동일한 개념이 없다. 
대신 상속과 비슷한 기법으로 슈퍼타입-서브타입 이 있는데 이를 활용해서 JPA 는 상속관계를 매핑한다. 
```
슈퍼타입 - 서브타입 논리 모델을 물리적으로 구현하는 방법은 3가지가 있다.
1. 조인전략 
![조인전략](https://github.com/JadenKim940105/TIL/blob/master/jpa/img/join.png)  

- @Inheritance(strategy = InheritanceType.JOINED) 
- 부모 테이블과 자식 테이블을 생성해 사용하는 방식으로 select 시 join 을 사용해 데이터를 가져온다.
- @DiscriminatorColumn 을 통해 구분컬럼(DTYPE) 을 넣을 수 있다. // 운영상 항상 넣어주는 것이 좋다.
- 정규화된 테이블 사용가능
  
---   
  
2. 단일테이블 전략
![단일테이블전략](https://github.com/JadenKim940105/TIL/blob/master/jpa/img/onetable.png)  
   
- @Inheritance(strategy = InheritanceType.SINGLE) // default 전략, 생략가능
- 한 테이블에 부모와 모든 자식들의 필드를 컬럼으로 추가하고 DTYPE 으로 구분하는 방법
- 자신의 필드가 아닌 컬럼에는 null 이 들어감
- 한 테이블에서 관리됨으로 성능적으로 우수 
- @DiscriminatorColumn 을 생략해도 default 로 넣어줌 
  
----
   
3. 구현 클래스별 테이블 생성 
![테이블 분할](https://github.com/JadenKim940105/TIL/blob/master/jpa/img/eachtable.png)  
   
- @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) 
- 테이블로 구분되었음으로 @DiscriminatorColumn 는 사용하지 않는다.
- 상위타입을 활용해 데이터를 찾는 경우 union all 로 모든 자식테이블을 탐색 -> 최악의 성능..
- 실무에서 사용하지 않는다. 

-----

### @MappedSuperClass
```java
여러 테이블에서 공통으로 사용되는 속성들이 있을 때 사용할 수 있다.
ex) 다수의 테이블에 등록시간, 수정시간, 등록자, 수정자 정보가 필요한 경우

@MappedSuperclass
public abstract class BaseEntity {

    private String createdBy;

    private LocalDateTime createdDate;

    private String lastModifiedBy;

    private LocalDateTime lastModifiedDate;
}

// 

public class Member extends BaseEntity{
    //... Member 클래스..
}

public class Team extends BaseEntity{
    //... Team 클래스..
}
```
- @MappedSuperclass 는 상속관계 매핑은 아니다
- 해당 클래스는 엔티티가 아니다.(테이블과 매핑 X)
- 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공
- 직접 생성해서 사용할 일이 없으므로 추상클래스로 사용할 것을 권장
