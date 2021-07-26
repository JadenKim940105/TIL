# JPA#8 값 타입

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---

## JPA의 데이터 타입 분류 
1. 엔티티 타입
   - @Entity 로 정의하는 객체
   - 데이터가 변해도 식별자로 지속해서 추적 가능 
2. 값 타입
   - int, Integer, String 처럼 단순히 값으로 사용하는 자바 기본타입이나 객체 
   - 값 그 자체로 변경시 추적 불가
   
## 값 타입 분류
1. 기본값 타입
   - 자바 기본타입 (int, double..)
   - Wrapper Class (Integer, Double..)
   - String
2. 임베디드 타입(Embedded Type = 복합 값 타입)
   - ex) 좌표 (x, y 로 구성한 Position 클래스)
3. 컬렉션 타입 
   - 자바 컬렉션을 사용
   
## Embedded Type
```java
새로운 값 타입을 직접 정의해서 사용하는 것을 의미한다.
복합 값 타입을 위한 객체는 기본 생성자 필수. 

예를들어 Member 엔티티는 이름, 근무 시작일, 근무 종료일, 주소(도시, 번지, 우편번호) 를 가진다. 
-> Member 엔티티를 이름, 근무기간, 주소 형태로 구현 할 수 있을 것이다. 
-> @Embeddable(값 타입을 정의하는 곳), @Embedded(값 타입을 사용하는 곳)

@Entity
public class Member{
    
   @Id @GeneratedValue
   private Long id;
   
   private String name;
   
   private LocalDate startDate;
   
   private LocalDate endDate;
   
   private String city;
   
   private String street;
   
   private String zipCode;
}

------ 임베디드 타입 활용 
@Embeddable
public class WorkPeriod{
   private LocalDate startDate;

   private LocalDate endDate;
}

@Embeddable
public class Address{
   private String city;

   private String street;

   private String zipCode;
}

@Entity
public class Member{

   @Id @GeneratedValue
   private Long id;

   private String name;

   @Embedded
   private WorkPeriod workPeriod;
   
   @Embedded
   private Address address;
}

----------------
한 엔티티에서 같은 값 타입을 여러번 사용하는 경우 컬럼 명이 중복됨으로 @AttributeOverrides, @AttributeOverride
를 활용해 컬럼명을 재정의할 수 있다.

@Entity
public class Member{

   @Id @GeneratedValue
   private Long id;

   private String name;

   @Embedded
   @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "home_city")),
            @AttributeOverride(name = "street", column = @Column(name = "home_street")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "home_zipcode")),
           }
   )
   private Address HomeAddress;

   @Embedded
   @AttributeOverrides({
           @AttributeOverride(name = "city", column = @Column(name = "work_city")),
           @AttributeOverride(name = "street", column = @Column(name = "work_street")),
           @AttributeOverride(name = "zipcode", column = @Column(name = "work_zipcode")),
          }
   )
   private Address WorkAddress;
}

```

### MappedSuperclass 과 EmbeddedType (상속과 위임)
```text
이전 포스팅에서 createTime, createdBy, lastModifiedAt, lastModifiedBy 와 같이 여러 엔티티에서
공통으로 사용할 만한 속성들을 부모 클래스로 묶어 상속을 사용해 재사용 가능하고, 응집도를 높인 클래스를 활용하여 
엔티티를 설계할 수 있는 것을 확인하였다.
그리고 이번 포스팅에서 EmbeddedType 을 활용해도 비슷한 작업이 가능한 것을 확인할 수 있다.
즉, MappedSuperclass 는 객체지향의 '상속' 을 EmbeddedType 객체지향의 '위임' 을 사용하는 것이다.  
일반적으로 상속보다 위임이 더 좋은 설계방법이라고 하지만 경우에 따라서 상속이 편한 경우도 있다.

예를들어 JPQL 을 사용할 때 상속을 활용하면 바로 필드에 접근하면 되지만 상속을 사용하면 임베디드타입의 필드에 접근해야한다.
ex) 
"select m from Member m where m.createdTime = ?" : 상속 사용시 필드 바로 접근 ==> 직관적임
"select m from Member m where m.baseEntity.createdTime = ?" : 위임 사용시 임베디드 타입의 필드에 접근
```

### 값 타입과 불변객체(Immutable)
```text
값 타입은 단순하고 안전하게 다룰 수 있어야한다. 
임베디드 타입 같은 값 타입을 공유해서 참조하는 경우 side effect 가 발생할 수 있다.
즉, Member1 과 Member2 가 같은 Address 를 참조하는 하는게 가능하고,  Member1 의 주소를 바꾸고 싶어 변경하면
같은 곳을 참조하는 Member2 의 주소도 변경이 되는 side effect 가 발생할 수 있는 것이다. 

이러한 side effect 를 최대한 방지하려면 값 타입은 Immutable 객체로 설계를 한다.
그리고 변경이 필요한 경우에는 변경할 값의 인스턴스를 새로 생성하여 통째로 변경해준다. 
```
### 값 타입의 비교
```text
값 타입의 경우 인스턴스가 다르더라도 그 안의 값이 같으면 같은 것으로 봐야한다 

Address a = new Address("서울시");
Address b = new Address("서울시"); 
인 경우 a == b -> false 이다. (인스턴스가 다름으로) 
따라서 값 타입 비교는 == (동일성 비교 - 인스턴스의 참조 값을 비교) 가 아닌 equals(동등성 비교 - 인스턴스의 값을 비교) 를 사용한다. 
즉, 값 타입은 말 그대로 '값' 을 표현하는 것임으로 비교를 할 때는 equals() 를 재정의 하여 사용한다.

 
```