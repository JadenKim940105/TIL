# 테코톡#6 Process 와 Thread

---

해당 포스팅은 우아한Tech의 테크토크 [Process vs Thread - 코다](https://www.youtube.com/watch?v=1grtWKqTn50) 영상을 참고하여 재구성 하였습니다.

---

## Process 와 Thread

컴퓨터로 작업을 하다보면, Chrome 과 같은 브라우저와 메신저 IDE 같은 여러가지 프로그램을 동시에 사용(Process) 
하는 경우가 많다. 하지만 한 순간에 CPU를 점유할 수 있는 프로세스는 오직 하나이다. 그래서 이렇게 동시에
여러 프로세스를 돌리는 경우에는 CPU 점유시간을 짧게 분할해 여러 프로세스들이 번갈아 가며
CPU를 점유하게 된다. 이렇게 **_시분할_** 을 통해 여러 프로세스들이 **_컨텍스트 스위칭(Context Switching)_** 을
해가며 동시 작업을 하는 것 처럼 느껴지는 것이다.

위의 예시를 통해 Process, 시분할, ContextSwitching, 동시성(Concurrency) 에 대해 이해했다면 다음으로 넘어가 보자.

우선, 프로세스의 구조를 살펴보도록 하자 프로세스 구조는 다음과 같다.  
![프로세스 구조](https://github.com/JadenKim940105/TIL/blob/master/techtalk/img/processStructure.png)  
  
Code 영역에는 프로그램의 코드, Data 영역에는 전역변수(static, global 변수), 
Heap 영역에는 동적으로 생긴 데이터(new Object()),
Stack 영역에는 호출된 함수, 지역변수 등 임시 데이터가 저장된다. 

프로세스의 동작은 Stack 영역의 함수들이 순서대로 진행되는 과정이다. 나머지 영역들은 Stack 의 함수들이 실행되는 결과를 담고
혹은 Stack 의 함수들이 사용할 데이터를 제공해주는 부분이라고 생각해도 무방하다. 

즉 프로세스의 작업단위는 바로 이 Stack 이고 이러한 작업단위를 Thread 라고 한다.   
![스레드 구조](https://github.com/JadenKim940105/TIL/blob/master/techtalk/img/thread.png)  

동시작업을 위해 ContextSwitching 을 할 때, 프로세스 여러 개 생성해 스위칭 하기보다 실제 작업 단위인 
스레드를 스위칭하는 비용이 프로세스 ContextSwitching 보다 작다는 것을 쉽게 떠올릴 수 있다. 다만, 
스레드들은 CODE, DATA, HEAP 영역을 공유함으로 동기화 작업을 제대로 하지 않으면 원하지 않는 오류가
생길 수 있게 되는 것이다. 


## Multi-Process vs Multi-Thread 

#### MultiProcess 
1. 각 프로세는 독립적
2. 프로세스간 IPC 를 사용한 통신
3. 자원 소모적, 개별 메모리 차지
4. Context Switching 비용이 큼
5. 동기화 작업이 필요하지 않음

#### MultiThread
1. Thread 끼리 긴밀히 연결
2. 공유 자원으로 통신 비용 절감
3. 공유 자원으로 효율적인 메모리 사용
4. Context Switching 비용이 작음
5. 동기화 작업이 필요함



## Multi-Core 

Multi-Core 는 말 그대로 여러개의 Core 를 사용하는 것이다.
Multi-Process 와 Multi-Thread 가 '처리방식' 과 관련된 부분 이였다면 Multi-Core 는 하드웨어적으로
여러개의 코어를 사용해 '병렬처리' 를 가능하게 하는 것이다. 

## 요약

1. 프로세스는 실행중인 프로그램이다.
2. 스레드는 한 프로세스 내에서 나뉘어진 하나 이상의 실행단위 이다.
3. 한 어플리케이션에 대한 작업을 동시에 하기 위해서는 2가지 처리 방식(Multi-Process, Multi-Thread)이 있다.
4. 동시에 실행이 되는 것처럼 보이기 위해 실행단위는 시분할로 cpu 를 점유하며 Context-Switching 을 한다.
5. 멀티 프로세스는 독립적인 메모리를 가지고 있지만, 스레드는 자원을 공유한다. 그것에 따른 각각의 장단점이 있다.
6. 멀티 코어는 하드웨어 측면에서 실행단위를 병렬적으로 처리할 수 있도록 여러 프로세서(코어)가 있는 것이다. 