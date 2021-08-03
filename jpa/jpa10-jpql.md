# JPA#10 객체지향 쿼리

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---
```text
JPA 는 JPQL, JPA Criteria, QueryDSL, native SQL, JDBC API 직접 사용, MyBatis 
등등 다양한 쿼리 방법을 지원한다.
```

### JPQL 
```text
가장 단순한 조회는 EntityManager.find() 혹은 객체 그래프 탐색 a.getB().getC() 와 같은 방법으로 조회가 가능했다.
하지만 나이가 18살 이상인 회원을 모두 검색하고 싶다면 어떻게 해야할까 ?
이럴 때 JPQL 을 사용하면 엔티티 객체를 대상으로 검색이 가능하다. 
JPA 는 SQL 을 추상화한 JPQL 을 지원하고 JPQL 을 사용하면 엔티티 객체를 대상으로 쿼리가 가능해진다.
동작자체는 결국 JPQL 이 SQL 로 변환되어 쿼리가 날라간다. 

String jpql = "select m From Member m where m.age >= 18";
List<Member> result = em.createQuery(jpql, Member.class).getResultList();
```

### JPA Criteria
```text
JPQL 은 단순 문자열로 작성한다. 그렇기 떄문에 동적 쿼리를 작성하는데 있어서 불편한 부분이 많다.
이를 해결하기 위해 자바 표준에서 제공하는 Criteria 를 사용하여 쿼리를 코드로 작성할 수 있다.

// Criteria 사용 준비
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Member> query = cb.createQuery(Member.class);

// 루트 클래스 (조회를 시작할 클래스)
Root<Member> m = query.from(Member.class);  

// 쿼리 생성 
CriteriaQuery<Member> cq = query.select(m)
String username = "test";
if (username != null){
 cq = cq.where(cb.equal(m.get("username"), username));
}
List<Member> resultList = em.createQuery(cq).getResultList();

위으 코드를 보면 동적쿼리를 비교적 쉽게 작성할 수 있다는 것을 볼 수 있다. 
하지만 쿼리문 자체를 해석하기가 너무 힘들다는 단점이 있다. 따라서 실무에서 실용성이 떨어진다.
Criteria 대신 QueryDSL 오픈소스 라이브러리 사용을 권장한다.

--> QueryDSL 예시
JPAFactoryQuery query = new JPAQueryFactory(em);
QMember m = QMember.member;

List<Member> result = query.selectFrom(m)
                           .where(m.age.gt(18))
                           .orderBy(m.name.desc())
                           .fetch(); 
```

### Native SQL
```text
JPQL 로 해결할 수 없는 특정 데이터베이스에 의존적인 기능을 사용해야할 때 사용할 수 있다.
ex) 오라클 CONNECT BY 

em.createNativeQuery(SQL, Member.class).getResultList(); 
```

### 참고사항
```text
참고로 flush() 는 commit 뿐만 아니라 쿼리가 날라가는 시점에도 일어난다. (Hibernate 구현체를 사용하는 경우, Spring Data JPA 는 hibernate 사용)  
하지만 JDBC Template 를 직접 사용하거나 JPA 랑 관련 없는 방식으로 쿼리를 날리는 경우에는 쿼리를 날리기 직전에 수동으로
flush 를 날려주어야 한다. 
```


### JPQL 문법
```text
- 엔티티와 속성은 대소문자를 구분한다.
- JPQL 키워드는 대소문자를 구분하지 않는다.
- 엔티티 이름을 사용한다(테이블 이름 X)
- 별칭은 필수 (as 는 생략가능)


<TypeQuery 와 Query> 
TypeQuery : 반환 타입이 명확할 때 사용 
(ex) TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
Query : 반환타입이 명확하지 않을 떄 사용
(ex) Query query = em.createQuery("select m.username, m.age from Member m");


<getResultList() 와 getSingleResult()>
결과가 하나 이상일 때, (Collection 인 경우) 
query.getResultList();
=> 결과가 없는 경우 빈 리시트 반환 

결과가 하나 일 때,
query.getSingleResult();
=> 결과가 없으면 NoResultException, 둘 이상이면 NonUniqueResultException
--> Spring Data JPA 를 사용하는 경우 NoResultException 을 내부적으로 try-catch 로 감싸 null 을 반환해준다.

 

<파라미터 바인딩 - 이름기준, 위치기준>
이름기준
Member result = em.createQuery("select m from Member m where m.username = :username", Member.class);
                             .setParameter("username", "member1");
                             .getSingleResult(); 

위치기준
Member result = em.createQuery("select m from Member m where m.username = ?1", Member.class);
                  .setParameter(1, "member1");
                  .getSingleResult();
                  
                  
<프로젝션>
SELECT 절에 조회할 대상을 지정하는 것 
프로젝션 대상 : 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자 등 기본 데이터 타입) 
ex)
SELECT m FROM Member m -> 엔티티 프로젝션 ( == SELECT * FROM Member )
SELECT m.team FROM Member m -> 엔티티 프로젝션 ( 
SELECT m.address FROM Member m -> 임베디드 타입 프로젝션 
SELECT m.username, m.age FROM Member m -> 스칼라 타입 프로젝션 
DISTINCT 로 중복제거 가능

select 의 응답값이 여러 타입인 경우?
-> DTO 로 조회하는 방법  (new 키워드 사용, 패키지명을 다 적어줘야함)
em.createQuery("select new jpql.MemberDto(m.username, m.age) from Member m", MemberDto.class)



```


