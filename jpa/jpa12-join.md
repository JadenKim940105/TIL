# JPA#12 Join

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---
```sql
JPQL 의 join 은 역시나 엔티티를 중심으로 쿼리를 작성한다.
내부조인 :  SELECT m FROM Member m [INNER] JOIN m.team t
외부조인 : SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
세타조인 : SELECT COUNT(m) FROM Member m, Team t WHERE m.username = t.name  

* 세타조인? : 조인에 참여하는 두 릴레이션의 속성 값을 비교하여 조건을 만족하는 튜플을 반환한다. 
```

조인대상 필터링
```sql
ex) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
SQL  : SELECT m.*, t.* FROM Member m LEFT JOIN TEAM t ON m.TEAM_ID = t.id AND t.name = 'A'
JPQL : SELECT m, t FROM Member m LEFT JOIN m.team t ON t.name = 'A'
```

연관관계가 없는 엔티티 외부 조인 
```sql
ex) 회원의 이름과 팀의 이름이 같은 대상 외부 조인 
SQL  : SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name 
JPQL : SELECT m, t FROM Member m LEFT JOIN Team t ON m.username = t.name

-> 회원의 이름과 팀의 이름 사이에는 아무런 연관관계가 없지만 조인이 가능함. 
```