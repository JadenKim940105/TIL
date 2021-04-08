# Spring Batch 스프링 배치 사용해보기(1)

## 1. @EnableBatchProcessing
Spring batch 기능 활성화 어노테이션을 추가해 Spring batch 기능을 사용할 수 았다.
```java
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }

}
```

## 2. Batch Job & Step 만들어 사용해보기 

- Spring Batch 에서 Job 은 하나의 배치 작업 단위를 의미한다.    

- Job 안에는 여러 Step 이 존재하고, Step 안에 'Tasklet' 혹은 'Reader & Processor & Writer 묶음' 이 존재한다.  
![스크린샷 2021-04-04 오후 3 01 27](https://user-images.githubusercontent.com/46964910/113500169-e1072500-9556-11eb-9e38-9cc219811245.png)

```java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    // 'simpleJob' 이라는 Batch job 생성 
    @Bean
    public Job simpleJob(){
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1())
                .build();
    }

    // 'simpleStep1' 이라는 Batch step 생성
    @Bean
    public Step simpleStep1(){
        return stepBuilderFactory.get("simpleStep1")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(" ======  Step 1 ====== ");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }
}
```

## 3. MySQL 환경에서 Spring Batch 실행해보기 
Spring Batch 를 사용하기 위해선 여러가지 메타 데이터 테이블이 필요하다.([참고](https://docs.spring.io/spring-batch/docs/3.0.x/reference/html/metaDataSchema.html))   
h2 DB를 사용할 경우 Spring boot 가 필요한 메타 데이터 테이블을 생성해주지만, 다른 DB를 사용할 경우 직접 메타 데이터 테이블을 생성해 두어야 한다. 
Intellij 를 사용하는 경우 파일찾기(cmd+shift+o)에서 schema 를 검색하면 메타 데이터 테이 스키마들을 DBMS 별로 보여준다.    

<스키마 검색화면>    

![스크린샷 2021-04-04 오후 3 52 29](https://user-images.githubusercontent.com/46964910/113501077-d13f0f00-955d-11eb-92d0-4757ea0b2608.png)     

<MySQL 스키마 파일>     

![스크린샷 2021-04-04 오후 3 56 18](https://user-images.githubusercontent.com/46964910/113501153-4e6a8400-955e-11eb-94f4-2999beb55c5a.png)     

<MySQL WorkBench 에서 복사한 스키마를 통해 메타 데이터 테이블 생성>      

![스크린샷 2021-04-04 오후 4 02 43](https://user-images.githubusercontent.com/46964910/113501335-32b3ad80-955f-11eb-95ea-06530764cfbf.png)      

해당 스키마를 모두 복사하여 메타 데이터 테이블을 생성하고 돌리면 정상적으로 동작하는 것을 확인할 수 있다.

----
위 내용은 [기억보단 기록을](https://jojoldu.tistory.com/324) 블르그의 글을 참조하여 재구성하였습니다.





