# Spring 배치 메타테이블 살펴보기 

< Spring Batch 메타테이블 구조 [참고](https://docs.spring.io/spring-batch/docs/3.0.x/reference/html/metaDataSchema.html) >    

![스크린샷 2021-04-05 오전 9 29 20](https://user-images.githubusercontent.com/46964910/113525701-6808db80-95f1-11eb-99c6-597ec3cb7423.png)    
## BATCH_JOB_INSTANCE 

이전 시간까지 특별한 문제없이 진행했다면, MySQL 에서 BATCH_JOB_INSTANCE 테이블을 조회시 1개의 ROW 가 검색될 것이다.    
![스크린샷 2021-04-05 오전 9 35 27](https://user-images.githubusercontent.com/46964910/113525861-43613380-95f2-11eb-9bdc-c1d7b721ac99.png)     
- JOB_INSTANCE_ID : BATCH_JOB_INSTANCE 테이블의 PK    
- JOB_NAME : 수행한 Batch Job Name     

BATCH_JOB_INSTANCE 테이블의 튜플은 Job Parameter 에 따라 생성된다. Job Parameter 는 Spring Batch가 실행될 때 외부에서 받는 파라미터로 
예를들어 특정 날짜를 Job Parameter 로 넘기면 Spring Batch 에서는 해당 날짜로 조회/가공/입력 등의 작업을 할 수 있다.    
> 같은 Batch Job 이라도 Job Parameter 가 다르면 BATCH_JOB_INSTANCE 에 기록되며, Job Parameter 가 같으면 기록되지 않는다. 

```java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob(){
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1(null))
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate){
        return stepBuilderFactory.get("simpleStep1")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(" ======  Step 1 ====== ");
                    log.info(" >>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }
}
```
위의 코드에서 Job Parameter 를 받아서 사용하도록 하였다. IDE를 통해 인자값을 전달하고         
![스크린샷 2021-04-05 오전 10 24 00](https://user-images.githubusercontent.com/46964910/113527597-0a788d00-95f9-11eb-93f4-ccd208d48903.png)     
실행하면   

![스크린샷 2021-04-05 오전 10 24 38](https://user-images.githubusercontent.com/46964910/113527638-22501100-95f9-11eb-9000-4ae8b58520cb.png)     
다음과 같이 Job Parameter 를 전달받아 수행된 것을 확인할 수 있고, DB를 조회해보면     
![스크린샷 2021-04-05 오전 10 26 30](https://user-images.githubusercontent.com/46964910/113527729-63e0bc00-95f9-11eb-92cb-8881657ff407.png)    
JOB_INSTANCE 튜플이 2개 생성되어있는 것을 확인할 수 있다. 앞서서 동일한 Parameter 를 가진 Job Instance 는 하나만 생성된다고 하였는데, 동일한 파라미터를 넘겨서 다시 실행하면 
```java
Caused by: org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException: A job instance already exists and is complete for parameters={requestDate=20210404}.  If you want to run this job again, change the parameters.
```

위와같은 에러가 발생하게 된다. 그렇다면 다시한번 파라미터를 변경해 실행한다면?   
![스크린샷 2021-04-05 오전 10 30 39](https://user-images.githubusercontent.com/46964910/113527901-f8e3b500-95f9-11eb-8d6b-c5965a3fe304.png)     
에러없이 실행되고, 실제로 DB를 조회해봐도    
![스크린샷 2021-04-05 오전 10 31 30](https://user-images.githubusercontent.com/46964910/113527944-19ac0a80-95fa-11eb-8f11-7d8fd501b2c7.png)     
다른 파리미터가 들어왔기 때문에 새로운 JOB_INSTANCE 튜플이 생성된 것을 확인할 수 있다. 

## BATCH_JOB_EXECUTION 
JOB_INSTANCE 과 JOB_EXECUTION 는 부모-자식 관계로 JOB_EXECUTION 은 자신의 부모 JOB_INSTANCE 가 성공/실패했던 모든 내역을 갖고 있는다.      
무슨 말인지 알아보기 위해 코드를 수정하고 새로운 파라미터를 넘겨서 실행한 결과를 확인해보도록 하자. 
```java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob(){
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1(null))
                .next(simpleStep2(null))
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate){
        return stepBuilderFactory.get("simpleStep1")
                .tasklet(((contribution, chunkContext) -> {
                    throw new IllegalAccessException("step1에서 실패합니다.");
                }))
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate){
        return stepBuilderFactory.get("simpleStep1")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(" ======  Step 2 ====== ");
                    log.info(" >>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }
}
```
위의 코드에 따라 simpleStep1을 수행하던 도중 Runtime Exception 이 발생하여 Batch Job이 실패한다.
그렇다면 DB를 살펴보도록 하자.   
우선, BATCH_JOB_INSTANCE 테이블을 조회해보면
![스크린샷 2021-04-05 오전 10 44 22](https://user-images.githubusercontent.com/46964910/113528559-e4082100-95fb-11eb-9fed-81c74fd61286.png)     
다음과 같이 새로운파라미터로 JOB_INSTANCE 튜플이 생성된 것을 확인할 수 있다.    
     
다음으로, BATCH_JOB_EXECUTION 테이블을 조회해보자    
![스크린샷 2021-04-05 오전 10 46 53](https://user-images.githubusercontent.com/46964910/113528679-3cd7b980-95fc-11eb-9ef7-aea270026abf.png)     
JOB_INSTANCE_ID 가 4인 JOB 을 수행하다 FAILED 된 기록이 남아있게된다. 여기서 이제 코드를 수정해 에러가 발생하지 않도록 변경하고 실행해보도록 하자(파라미터는 동일)
```java
@Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate){
        return stepBuilderFactory.get("simpleStep1")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(" ======  Step 1 ====== ");
                    log.info(" >>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }
```
정상적으로 실행되는 것을 확인하고, DB를 확인해보면   
![스크린샷 2021-04-05 오전 10 52 16](https://user-images.githubusercontent.com/46964910/113528918-ff276080-95fc-11eb-809c-8a9b8f4bee09.png)    
다음과 같이 FAILED 기록과 COMPLETED 기록이 남아있는 것을 확인할 수 있다. 

> Job Instance 는 Job Parameter 단위로 생성되고, Job Execution 이 COMPLETED(성공적으로 수행)된 경우 재수행 되지 않는다.

----
위 내용은 [기억보단 기록을](https://jojoldu.tistory.com/324) 블르그의 글을 참조하여 재구성하였습니다.








