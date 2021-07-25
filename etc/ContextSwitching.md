# etc. ContextSwitching

## 인터럽트와 컨택스트스위칭
```text
인터럽트가 발생하면 OS 는 현재 CPU 에서 진행중인 작업을 멈추고 ISR(Interrupt Service Routine) 이 진행된다.
인터럽트가 발생하면 시스템은 현재 진행중인 프로세스의 문맥(Context)을 저장해두어야 인터럽트 처리가 끝나고 난 후 
다시 진행중이었던 프로세스를 정상적으로 수행할 수 있게된다. 

1. 프로세스 작업 진행
2. 인터럽트 A 발생
3. 현재 실행중인 프로세스 문맥(Context) 를 저장
4. 인터럽트 백터에서 해당 ISR 을 찾음 
5. 인터럽트 A 처리를 위한 ISR 주소로 변경
6. 인터럽트 A 처리
7. 기존에 작업중이던 프로세스 주소로 변경 

기존에 진행중이던 프로세스 문맥을 저장하고 CPU 가 다른 프로세스로 넘어가는 과정을 바로 컨택스트스위칭 이라고 한다.

추가적으로, 인터럽트가 발생한다고 그 즉시 진행중인 작업이 멈추는 것은 아니다. 명령어가 처리 사이클은 인출(fetch) 와
실행(excution) 두 단계를 반복해서 수행하는데 실행단계를 마칠 때 마다 인터럽트가 있는 지 확인을 하고 있다면 인터럽트를 처리하게 된다. 

또한 인터럽트가 발생해 ContextSwitching 이 일어나는 시간 동안은 CPU 는 idle(유휴) 상태, 즉 어떠한 프로세스의 작업도
진행하지 않는 상태가 된다. 즉, 문맥교환 작업은 overhead 이고 너무 잦은 문맥교환은 성능저하를 야기한다.  
```

### 다양한 인터럽트
```text
1. I/O request (입출력 요청할 때)

2. time slice expired (CPU 사용시간이 만료 되었을 때) 

3. fork a child (자식 프로세스를 만들 때)

4. wait for an interrupt (인터럽트 처리를 기다릴 때)

외에도 다양한 인터럽트가 존재하며, 인터럽트가 일어나면 OS스케쥴러가 다음 수행할 프로세스를 결정하게 된다. 
```

### PCB(Process Control Block)
![PCB](https://github.com/JadenKim940105/TIL/blob/master/etc/img/pcb.png)
```text
프로세스의 문맥은 해당 프로세스의 PCB(Process Control Block) 에 기록되어 있다. 
```
