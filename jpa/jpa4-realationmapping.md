# JPA#4 연관관계 매핑 

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---

### 테이블 중심 모델링의 문제점
```java 
객체를 테이블에 맞추어 모델링 하면 협력 관계를 만들 수 없다. 
객체는 연관객체를 찾을 때 참조를 사용한다. 그에 반면 테이블은 외래키로 join 을 사용해 연관 테이블을 찾는다.
만약 객체를 테이블에 맞추어 모델링 한다면 다음과 같이 설계할 수 있을 것이다.

@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String username;

    @Column(name = "team_id") // 테이블에 맞추어 fk 를 집어넣었다.
    private Long teamId;    
}

@Entity
public class Team {

    @Id @GeneratedValue
    private Long id;

    private String name;
}

이렇게 설계한 경우 member 객체에서 team 을 참조할 수 없다. 
아래의 코드처럼 fk(team_id) 를 가지고 직접 가져와 사용해야 한다. 

 Member findMember = em.find(Member.class, member.getId());
 Long findTeamId = findMember.getTeamId();
 Team findTeam = em.find(Team.class, findTeamId); 
```

### 객체 지향 모델링
````java
기존의 테이블 중심의 모델링에서 객체 중심의 모델링으로 변경해보자

@Entity
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "team_id")  
    private Team team;  // fk(team_id) 가 아닌 Team 을 참조.
}

Member findMember = em.find(Member.class, member.getId());
Team findTeam = findMember.getTeam(); // 객체 그래프 탐색이 가능하다.
````

### 양방향 연관관계와 연관관계의 주인 
```java
앞선 코드는 단방향 연관관계로 Member 에서 Team 으로의 탐색은 가능했지만,
Team 에서 Member 로의 탐색은 불가능했다. 이 떄 Team 에서도 Member 로의 탐색을 가능하게 설계하는 것을 
"양방향 연관관계" 라고 한다.

양방향 연관관계를 설정해 보도록 하자

@Entity
public class Team {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();
}


양방향 연관관계를 위해 Team 엔티티에 List<Member> 를 추가하였다. 그렇다고 테이블에 변화가 있을까?
정답은 아니다. 테이블은 fk 를 통해 테이블간의 연관관계를 맺기 때문에 fk 만 있다면 서로 간의 탐색이 가능하다.

또 중요한 부분은 Member 와 Team 둘 중 하나로 외래키를 관리해야 한다는 것이다. 
다음의 상황을 가정해보자
멤버가 속한 팀을 설정하기 위해 
1. member.setTeam(teamA); 와 같이 팀을 설정하였다.
2. 반면 teamA 의 List<Member> 에는 member 를 추가하지 않았다. 
그렇다면 jpa 입장에서는 member 의 fk 에 값을 집어넣아야할지 말아야할지 딜레마가 오게되는 것이다.

이런 딜레마를 없애기 위해 두 객체중 하나를 연관관계의 주인으로 지정하고 
연관관계의 주인만이 외래키를 관리하고 주인이 아닌 쪽은 읽기만 가능한 규칙이 존재한다.
바로 mappedBy 가 주인이 아닌쪽에서 주인을 지정해주는 속성이다. 

그렇다면 어떤 객체를 주인으로 지정해야할까? 
외래키가 있는 쪽을 주인으로 지정하는 것을 권장한다. 즉 1:n 관계에서 1 쪽을 주인으로 지정하는 것을 권장한다. 
1을 관계의 주인으로 지정하면 1의 데이터를 수정하였는데 n 테이블의 업데이트가 되기 때문에(fk는 n 쪽에 있으니) 굉장히 헷갈리기 때문이다. 
따라서, 객체의 주인은 항상 외래키가 있는 쪽으로 한다.
```

### 양방향 매핑시 주의해야할 점 
```java
앞서 설명했듯이 연관관계의 주인인 쪽만 외래키를 관리한다.
그런데 주인이 아닌쪽에서만 연관관계를 맺으면 jpa 는 연관관계를 맺어주지 않는다.
즉, 연관관계를 설정하려면 주인인 쪽에서 세팅을 하든, 아니면 양쪽 다 세팅해주어야 테이블 fk 가 설정된다.
더 나아가서 양방향 매핑시에는 객체 관계를 고려해서 양쪽 다 값을 입력해주도록 하자. 
-> 연관관계를 설정해야 하는 경우 다음의 코드처럼 '연관관계 편의 메소드' 로 설정해두도록 하자.
public void addMember(Member member){
    this.members.add(member);
    member.joinTeam(this);
}

더불어 양방향 매핑시 상호참조에 의한 무한루프를 조심하자
```

### 정리
```text
단방향 매핑만으로도 이미 연관관계 매핑은 완료 ( 테이블은 pk 로 연관관계를 설정함으로 ) 
양방향 매핑은 반대 방향으로 조회(객체 그래프 탐색) 기능을 추가한 것 뿐
단방향 매핑을 잘하고 양방향은 필요할 때 추가해도 됨

연관관계의 주인은 외래키가 있는 쪽을 주인으로 정한다. 
```