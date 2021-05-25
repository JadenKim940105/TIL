# Java#3 Concurrent 프로그래밍 

---

해당 포스팅은 인프런 백기선님의 [더 자바, Java 8](https://www.whiteship.me/courses/) 강좌를 참고하여 재구성 하였습니다.

---

### Concurrent 소프트웨어란?
- 동시에 여러 작업을 할 수 있는 소프트웨어 
- ex) 문서 작업을 하면서 유튜브로 노래를 듣는다. 

### 자바가 지원하는 Concurrent 프로그래밍 
- 멀티프로세싱 (ProcessBuilder)
- 멀티쓰레드 

### MultiThread 
- Thread 를 상속받아 구현
```java
    private static class Thread1 extends Thread{
        @Override
        public void run() {
            System.out.println("Thread1 - " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread1();
        thread1.start();
        System.out.println("MainThread - " + Thread.currentThread().getName() );
    }
```
- Thread 를 생성하는데 생성자에 Runnable 을 준다. 
```java
    public static void main(String[] args) {
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread2 - " + Thread.currentThread().getName());
            }
        });
        thread2.start();
        System.out.println("MainThread - " + Thread.currentThread().getName() );
    }
```
--> 람다식으로 사용도 가능하다 
```java
    public static void main(String[] args) {
        Thread thread2 = new Thread(() -> {
            System.out.println("Thread2 - " + Thread.currentThread().getName());
        });
        thread2.start();
        System.out.println("MainThread - " + Thread.currentThread().getName() );
    }
```

### Thread 의 기능 
- sleep : 해당 쓰레드 대기 (다른 쓰레드에게 우선권 양도)
```java
 Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("Thread2 - " + Thread.currentThread().getName());
        });
        thread2.start();
```
- 
- interrupt : sleep 상태의 쓰레드의 경우 꺠움 
```java
 public static void main(String[] args) throws InterruptedException {
        Thread thread2 = new Thread(() -> {
            while (true){
                System.out.println("Thread2 - " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    System.out.println("exit!");
                    return;
                }
            }
        });
        thread2.start();
        
        System.out.println("MainThread - " + Thread.currentThread().getName() );
        Thread.sleep(3000l);
        thread2.interrupt();
    }
```
- join : 현재 쓰레드가 join 한 쓰레드를 기다린다.
    - join 상태에서도 InterruptedException 일어날 수 있다. 
```java
   public static void main(String[] args) throws InterruptedException {
        Thread thread2 = new Thread(() -> {
           System.out.println("Thread2 - " + Thread.currentThread().getName());
           try {
               Thread.sleep(3000l);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
        });
        thread2.start();
        System.out.println("MainThread - " + Thread.currentThread().getName() );
        System.out.println("MainThread wait for thread2");
        thread2.join(); // 메인쓰레드가 thread2 작업의 완료를 기다린다. 
        System.out.println("Waiting is over");
    }
```

 

