# JPA#2 영속성컨텍스트

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---


## EntityMangerFactory & EntityManger

![엔티티매니저팩토리](https://github.com/JadenKim940105/TIL/blob/master/jpa/img/emf.png)  

```text
요청이 오는 경우 EntityMangerFactory 에서 EntityManger 를 생성하고, EntityManger 는
JDBC Connection 을 사용해 DB 와 통신한다.  
```

## 영속성컨텍스트
```text
- "엔티티를 영구 저장하는 환경"
- EntityManger.persist(entity) 는 entity 를 DB에 persist 한다는 뜻이 아니라 
  영속성 컨텍스트에 persist 한다는 뜻이다. 
```

### 엔티티의 생명주기 
```text
1. 비영속(new/transient) : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
2. 영속(managed) : 영속성 컨텍스트에 관리되는 상태
3. 준영속(detached) : 영속성 컨텍스트에 저장되어있다가 분리된 상태
4. 삭제(removed) : 삭제된 상태  
```
```java
// member 객체를 생성, 영속성 컨텍스트와는 관계없는 transient 상태
Member member = new Member();
member.setId(1L);
member.setName("tester");

// member 객체를 영속성 컨텍스트에 persist 하여 manager 상태 ( DB에 persist 된 건 아님 )
em.persist(member);

// DB와 통신하는 시점은 transaction 이 commit 되는 시점 
tx.commit();
```

### 영속성 컨텍스트의 이점
```text
- 1차 캐시
    영속성 컨텍스트는 내부에 1차 캐시를 가지고 있다. 1차 캐시는 key-value(pk값-객체) 테이블 형태로 저장된다.
    이렇게 1차 캐시를 가지고 있다면 조회시에 우선 1차 캐시에 존재하는지 확인하고 있다면 DB가 아닌 1차 캐시값을 반환해주면 된다.   
- 동일성(identity) 보장
    1차 캐시의 key값(pk) 을 통해 엔티티의 동일성을 보장해준다.    
- 트랜잭션을 지원하는 쓰기 지연
    transaction commit 이 일어날 때 까지 SQL 을 보내지 않고 "쓰기 지연 SQL 저장소" 에 쌓아두고
    commit 시점에 flush 가 발생하면서 DB에 쿼리가 날라간다. 
- 변경감지(Dirty-Checking) 
    1. transaction commit 을 하면 내부적으로 flush() 가 일어나게 된다. 
    2. flush() 가 발생하면 엔티티와 스냅샷을 비교하게 된다. (스냅샷은 1차캐시에 들어간 최초 상태를 저장)
    3. 변경사항이 있으면 update 쿼리를 '쓰기 지연 SQL 저장소' 에 쌓는다 
    4. SQL 전송
- 지연로딩(Lazy-loading) 
```


