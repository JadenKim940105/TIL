# etc. MongoDB

## Docker 를 이용한 MongoDB Installation 

우선 MongoDB 를 사용해보기 위해 도커를 활용하여 MongoDB와 
MongoExpress의 이미지를 받아 컨테이너를 만들고 실행하여 Dokcer network 를 활용해 
두 컨테이너가 통신할 수 있도록 하겠다. 
  
Docker-Compose 를 사용해 간편하게 설정해보도록 하겠다. 
```yaml
version: "3.8"
services:
    mongodb:          # 몽고DB 컨테이너
        image: mongo  # 이미지 정보 
        container_name: mongodb # 컨테이너 이름 
        ports:                  
            - 27017:27017     # 포트 맵핑
        volumes:  
            - data:/data      # 볼륨 설정 (몽고가 사용할 저장영역 정도로 생각하면 될 것 같다) 
        environment:          # 환경설정 ( credential ) 
            - MONGO_INITDB_ROOT_USERNAME=rootuser
            - MONGO_INITDB_ROOT_PASSWORD=rootpass
    mongo-express:    # 몽고Express 컨테이너 
        image: mongo-express    # 이미지 정보 
        container_name: mongo_express   # 컨테이너 이름 
        restart: always
        ports:  
            - 8081:8081  # 포트 맵핑
        environment:     # 환경설정 ( credetial ) 
            - ME_CONFIG_MONGODB_ADMINUSERNAME=rootuser
            - ME_CONFIG_MONGODB_ADMINPASSWORD=rootpass
            - ME_CONFIG_MONGODB_SERVER=mongodb
volumes:
    data: {}

networks:           # 네트워크 설정
    default:
        name: mongodb_network
```
yaml 파일을 작성하였다면 다음과 같이 실행시켜보자.  
![Compose활용](https://github.com/JadenKim940105/TIL-images/blob/master/img/etc/mongo/1start.png)  
  
컨테이너가 뜬 것을 확인하였고 직접 MongoExpress 에 들어가보면 잘 동작하는 것을 확인 할 수 있다.  
![MongoExpress](https://github.com/JadenKim940105/TIL-images/blob/master/img/etc/mongo/2mongoexpress.png)

#### Mongo Shell 사용하기
MongoExpress 와 같은 GUI 툴을 사용할 수도 있지만 Mongo Shell 을 사용해 간단히 몽고DB를 조작할 수 있다.

```text
1. 컨테이너에 접속한다.
docker exec -it [컨테이너id/컨테이너name] 

2. Mongo Shell 에 접속한다.
mongo mongodb://localhost:27017 -u [설정한 credential 위 설정에 따라 여기선 rootuser]  -p [마찬가지]rootpass 
```

## Create Database 
접속한 MongoShell 에서 데이터베이스를 생성해보자.  
```text
(Mongo Shelle 에 접속한 상태) 

> use summerbelldb;                     // use [생성할 db명] 으로 db 생성 
switched to db summerbelldb
> show dbs                              // show dbs 를 하면 생성한 db 가 보이지 않는다..                         
admin   0.000GB
config  0.000GB
local   0.000GB 
> db.getName();                         // 현재 사용중인 db를 찍어보면 summerbelldb 가 제대로 나온다.
summerbelldb
> db.createCollection("person");        // Collection 을 생성해 주어야 
{ "ok" : 1 }
> show dbs                              // show dbs 목록에 생성한 db 가 보이는 것을 확인할 수 있다. 
admin         0.000GB
config        0.000GB
local         0.000GB
summerbelldb  0.000GB

// db.help() 를 쳐보면 MongoShell 을 어떻게 조작할 수 있는지에 대해 잘 설명되어있다.  
```


## MongoDB의 구조

1. Collection ( RDB의 테이블 개념과 동일하다.  )  
2. Documents  ( RDB의 레코드/튜플/row 와 동일하다. )  
-> MongoDB stores data records as BSON documents. BSON is a binary representation of JSON documents  
   몽고는 데이터 레코드를 BSON documents 로 저장한다. BSON 은 JSON documents 의 이진수표현법이다.
   

## Document 생성
앞서서, db.createCollection() 을 사용해 collection 을 생성해 보았는데, 굳이 collection을 생성하지 않더라도
Document 를 생성할 떄 collection 이 없는 경우 자동으로 생성해 준다.
```text
> student = {
...     "firstName": "Jaden",
...     "lastName": "kim",
...     "email": "jaden@email.com",
...     "gender": "M",
...     "country": "Republic of Korea",
...     "isStudent": false,
...     "favouriteSubjects": [
...         "PE",
...         "ComputerScience"
...     ],
...     "totalSpentInBooks": 0.00
... }
{
	"firstName" : "Jaden",
	"lastName" : "kim",
	"email" : "jaden@email.com",
	"gender" : "M",
	"country" : "Republic of Korea",
	"isStudent" : false,
	"favouriteSubjects" : [
		"PE",
		"ComputerScience"
	],
	"totalSpentInBooks" : 0
}
> db.student
summerbelldb.student
> db.student.insert(student)
WriteResult({ "nInserted" : 1 })
> show collections
student                 // createCollection() 을 하지 않았지만 document 를 생성함으로 student collection 이 생성된걸 확인
```

insert 가 제대로 되었는지 확인해보자 
```text
> db.student.find().pretty()
{
	"_id" : ObjectId("60c78412ea3b0060957f8b09"),
	"firstName" : "Jaden",
	"lastName" : "kim",
	"email" : "jaden@email.com",
	"gender" : "M",
	"country" : "Republic of Korea",
	"isStudent" : false,
	"favouriteSubjects" : [
		"PE",
		"ComputerScience"
	],
	"totalSpentInBooks" : 0
}
```

## MongoDB query 
1. 앞서서 insert 는 살펴보았다.  
 

2. find 는 다음과 같은 방식을 사용한다
```text
db.users.find(              // collection 
{ age: { $gt: 18 } },       // query criteria
{ name: 1, address: 1 }     // projection ( 1 은 해당 필드를 포함하는 것을 의미, 0 은 exclude 섞어서는 사용 안되는 듯?) 
).limit(5)                  // cursor modifier 
```

데이터를 추가하고 find 를 사용해보았다.  
```text
> db.student.find({firstName: 'Jaden'}).pretty();
{
	"_id" : ObjectId("60c78412ea3b0060957f8b09"),
	"firstName" : "Jaden",
	"lastName" : "kim",
	"email" : "jaden@email.com",
	"gender" : "M",
	"country" : "Republic of Korea",
	"isStudent" : false,
	"favouriteSubjects" : [
		"PE",
		"ComputerScience"
	],
	"totalSpentInBooks" : 0
}
> db.student.find({firstName: 'summer'}).pretty();
{
	"_id" : ObjectId("60c786c7ea3b0060957f8b0a"),
	"firstName" : "summer",
	"lastName" : "Bell",
	"email" : "summerbell@email.com",
	"gender" : "M",
	"country" : "Republic of Korea",
	"isStudent" : false,
	"favouriteSubjects" : [
		"PE",
		"ComputerScience"
	],
	"totalSpentInBooks" : 0
}
```

3. update 
```text
// 업데이트 쿼리 
> db.student.update({_id: ObjectId("60c786c7ea3b0060957f8b0a")}, {$set: {firstName: 'Winter'}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

// 확인해보면 
> db.student.find().pretty()
{
	"_id" : ObjectId("60c78412ea3b0060957f8b09"),
	"firstName" : "Jaden",
	"lastName" : "kim",
	"email" : "jaden@email.com",
	"gender" : "M",
	"country" : "Republic of Korea",
	"isStudent" : false,
	"favouriteSubjects" : [
		"PE",
		"ComputerScience"
	],
	"totalSpentInBooks" : 0
}
{
	"_id" : ObjectId("60c786c7ea3b0060957f8b0a"),
	// 변경되어있다. 
	"firstName" : "Winter",                      
	"lastName" : "Bell",
	"email" : "summerbell@email.com",
	"gender" : "M",
	"country" : "Republic of Korea",
	"isStudent" : false,
	"favouriteSubjects" : [
		"PE",
		"ComputerScience"
	],
	"totalSpentInBooks" : 0
}
```
특정 프로퍼티를 삭제하는 것도 가능하다.
```text
// 쿼리
> db.student.update({_id: ObjectId("60c786c7ea3b0060957f8b0a")}, {$unset: {lastName: 1}})

// 결과
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

// 찍어보면 
> db.student.find().pretty()
{
	"_id" : ObjectId("60c78412ea3b0060957f8b09"),
	"firstName" : "Jaden",
	"lastName" : "kim",
	"email" : "jaden@email.com",
	"gender" : "M",
	"country" : "Republic of Korea",
	"isStudent" : false,
	"favouriteSubjects" : [
		"PE",
		"ComputerScience"
	],
	"totalSpentInBooks" : 0
}
{
	"_id" : ObjectId("60c786c7ea3b0060957f8b0a"),
	"firstName" : "Winter",
	// lastName 프로퍼티가 사라진것 확인가능 
	"email" : "summerbell@email.com",
	"gender" : "M",
	"country" : "Republic of Korea",
	"isStudent" : false,
	"favouriteSubjects" : [
		"PE",
		"ComputerScience"
	],
	"totalSpentInBooks" : 0
}
```

4. delete

````text
// id 목록 확인 
> db.student.find({}, {_id: 1}).pretty()
{ "_id" : ObjectId("60c78412ea3b0060957f8b09") }
{ "_id" : ObjectId("60c786c7ea3b0060957f8b0a") }

// delete 쿼리
> db.student.deleteOne({_id: ObjectId("60c78412ea3b0060957f8b09")})

// 결과 
{ "acknowledged" : true, "deletedCount" : 1 }

// 다시 확인해보면 삭제된 것 확인 
> db.student.find({}, {_id: 1}).pretty()
{ "_id" : ObjectId("60c786c7ea3b0060957f8b0a") }
````







