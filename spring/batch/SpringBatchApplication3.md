# Spring Batch#5 Job Flow(2)

## 조건별 흐름 제어
Next를 통해 순차적으로 Step의 순서를 제어한다는 것을 이전 예제코드를 통해 확인할 수 있었다. 중요한 사실은 **앞의 Step 에서 오류가 나면 뒤에 있는 Step 들은 실행되지 않는다**는 것이다.  
그렇다면 다음과 같은 흐름제어가 필요할 땐 어떻게 할 수 있을까? 
- A Step 수행성공 -> B Step 수행  
- A Step 수행실패 -> C Step 수행 

코드를 통해 어떻게 제어를 하는지 확인해보도록 하자. 

```java
@Configuration
@RequiredArgsConstructor
@Slf4j
public class StepNextConditionalJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextConditionalJob(){
        return jobBuilderFactory.get("stepNextConditionalJob")  // stepNextConditionalJob 을 생성하는데
                .start(conditionalStepA())                      // conditionalStepA 를 수행한다.
                    .on("FAILED")                       // 수행결과가 FAILED 인경우
                    .to(conditionalStepC())                             // conditionalStepC 를 수행한다.
                    .on("*")                                    // conditionalStepC 수행결과와 상관없이 수행하고 나면
                    .end()                                             // Flow 종료
                .from(conditionalStepA())                       // conditionalStepA 의 수형결과가
                    .on("*")                                    // FAILED 가 아닌 다른 결과라면 (ex - COMPLETED)
                    .to(conditionalStepB())                             // conditionalStepB 를 수행한다.
                    .on("*")                                    // conditionalStepC 의 수행결과와 상관없이 수행하고 나면
                    .end()                                             // Flow 종료
                .end()                                          // Job 종료
                .build();
    }

    @Bean
    public Step conditionalStepA(){
        return stepBuilderFactory.get("stepA")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> This is stepNextConditionalJob STEP-A");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step conditionalStepB(){
        return stepBuilderFactory.get("stepB")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> This is stepNextConditionalJob STEP-B");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step conditionalStepC(){
        return stepBuilderFactory.get("stepC")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> This is stepNextConditionalJob STEP-C");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}

```

만약, conditionalStepA 가 문제없이 수행되고 COMPLETED 된다면 job flow 는 A -> B 로 이어질 것이다. 로그를 확인 해보도록 하자.

```java
2021-04-06 15:44:15.641  INFO 92754 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=stepNextConditionalJob]] launched with the following parameters: [{}]
2021-04-06 15:44:15.716  INFO 92754 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepA]
2021-04-06 15:44:15.733  INFO 92754 --- [           main] .s.j.StepNextConditionalJobConfiguration : >>> This is stepNextConditionalJob STEP-A
2021-04-06 15:44:15.750  INFO 92754 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [stepA] executed in 32ms
2021-04-06 15:44:15.771  INFO 92754 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepB]
2021-04-06 15:44:15.779  INFO 92754 --- [           main] .s.j.StepNextConditionalJobConfiguration : >>> This is stepNextConditionalJob STEP-B
2021-04-06 15:44:15.788  INFO 92754 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [stepB] executed in 17ms
2021-04-06 15:44:15.803  INFO 92754 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=stepNextConditionalJob]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 107ms
```

원하는 흐름대로 수행된 것을 확인할 수 있다.  
그렇다면 이번엔 step A 를 수행한 결과상태를 FAILED 로 바꾸어보자

````java
    @Bean
    public Step conditionalStepA(){
        return stepBuilderFactory.get("stepA")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> This is stepNextConditionalJob STEP-A");
                    contribution.setExitStatus(ExitStatus.FAILED);      
                    return RepeatStatus.FINISHED;
                }).build();
    }
````

위와같이 코드를 수정하고 실행시켜 로그를 확인해보면

````java
2021-04-06 15:56:55.606  INFO 92781 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=stepNextConditionalJob]] launched with the following parameters: [{v=2}]
2021-04-06 15:56:55.668  INFO 92781 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepA]
2021-04-06 15:56:55.688  INFO 92781 --- [           main] .s.j.StepNextConditionalJobConfiguration : >>> This is stepNextConditionalJob STEP-A
2021-04-06 15:56:55.704  INFO 92781 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [stepA] executed in 36ms
2021-04-06 15:56:55.726  INFO 92781 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepC]
2021-04-06 15:56:55.736  INFO 92781 --- [           main] .s.j.StepNextConditionalJobConfiguration : >>> This is stepNextConditionalJob STEP-C
2021-04-06 15:56:55.744  INFO 92781 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [stepC] executed in 17ms
2021-04-06 15:56:55.758  INFO 92781 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=stepNextConditionalJob]] completed with the following parameters: [{v=2}] and the following status: [COMPLETED] in 111ms
````
job flow 는 A -> C 로 이어진 것을 확인할 수 있다.

