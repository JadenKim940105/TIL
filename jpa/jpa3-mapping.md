# JPA#3 엔티티 매핑

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---

### 엔티티 매핑
```text
객체와 테이블 매핑 : @Entity, @Table
필드와 컬럼 매핑 : @Column
기본 키 매핑 : @Id
연관관계 매핑 : @ManyToOne, @JoinColumn 
```
### 객체와 테이블 매핑
```text
@Entity 가 붙은 클래스는 JPA 가 관리, '엔티티' 라고 한다.

주의사항
- 기본 생성자 필수 (public 또는 protected) -> JPA 구현체들이 엔티티 객체를 프록싱 하는 작업들이 일어나기때문에
- final 클래스, enum, interface, inner 클래스 사용 X
- 저장할 필드에 final 사용 x   
```

### 데이터베이스 스키마 자동 생성
```text
JPA 는 매핑정보를 기반으로 DDL 을 어플리케이션 실행 시점에 자동 생성해주는 기능을 제공한다.
DB방언을 활용해서 DB에 맞는 적절한 DDL을 생성해준다.

hibernate.hbm2ddl.auto = [create, create-drop, update, validate, none]
create => 기존테이블 삭제 후 다시 생성
create-drop => create 와 같으나 종료시점에 drop 
update => 변경부분만 반영 (필드 삭제는 적용 x) 
validate => 테이블과 엔티티가 정상 매핑되었는지 확인 
none => 사용 X 

주의사항 : 운영장비에서는 DDL 자동생성 기능을 사용하면 안된다. (특히, create, create-drop, update) 
```
### 필드와 컬럼 매핑
```text
@Id - PK 맵핑

@Column 
속성
-name : 필드와 매핑할 테이블의 컬럼 이름 (default 는 객체의 필드이름)
-insertable, updatable : 등록, 변경 가능 여부 (default 는 true)
-nullable(DDL) : null 값의 허용 여부를 설정. false 로 설정하면 DDL 생성 시 not null 제약조건이 붙는다. (default true)
-unique(DDL) : 유니크 제약조건을 걸 때 사용 ( 하지만 @Table 의 uniqueConstraints 를 주로 사용: 왜? 제약조건 이름을 알아보기 힘들다.)
-length(DDL) : 문자 길이 제약조건 (String 타입에만) 
 
@Enumerated - enum 타입 맵핑
주의사항 :  @Enumerated(EnumType.ORDINAL) 이 default 인데 @Enumerated(EnumType.STRING) 사용할 것 
왜? enum 이 추가되거나 삭제될 시 ORDINAL 이 변경됨. 

@Lob - 용량이 큰 컨텐츠를 넣어야할 떄 사용
매핑하는 필드 타입이 문자면(String, char[]) CLOB, 나머지는 BLOB 매핑 
 
@Temporal - 날짜타입 ( LocalDate / LocalDateTime 사용하면 생략가능 )
 
@Transient - 맵핑에서 제외할 필드 
```

### 기본 키 매핑
```text
직접 할당 : @Id 만 사용
자동 생성 : @GeneratedValue
IDENTITY : 데이터베이스에 위임, MySQL
SEQUENCE : 데이터베이스 시퀀스 오브젝트 사용, ORACLE -> @SequenceGenerator 필요
TABLE : 키 생성용 테이블 사용, 모든 DB에서 사용 -> @TableGenerator 필요
AUTO : 방언에 따라 자동 지정 (default 값임) 

1. IDENTITY 전략
- 기본 키 생성을 DB에 위임
- 주로 MySQL, PostgreSQL, SQL Server, H2 에서 사용 (ex : MySQL 의 auto_increment)
- 고려할 부분!! 
IDENTITY 전략은 DB 에 insert 가 들어가야 id 값을 가져올 수 있다. 
그리고 JPA 는 보통 트랜잭션 커밋 시점에 INSERT SQL 실행하게 된다. 
그런데 영속성 컨텍스트에서 관리가 되려면 반드시 id 가 존재해야 한다. 
그래서 IDENTITY 전략을 사용하면 persist() 호출 시 바로 insert 쿼리를 날려 id 값을 가져온다.   



2. SEQUENCE 전략
- SEQUENCE 를 생성해 사용 
@Entity
@SequenceGenerator(
    name= "MEMBER_SEQ_GENERATOR",
    sequenceName = "MEMBER_SEQ",
    initialValue = 1, allocationSize=1)
public class Member {
    @ID
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "MEMBER_SEQ_GENERATOR")
    private Long id; 
}
-  SequenceGenerator 를 활용해서 시퀀스 DDL 가능 없을시 (hibernate_sequence) 
- 고려할점!! 
persist() 호출 시 다음 시퀀스번호를 가져와야한다. 
allocationSize 의 크기만큼 가져온다. 
즉 매번 persist() 마다 가져오는 것은 아니고 allocationSize 만큼 한번에 땡겨와서 
메모리에 두고 가지고 쓰다가 그걸 다쓰면 또 땡겨오는 방식 


3. TABLE 전략
키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
장점 : 모든 데이터베이스에 적용 가능
단점 : 성능 
-> 거의 사용하지 않는다. 

권장되는 식별자 전략
- 기본키 제약조건 : null 아님, 유일, 변하면 안된다.
- 위 조건을 만족하는 자연키는 찾기 어렵다. 대리키(대체키) 사용하자.
=> Long 형 대체키를 사용하자. 
```


