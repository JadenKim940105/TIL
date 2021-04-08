# Spring Batch Scope & Job parameter

## JobParameter 와 Spring Batch 의 Scope 활용
Spring 배치에서는 외부/내부 에서 파라미터를 받아 Batch 컴포넌트들이 사용할 수 있도록 지원하고 있다.
이때 배치 컴포넌트들이 사용하는 파라미터를 **Job Parameter** 라고한다.  
Job Parameter 를 사용하기 위해선 @StepScope / @JobScope 와 같은 Spring Batch 전용 Scope 를 선언해야 한다. 
해당 어노테이션을 붙이고 파라미터를 받을 때는 SpEL 을 사용하면 된다. 
> @Value("#{jobParameters[파라미터명]}")

1. @JobScope 사용예시 
```java
public class ScopeJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job scopeJob(){
        return jobBuilderFactory.get("scopeJob")
                .start(scopeStep1(null))
                .build();
    }

    @Bean
    @JobScope       // Step 선언문에는 @JobScope 사용
    public Step scopeStep1(@Value("#{jobParameters[requestDate]}") String requestDate){ // SpEL 사용
        return stepBuilderFactory.get("scopeStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>> ScopeStep1 ");
                    log.info(">>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
```
IDE 를 할용해 실행시킬 때 파라미터를 넘겨보도록 하겠다.  

![스크린샷 2021-04-07 오후 3 32 22](https://user-images.githubusercontent.com/46964910/113821443-0db97780-97b7-11eb-85d3-ecd890db6fa9.png)  
  

```
2021-04-07 15:31:37.607  INFO 96220 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=scopeJob]] launched with the following parameters: [{requestDate=20210407}]
2021-04-07 15:31:37.713  INFO 96220 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [scopeStep1]
2021-04-07 15:31:37.731  INFO 96220 --- [           main] m.s.s.job.ScopeJobConfiguration          : >>>> ScopeStep1 
2021-04-07 15:31:37.731  INFO 96220 --- [           main] m.s.s.job.ScopeJobConfiguration          : >>>> requestDate = 20210407
2021-04-07 15:31:37.750  INFO 96220 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [scopeStep1] executed in 34ms
2021-04-07 15:31:37.766  INFO 96220 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=scopeJob]] completed with the following parameters: [{requestDate=20210407}] and the following status: [COMPLETED] in 102ms
```
  
로그를 확인해보면 잘 넘어간 것을 확인할 수 있다. 

>@JobScope 는 Step 선언문에 사용한다. 

2. @StepScope 사용예시

````java
@Bean
public Step scopeStep2(){
    return stepBuilderFactory.get("scopeStep2")
            .tasklet(scopeStep2Tasklet(null))
            .build();
}

@Bean
@StepScope  // Tasklet 선언문에서는 @StepScope 사용 
public Tasklet scopeStep2Tasklet(@Value("#{jobParameters[requestDate]}") String requestDate){ // SpEL 사용
    return (contribution, chunkContext) -> {
        log.info(">>>>> ScopeStep2 ");
        log.info(">>>>> requestDate = {}", requestDate);
        return RepeatStatus.FINISHED;
    };
}
````

>@StepScope 는 Tasklet 이나 ItemReader, ItemWriter, ItemProcessor 에서 사용할수 있다. 

## @StepScope 와 @JobScope 
Spring Bean 의 기본 Scope 는 singleton 이다. 그리고 Bean 들은 어플리케이션이 구동될 떄 생성되어 applicationContext 에 올라가게 된다.  
하지만 @StepScope 를 사용하면 Spring Batch가 Spring 컨테이너를 통해 지정된 Step의 실행시점에 해당 컴포넌트를 Spring Bean으로 생성한다.
@JobScope 역시 Job 실행시점에 Bean 이 생성된다. 

> Bean 의 생성시점을 지정된 Scope가 실행되는 시점으로 지연시킨다.

Step과 Job의 생성시점을 지연시킴으로써 얻을 수 있는 장점이 크게 2가지 있는데,
1. Job Parameter 를 Late Binding 할 수 있다는 것이다. (구동시점 binding 하지않고 비즈니스 로직 처리단계에서 binding 가능)  
   
2. 동일한 컴포넌트를 병렬(동시)에 사용할 때 유용하다. (싱글톤이 아니므로 동시성 문제에서 자유롭다)






