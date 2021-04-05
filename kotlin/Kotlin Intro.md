# Kotlin

![스크린샷 2021-04-05 오후 10 28 13](https://user-images.githubusercontent.com/46964910/113578722-37ab5680-965e-11eb-9caa-403ff578357b.png)     

코틀린은 JVM 에서 동작하는 프로그래밍 언어로 2011년 7월 젯브레인사에서 공개하였다. 
나는 현재까지 JAVA 를 제외한 언어를 제대로 공부해본 적 없지만, Kotlin 은 차근차근 공부해보려고 한다. 
이유는 여러가지가 있겠지만, 우선 JVM 위에서 돌아감으로 JAVA 와 상호운영이 가능하고 
최신언어인 만큼 잘 사용한다면 JAVA 로 작성할 떄보다 간결한 코드를 작성할 수 있다고한다. 


이외에도 코틀린을 배워야 하는 이유를 몇 가지 생각해보 다음과 같다. 
1. Null safe - 기본적으로 null 참조를 못하고 null 값을 가지려면 nullable 로 선언해야한다. 
   즉, null 값으로 인한 오류를 방지할 수 있다.
2. IntelliJ 사용자이다 - 개인적으로 사용하는 IDE 가 IntelliJ 이다. 
   같은 회사에서 나온 언어인만큼 IntelliJ 가 얼마나 Kotlin 을 잘 지원해줄지는 
   사용해보지 않아도 알 수 있다. 
3. 새로운 언어 배우기 - 아직 제대로(?) 사용할 수 있는 언어는 JAVA 하나 뿐이다. 
   새로운 언어를 공부함으로써 개발자가 가져야할 역량 중 하나라고 생각하는 
   새로운 기술을 받아들이는 힘을 기를 수 있을 것이라고 생각한다.
   

## Hello, Kotlin! 
프로그래밍 언어 공부의 시작을 알리기 위해 Kotlin 세상에 인사를 날려보자!

Java 프로젝트를 생성하듯, Kotlin 프로젝트를 생성하면 된다. 나는 여기서 Build System 을 Gradle Kotlin 로 선택하였다.    

![스크린샷 2021-04-05 오후 10 32 34](https://user-images.githubusercontent.com/46964910/113588486-2b79c600-966b-11eb-8f18-476cedb68d15.png)
            
계속해서 프로젝트 생성을 진행한다. 
![스크린샷 2021-04-05 오후 10 32 58](https://user-images.githubusercontent.com/46964910/113588515-37fe1e80-966b-11eb-9272-e470048af1b6.png)
       
Kotlin 파일을 생성하고 main 함수를 생성해 Hello Kotlin 을 출력해보았다. (Build System 을 Gradle Kotlin 으로 선택해 실행시 gradle 로 빌드되고 실행되었다)
![스크린샷 2021-04-05 오후 11 59 22](https://user-images.githubusercontent.com/46964910/113588880-a3e08700-966b-11eb-9aef-c2a256667db3.png)
      
콘솔 화면에 출력문을 보기 힘들고 빌드되는 시간을 줄이기 위해 실행시 Intellij 에서 바로 실행되도록 변경하였다.     
![스크린샷 2021-04-06 오전 12 10 09](https://user-images.githubusercontent.com/46964910/113589741-b7d8b880-966c-11eb-9fa4-464dda5adbaf.png)     

원하는대로 Hello, Kotlin! 이 출력되는 것을 확인할 수 있다. 
![스크린샷 2021-04-05 오후 10 50 05](https://user-images.githubusercontent.com/46964910/113589791-c626d480-966c-11eb-9b83-b1842b744701.png)





   

