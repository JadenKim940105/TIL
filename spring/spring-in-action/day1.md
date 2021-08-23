# Spring-in-action #day1


정리내용 page 1 ~ 33 

###1
대부분의 애플리케이션은 애플리케이션 전체 기능 중 일부를 담당하는 많은 컴포넌트로 구성되며, 각 컴포넌트는 다른 애플리케이션 구성 요소와 협력해서 작업을 처리한다.
스프링은 ‘ 스프링 애플리케이션 컨텍스트 ‘ 라는 ‘ 컨테이너 ‘ 를 제공하고 이것은 애플리케이션 컴포넌트들을 생성하고 관리한다.

###2
컴포넌트 구성 설정방법
1. XML
2. 자바 코드
3. 자동-구성 (Autowiring, Componenet Scanning) => 스프링부트가 소개되며 주로 사용

###3
Maven 빌드시 pom.xml 의 <parent> 요소의 <version> 을 통해 여러 라이브러리의 의존성 관리

###4
@SpringBootApplication 어노테이션 은 3개의 어노테이션이 결합한 것
1. @SpringBootConfiguration : 현재 클래스를 ‘구성’ 클래스로 지정한다.  (@Configuration 과 동일한 역할)
2. @EnableAutoConfiguration : 스프링 부트 자동-구성 활성화
3. @ComponentScan : 컴포넌트 검색을 활성화

###5
스프링 부트 DEVTOOLS 를 사용하면 두 개의 클래스 로더 (base class loader, restart class loader) 에 의해 애플리케이션이 로드된다.
이 때, 변경이 감지될 경우  restart class loader 부분만 다시 로드하여 애플리케이션을 재실행 하는 방법으로 전체를 로드하는 것보다 시간을 단축시켜준다.

스프링 부트 DEVTOOLS 를 사용하면 템프릿 캐싱을 비활성화 하여 템플릿 변경시 브라우저 새로고침 만으로 변경된 템플릿 적용 가능.



