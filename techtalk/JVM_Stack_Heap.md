# 테코톡#3 JVM Stack & Heap

---

해당 포스팅은 우아한Tech의 테크토크 [JVM Stack & Heap -무민](https://www.youtube.com/watch?v=UzaGOXKVhwU) 영상을 참고하여 재구성 하였습니다.

---

## JVM?

c, c++ 는 컴파일 플랫폼과 타겟 플랫폼(=배포할 플랫폼)이 다를 경우, 프로그램이 동작하지 않는다. 
(컴파일 된 코드는 플랫폼에 종속적이기 때문에)
이를 해결하기 위해 타겟 플랫폼에 맞춰 컴파일 하는 것을 **_크로스컴파일(Cross Compile)_** 이라고 한다.

Java 는 컴파일 코드의 플랫폼 종속 문제를 JVM 을 통해 해결할 수 있다.  
java 는 JVM 위에서 동작하기 때문에 타겟플랫폼과는 관련이 없다. 
즉, 자바 소스코드를 컴파일 하면 JVM 이 이해할 수 있는 자바바이트 코드가 된다. 그리고 자바바이트 코드는 어떤 플랫폼의 JVM 위에서든 동일하게 동작한다. 

소스코드 -> [javac(자바 컴파일러)] -> JavaByteCode -> [JVM] -> AssemblyProgram 

## JVM 내부 구조

JVM 내부 구조에서 Runtime Data Areas 는 Method Area, Heap, Stack, PC register, Native Method Stack 5개의 영역으로 구성된
JVM이 Java ByteCode 를 실행 하기 위해 사용하는 메모리 공간이다.  
이 중에서 Method Area, Heap 는 모든 쓰레드가 공유하는 영역이다.   

- Method Area : 클래스 로더가 클래스 파일을 읽어오면 클래스 정보를 파싱해서 
  Method Area 에 저장 
- Heap : 프로그램을 실행하면서 생성한 모든 객체를 저장 
- PC(Program Counter) : 각 쓰레드는 메서드를 실행하고 있고, pc 는 그 메서드 안에서 몇 번째 줄을 
  실행하는지 나타내는 역할
- Stack : 스레드 별로 1개만 존재하고 스택 프레임은 메서드가 호출될 떄마다 생성된다.
  메서드 실행이 끝나면 스택 프레임은 pop 되어 스택에서 제거된다. 
- Native Method Stack : Java ByteCode 가 아닌 다른 언어로 작성된 메서드를 사용할 떄 필요한 영역
  
#### 스택프레임? 
- 스택 프레임은 메서드가 호출될 때마다 새로 생겨 스택에 Push 된다.  
- 스택 프레임은 Local variable array, Operand stack, Frame Data 를 갖는다.  
- Frame Data 는 Constant Pool(상수풀), 이전 스택 프레임에 대한 정보, 
  현재 메서드가 속한 클래스/객체에 대한 참조 등의 정보를 갖는다.  
  
----  

```java
public static void main(String[] args) {
        double position = 1.0;
        double initial = 1.0;
        double rate = 1.0;

        position = initial + rate * 60;
}


Local Varaible Array 에는 다음과 같이 저장될 것이다 
0 args 
1 position
3 initial
5 rate

그리고 다음과 같은 bytecode 가 진행될 것이다. 
0 dconst_1 (operand stack 에 1.0 을 push)
1 dstore_1 (operand stack 에서 pop 한 값을 Local Varaible Array 1번 인덱스에 저장) -> postion = 1
2 dconst_1 (operand stack 에 1.0 을 push)
3 dstore_3 (operand stack 에서 pop 한 값을 Local Varaible Array 3번 인덱스에 저장) -> initial = 1
4 dconst_1 (operand stack 에 1.0 을 push)
5 dstore_5 (operand stack 에서 pop 한 값을 Local Varaible Array 5번 인덱스에 저장) -> rate = 1 
7 dload_3 (Local Variable Array 3번 index 의 값을 Operand Stack 에 push ) -> (operand Stack 에 initial의 값 1 push)
8 dload_5 (Local Variable Array 5번 index 의 값을 Operand Stack 에 push ) -> (operand Stack 에 rate 값 1 push)
10 ldc2_w #2 <60.0> Constant Pool 에서 2번째 값을 가져온다. (double 형 리터럴 60.0) -> (operand Stack 에 상수풀 2번 인덱스값 60.0 push)
13 dmul (Operand Stack 에서 두 값을 pop 한 후 곱해서 push) 
14 dadd (Operand Stack 에서 두 값을 pop 한 후 더해서 push)
15 dstore_1 (operand stack 에서 pop 한 값을 Local Varaible Array 1번 인덱스에 저장) -> postion = 61
16 return
```



