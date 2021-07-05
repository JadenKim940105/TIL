# 테코톡#4 DNS

---

해당 포스팅은   
우아한Tech의 테크토크 [DNS - 동글, 라면](https://www.youtube.com/watch?v=5rBzHoR4F2A)   
생활코딩의 [WEB2-Domain Name System](https://www.youtube.com/watch?v=zrqivQVj3JM&list=PLuHgQVnccGMCI75J-rC8yZSVGZq3gYsFp)   
영상을 참고하여 재구성 하였습니다.

---

## IP
컴퓨터간 통신을 위해서는 ip 주소가 필요하다. 마치 편지를 주고 받기 위해 주소를 알아야 하듯 말이다.  
그리고 네트워크 상에서 각각의 컴퓨터는 host 라고 칭한다.   
A-host 가 B-host 에게 메세지를 보내고 싶다면 A-host 는 B-host 의 ip 주소 (12.345.678.90)
로 메세지를 보내면 된다는 것이다. 그런데 이렇게 숫자의 조합으로 이루어진 ip 주소는 사용자가 기억하기에는 너무 어려운 일이다.  
그래서, ip 주소 대신 이름을 사용하면 어떨까라는 생각이 등장했을 것이다.  

모든 운영체제는 hosts 라는 파일을 가지고 있고 해당파일은 ip 와 대응하는 이름을 저장해두고 
사용자가 이름으로 메세지를 보내면 그에 대응하는 ip 를 반환해 해당 ip로 메세지를 보내게 된다. 

이런방식을 사용한다면 DNS 를 사용하지 않더라도 이름으로 원하는 ip 에 접속할 수 있게 된다. 

## before DNS 

앞서서 hosts 라는 파일을 통해 ip 를 이름으로 변환해 사용하는 방법을 알아보았다. 
하지만 hosts 파일은 개인의 컴퓨터에 저장되는 파일이고, 모두에게 공유되지 않는다.
즉, A 컴퓨터에서 aaa.com 은 1.2.3.4 로 접속이 되고 B 컴퓨터에서 aaa.com 은 5.6.7.8 로 접속이 될 수 있다는 뜻이다.  

개인이 관리하는 hosts 파일이 아닌 모든 컴퓨터가 공통으로 사용할 수 있는 hosts 파일의 
필요성이 대두되었고 Stanford Research Institute(SRI) 가 그 역할을 수행하게 된다. 

개인은 이제 SRI 로부터 hosts 파일을 다운받아 사용하면 공통된 hosts 파일을 사용하게 될 수 있게 된 것이다.

하지만 이는 금방 한계에 부딪치게 되었다. SRI 는 계속해서 늘어나는 서버들을 일일이 관리하기 힘들었고,
사용자는 hosts 파일을 지속적으로 업데이트 하기위해 다시 다운로드 받아야 하며, 
하나의 파일에 모든 'ip - 이름' 정보를 관리하는 일 자체가 한계가 된 것이다.

이런 한계가 DNS 의 탄생배경이 된 것이다. 

## DNS?
DNS 란 **_Domain Name System_** 의 줄임말이다.

DNS 의 동작방식은 hosts 파일을 사용하는 방식과 유사하다.  
1. 만약 1.2.3.4 라는 ip 주소를 가진 컴퓨터의 도메인 주소를 example.com 으로 설정하고 싶다면, 
Domain Name System(DNS) Server 에 요청을 보낸다.
     
2. DNS Server 가 요청을 받아 들였다.
     
3. 다른 사용자가 example.com 에 접속할 때 우선 hosts 파일에 해당 도메인이 있는지 확인을 하고 없다면
   DNS Server 에 해당 도메인의 ip 주소를 요청하고 응답받은 ip 로 접속할 수 있게 된다. 
   

## Public DNS 

그런데 의문점이 하나있다. 사용자는 어떻게 DNS 서버를 알고 그곳으로 요청을 보내는 것일까? 
우리는 인터넷을 사용하기 위해 통신사(Internet Service Provider - ISP)를 통해 사용한다.
바로 그런 통신사들이 사용자의 컴퓨터가 도메인에 해당하는 ip 를 요청할때 해당 요청을 특정 DNS server 로
보낼 수 있는 매커니즘을 갖추고 있다.  
그렇다고 통신사들이 제공하는 DNS server 를 사용해야 할 필요는 없다. 다양한 public DNS 가 존재하고 
DNS 설정에 들어가서 변경만 하면 원하는 DNS server 를 사용할 수 있다.

## DNS Internal (도메인 이름의 구조)

DNS server 의 역할은 크게 2가지로 나눠볼 수 있다.
1. 서버측에서 보낸 ip 와 이름을 등록해 주는것 
2. 클라이언측에서 이름으로 요청을 보내면 ip를 응답하는 것 

그런데 전세계에 DNS server 1대가 전세계의 모든 이런 요청들을 처리할 순 없다. 
많은 DNS server 가 통합되어 이러한 일들을 수행하는데 어떻게 그것이 가능한 것일까?

우선 도메인 주소를 다시 한번 살펴보자.  
blog.example.com.(마지막의 . 은 보통 생략된다.)

이 주소는 부분부분으로 나눌 수 있는데 다음과 같다.
![DNSname](https://github.com/JadenKim940105/TIL/blob/master/techtalk/img/dnsname.png)

각각의 부분들에는 각각의 부분을 전담하는 독자적인 서버가 존재한다.
그리고 상위 서버는 직속 하위 서버들의 목록과 ip주소를 알고 있어야한다.  
(Root 서버는 Top-level 서버 목록을, Top-level 서버는 second-level 서버 목록을
second-level 은 sub 서버 목록을 알고 있다.)

blog.example.com. 의 ip 주소를 요청하는 과정은 다음과 같다.

1. 사용자는 root 서버에 blog.example.com. 의 ip 주소를 요청한다.
2. root 서버는 com 을 전담하는 서버의 ip 를 반환한다. 
3. 사용자는 top-lever(com) 서버에 blog.example.com. 의 ip 주소를 요청한다.
3. top-level(com) 서버는 example 을 전담하는 서버의 ip를 반환한다. 
4. 사용자는 second-level(example) 서버에 blog.example.com. 의 ip 주소를 요청한다 
5. second-level(example) 서버는 blog 를 전담하는 서버의 ip를 반환한다.
6. 사용자는 sub(blog) 서버에 blog.example.com. 의 ip 주소를 요청한다.
7. sub 서버는 blog.example.com. 의 ip 주소를 반환한다.

이런 구조를 가지고 있기 때문에 수많은 DNS server 들이 통합되어 수많은 요청을 처리하게 될 수 있는 것이다. 






