#Spring Batch Job Flow


## Next 를 활용한 흐름제어
Spring Batch 에서 배치 작업단위는 Job 으로 나타내고 Job 은 Step 들로 이루어져 있었다.  
실제 비즈니스 로직은 Step 안에 구현되어 있고 Job 에서는 Step 들간의 순서(흐름)을 제어하고 있다. 다음의 예제 코드를 살펴보도록 하자. 

```java
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration // Spring Batch의 모든 Job은 @Configuration으로 등록해서 사용
@RequiredArgsConstructor
public class StepNextJobConfiguration {   

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextJob(){                       // Job 을 구현하는 Bean 생성 
        return jobBuilderFactory.get("stepNextJob") // Job 에서 Step 들의 순서설정
                .start(step1())
                .next(step2())                      // next() 를 통해 다음에 올 Step 을 지정
                .next(step3())
                .build();
    }

    @Bean
    @JobScope
    public Step step1(){                            // 실제 비즈니스 로직이 들어가는 Step
        return stepBuilderFactory.get("step1") 
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> Step 1 <<<<<"); // 비즈니스 로직 (예제에서는 간단히 로그찍기)
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    @JobScope
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> step2 <<<<<");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    @JobScope
    public Step step3(){
        return stepBuilderFactory.get("step3")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> step3 <<<<<");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}

```

위의 코드를 실행시키고 로그를 확인해보면, 

```java
2021-04-06 14:15:24.703  INFO 92087 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=simpleJob]] launched with the following parameters: [{}]
2021-04-06 14:15:24.824  INFO 92087 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=1, version=3, name=simpleStep1, status=COMPLETED, exitStatus=COMPLETED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=1, rollbackCount=0, exitDescription=
2021-04-06 14:15:24.847  INFO 92087 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [simpleStep2]
2021-04-06 14:15:24.864  INFO 92087 --- [           main] m.s.s.job.SimpleJobConfiguration         :  ======  simpleStep2 ====== 
2021-04-06 14:15:24.864  INFO 92087 --- [           main] m.s.s.job.SimpleJobConfiguration         :  >>> requestDate = null
2021-04-06 14:15:24.882  INFO 92087 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [simpleStep2] executed in 35ms
2021-04-06 14:15:24.897  INFO 92087 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=simpleJob]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 137ms
2021-04-06 14:15:24.919  INFO 92087 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=stepNextJob]] launched with the following parameters: [{}]
2021-04-06 14:15:24.947  INFO 92087 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
2021-04-06 14:15:24.958  INFO 92087 --- [           main] m.s.s.job.StepNextJobConfiguration       : >>>>> Step 1 <<<<<
2021-04-06 14:15:24.969  INFO 92087 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 22ms
2021-04-06 14:15:24.996  INFO 92087 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step2]
2021-04-06 14:15:25.011  INFO 92087 --- [           main] m.s.s.job.StepNextJobConfiguration       : >>>>> step2 <<<<<
2021-04-06 14:15:25.025  INFO 92087 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step2] executed in 28ms
2021-04-06 14:15:25.047  INFO 92087 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step3]
2021-04-06 14:15:25.059  INFO 92087 --- [           main] m.s.s.job.StepNextJobConfiguration       : >>>>> step3 <<<<<
2021-04-06 14:15:25.073  INFO 92087 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step3] executed in 26ms
2021-04-06 14:15:25.090  INFO 92087 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=stepNextJob]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 166ms
```  

이전에 만들어두었던 'simpleJob' 과 이번에 만든 'stepNextJob' 이 모두 수행되는 것을 확인할 수 있고, Job 에서 지정한 순서대로 Step 들이 순차적으로 동작하는 것을 확인할 수 있다. 

## 지정한 Batch Job 만 수행시키기
앞선 작업에서 이전에 만들어두었던 simpleJob 과 새로만든 stepNextJob 이 모두 수행되는 것을 확인할 수 있었다. 만약 특정 Job 만 수행하고 싶다면 다음과 같이 설정할 수 있다. 

#### .yml (혹은 .properties 설정)
```java
spring:
  batch:
    job:
      names: ${job.name:NONE}
```
위와 같이 spring.batch.job.names 을 설정해 둔다. **spring.batch.job.names 에 할당된 값과 동일한 Job 만 수행**하게 되며 
위와 같은 설정을 하면 Spring Batch 가 실행될 때 **Program Arguments 로 job.name 값을 넘기면 해당 값을 spring.batch.job.names에 할당하고 해당 값이 없으면 
NONE 을 할당해 어떠한 Job 도 수행시키지 않게된다**

제대로 동작하는지 확인해보도록 하자.  program arguments 로 jab.name 에 stepNextJob 만 넘겨보도록 하겠다. 
![스크린샷 2021-04-06 오후 2 35 18](https://user-images.githubusercontent.com/46964910/113663777-92d45c00-96e5-11eb-9073-2bda532d68c9.png)    

실행시키고 로그를 살펴보면
```java
2021-04-06 14:35:37.709  INFO 92212 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=stepNextJob]] launched with the following parameters: [{}]
2021-04-06 14:35:37.797  INFO 92212 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=15, version=3, name=step1, status=COMPLETED, exitStatus=COMPLETED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=1, rollbackCount=0, exitDescription=
2021-04-06 14:35:37.804  INFO 92212 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=16, version=3, name=step2, status=COMPLETED, exitStatus=COMPLETED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=1, rollbackCount=0, exitDescription=
2021-04-06 14:35:37.812  INFO 92212 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=17, version=3, name=step3, status=COMPLETED, exitStatus=COMPLETED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=1, rollbackCount=0, exitDescription=
2021-04-06 14:35:37.827  INFO 92212 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=stepNextJob]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 64ms
```
stepNextJob 만 실행된 것을 확인할 수 있다. 



