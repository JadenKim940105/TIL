# JPA#1 Intro

---

이 포스팅은 김영한님의 JPA 강의를 듣고, 제 나름대로 다시 공부하고 정리한 포스팅 입니다. 만약, 조금 더 자세한 내용을 듣고 싶으시다면 [이곳의 강의](https://www.inflearn.com/users/@yh) 를 참고해보시기 바랍니다

---

## JPA? 
````text
JPA (Java Persistence API) 
자바 진영의 ORM 기술 표준

ORM(Object-relational mapping)? 
객체와 관계형데이터베이스를 매핑해주는 기술 
````
![JPA 활용그림](https://github.com/JadenKim940105/TIL/blob/master/jpa/img/jpaintro.png)

### 동작방식 
```text
객체(Entity) 분석 -> SQL 생성 -> JDBC API 사용해 DB에 쿼리 
즉, JPA 는 객체를 분석해서 객체와 릴레이션간 패러다임 불일치를 
해결한 쿼리를 작성하여 JDBC 를 사용해 DB와 통신한다. 
```

### JPA 발전과정 
```text
EJB(엔티티 빈) ORM 자바표준기술이 있었으나 성능과 활용면에서 불편함이 많아 거의 사용되지 않았다.
그래서 Gavin King 이라는 개발자가 Hibernate 라는 ORM 프레임워크를 만들기 시작하였고
오픈소스화되어 많은 사람들이 동참해 완성되었다. Hibernate 가 각광받기 시작하자 자바 잔영에서
Hibernate 를 기반으로 만든 ORM 기술이 JPA 이다. 
```

### JPA를 사용해야하는 이유
```text
1. SQL 중심적인 개발이 아닌 객체 중심 개발이 가능하다.
2. 높은 생산성 -> 쿼리를 JPA가 생성해준다, '변경감지'
3. 유지보수성  -> 필드 변경시 관련된 모든 SQL 을 수정하던 기존방식에 비해 훨씬 용이한 유지보수성
4. 성능향상 -> JAVA 어플리케이션과 RDB 사이에 JPA 라는 캐시/버퍼 라고 생각해보자.  
```

### JPA 와 패러다임의 불일치 해결
```text
객체의 '상속', '연관관계, 객체 그래프 탐색' 와 같은 개념들은 RDB 에는 존재하지 않는 개념이다.
JPA를 활용하면 객체의 '상속', '연관관계, 객체 그래프 탐색' 을 사용하여 RDB를 조작할 수 있다.
```


## JPA 프로젝트 시작하기 
```text
Project Environment
 Language :  Java 11
 DB       :  h2 
 Builder  :  maven
```
 
프로젝트 생성 후, hibernate 와 h2 driver 의존성 추가.
```xml
<dependencies>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-entitymanager</artifactId>
        <version>5.4.29.Final</version>
    </dependency>

    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.199</version>
    </dependency>
</dependencies>
```

META-INF/persistence.xml (설정정보) 생성
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_2.xsd">

    <persistence-unit name="hello">
        <properties>
            <!-- 연결할 DB 정보 입력-->
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>

            <!-- 옵션 설정 -->
            <property name="hibernate.show_sql" value="true"/> 
            <property name="hibernate.format_sql" value="true"/> 
            <property name="hibernate.use_sql_comment" value="true"/>
        </properties>
    </persistence-unit>

</persistence>
```
Entity 객체 생성 및 JPA 활용  

```java
// Member 엔티티 생성
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

```java
// jpa 활용하여 insert query 날려보기. ( 테이블 생성되어 있어야함 ) 
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        // persistence 설정의 persistence-unit name
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        // 데이터를 변경하는 모든 작업은 transaction 안에서 이루어진다.
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Member member = new Member();
            member.setId(1L);
            member.setName("tester");
            em.persist(member);
            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
/*
EntityManagerFactory - 하나만 생성해서 어플리케이션 전체에서 공유
EntityManger - 쓰레드간에 공유 X (사용후 close)
JPA 의 모든 데이터 변경은 트랜잭션 안에서 실행 
 */
```




