# JPA#9 값 타입 컬렉션

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---

## 값 타입 컬렉션 
RDB의 테이블의 컬럼으로 컬렉션을 사용할 수 없기 때문에 별도의 테이블을 생성해 사용해야 한다. 

- @ElementCollection, @CollectionTable 을 사용해 매핑 
```java
    @ElementCollection // 값 타입 컬렉션
    @CollectionTable(name = "favorite_food", // 테이블 이름 
            joinColumns = @JoinColumn(name = "member_id") // join 을 위한 컬럼 이름 
    )
    private Set<String> favoriteFoods = new HashSet<>();
```
- 값 타입 컬렉션도 지연로딩 전략 사용
- 값 타입 컬렉션은 영속성 전이(Cascade) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다.
- 즉, 값타입 컬렉션의 생명주기는 당연히 속한 엔티티를 따른다. 

## 값 타입 컬렉션 수정
수정할 부분을 컬렉션에서 remove 하고 변경 값을 add 해주면 된다. 
```java
   findMember.getFavoriteFoods().remove("치킨"); 
   findMember.getFavoriteFoods().add("한식");
```

컬렉션이 임베디드 타입이고 그 중 일부만 수정하고 싶다면? 
값 타입임으로 마찬가지로 통째로 교체를 해주어야한다. 이때 컬렉션에서 remove 가 제대로 이루어지려면
equals() 와 hashcode() 를 재정의해서 사용해주어야 한다. 
```java
    findMember.getAddressHistory().remove(new Address("old1", "street1", "old"));
    findMember.getAddressHistory().add(new Address("newCity", "street1", "old"));
```
다만, 의도는 old1 만 변경하려 했어도 member 의 모든 addressHistory 를 삭제하고, 변경된 부분을 포함한 모든 부분을
다시 insert 하는 쿼리가 날라가는 것을 확인할 수 있다.
=> 실무에서는 상황에 따라 값 타입 컬렉션 대신 일대다 관계를 고려

## 값 타입 컬렉션 제약사항 
- 값 타입은 엔티티와 다르게 식별자 개념이 없다. 
- 값을 변경하면 추적이 어렵다.
- 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 현재 있는 값을 모두 다시 저장한다. 
- 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본 키를 구성해야한다. 
=> 실무에서는 상황에 따라 값 타입 컬렉션 대신 일대다 관계를 고려 
  
## 정리
- 값 타입은 정말 값 타입이라 판단될 때 사용.
- 식별자가 필요하고, 지속해서 값을 추적 변경 해야한다면 값타입이 아닌 엔티티