> .on() : 캐치할 ExitStatus 지정,   * 일 경우 모든 ExitStatus 가 지정  
> 
> .to() : 다음으로 이동할 step 지정  
> 
> .from() : 일종의 이벤트 리스너 역할, 해당 step 의 상태값을 보고 일치하면 .to()에 포함된 step 호출

## Decide
Spring Batch 에서는 Job의 Step flow 속에서 분기만 담당하는 타입이 있다. 그것은 바로 JobExecutionDecider 이다. 
이를 활용한 샘플코드를 살펴보도록 하자.

````java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class DeciderJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job deciderJob(){
        return jobBuilderFactory.get("deciderJob")
                .start(startStep())
                .next(decider())        // decider 로 넘어가
                .from(decider())        // decider 이 반환하는 값에 따라 flow 결정
                    .on("ODD")      // decider 반환값이 ODD 면
                    .to(oddStep())          // oddStep 수행
                .from(decider())        // decider 이 반환하는 값에 따라 flow 결정
                    .on("EVEN")     // decider 반환값이 EVEN 이면
                    .to(evenStep())         // evenStep 수행
                .end()
                .build();
    }

    @Bean
    public Step startStep(){
        return stepBuilderFactory.get("startStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> Start!");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step evenStep(){
        return stepBuilderFactory.get("evenStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> 짝수입니다.");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step oddStep(){
        return stepBuilderFactory.get("oddStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> 홀수입니다.");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public JobExecutionDecider decider(){
        return new OddDecider();
    }

    public static class OddDecider implements JobExecutionDecider{
        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            Random random = new Random();

            int randomNumber = random.nextInt(10) + 1;
            log.info("생성한 랜덤숫자 {}", randomNumber);

            if (randomNumber % 2 == 0){
                return new FlowExecutionStatus("EVEN");
            } else {
                return new FlowExecutionStatus("ODD");
            }
        }
    }
}

````

위의 코드를 수행하면 랜덤숫자가 홀수인 경우 startStep -> oddStep  의 job flow 를 가지게 될 것이고 짝수인 경우 startStep -> evenStep 이 순차적으로 수행될 것이다. 실행시키고 로그를 살펴보도록 하자.

```java
2021-04-06 16:26:13.625  INFO 92851 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=deciderJob]] launched with the following parameters: [{v=3}]
2021-04-06 16:26:13.707  INFO 92851 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [startStep]
2021-04-06 16:26:13.722  INFO 92851 --- [           main] m.s.s.job.DeciderJobConfiguration        : >>>>> Start!
2021-04-06 16:26:13.739  INFO 92851 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [startStep] executed in 31ms
2021-04-06 16:26:13.747  INFO 92851 --- [           main] m.s.s.job.DeciderJobConfiguration        : 생성한 랜덤숫자 6
2021-04-06 16:26:13.765  INFO 92851 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [evenStep]
2021-04-06 16:26:13.773  INFO 92851 --- [           main] m.s.s.job.DeciderJobConfiguration        : >>>>> 짝수입니다.
2021-04-06 16:26:13.781  INFO 92851 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [evenStep] executed in 16ms
2021-04-06 16:26:13.795  INFO 92851 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=deciderJob]] completed with the following parameters: [{v=3}] and the following status: [COMPLETED] in 113ms
```

위의 로그에서 랜덤숫자 6이 나왔고  startStep -> evenStep Flow 로 작업이 수행된 것을 확인할 수 있다. 

홀수가 나오는 상황을 확인하기 위해 코드를 몇 번더 수행시켜봤다.

```java
2021-04-06 16:28:34.548  INFO 92862 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=deciderJob]] launched with the following parameters: [{v=6}]
2021-04-06 16:28:34.611  INFO 92862 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [startStep]
2021-04-06 16:28:34.632  INFO 92862 --- [           main] m.s.s.job.DeciderJobConfiguration        : >>>>> Start!
2021-04-06 16:28:34.647  INFO 92862 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [startStep] executed in 36ms
2021-04-06 16:28:34.655  INFO 92862 --- [           main] m.s.s.job.DeciderJobConfiguration        : 생성한 랜덤숫자 3
2021-04-06 16:28:34.672  INFO 92862 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [oddStep]
2021-04-06 16:28:34.682  INFO 92862 --- [           main] m.s.s.job.DeciderJobConfiguration        : >>>>> 홀수입니다.
2021-04-06 16:28:34.690  INFO 92862 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [oddStep] executed in 17ms
2021-04-06 16:28:34.705  INFO 92862 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=deciderJob]] completed with the following parameters: [{v=6}] and the following status: [COMPLETED] in 115ms
```

랜덤숫자 3이 나왔다 startStep -> oddStep Flow 로 작업이 수행된 것을 확인할 수 있다.

----
위 내용은 [기억보단 기록을](https://jojoldu.tistory.com/324) 블로그의 글을 참조하여 재구성하였습니다.