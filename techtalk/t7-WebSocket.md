# 테코톡#7 WebSocket

---

해당 포스팅은 우아한Tech의 테크토크 [Web Sokcet - 코일](https://www.youtube.com/watch?v=MPQHvwPxDUw) 영상을 참고하여 재구성 하였습니다.

---

## 웹 소켓이란?

웹 소켓이란 두 **_프로그램 간의 메시지를 교환하기 위한 통신 방법_** 중 하나이다.  
![웹 소켓](https://github.com/JadenKim940105/TIL/blob/master/techtalk/img/websocket1.png)  

현재 인터넷 환경에서 많이 사용되는 기술이다.
## 웹 소켓의 특징
```text
1. 양방향 통신 (Full-Duplex)
 - 데이터 송수신을 동시에 처리할 수 있는 통신방법
 - 클라이언트와 서버가 서로에게 원할 때 데이터를 주고 받을 수 있다.
 - 통산적인 Http 통신은 Client 가 욫처을 보내는 경우에만 Server 가 응답하는 단반향 통신

2. 실시간 네트워킁 (Real Time-Networking) 
 - 웹 환경에서 연속된 데이터를 빠르게 노출 ex) 채팅, 주식, 비디오 데이터
 - 여러 단말기에 빠르게 데이터를 교환 
```
## 웹 소켓 이전의 실시간 통신
```text
Polling 
서버로 일정 주기로 요청을 보내 송신 받는방법.
real-time 통신에서는 언제 통신이 발생할지 예측이 불가능 하기 때문에 일정주기로 계속 request 를
보내는 방법이다. -> 불필요한 request 와 connection 을 생성

Long Polling 
서버에 요청을 보내고 이벤트가 생겨 응답 받을 떄 까지 연결 종료 x 응답 받으면 끊고 다시 재요청 

Streaming 
서버에 요청을 보내고 끊기지 않은 연결상태에서 끊임없이 데이터를 수신한다. 
-> 클라이언트에서 서버로의 데이터 송신이 어렵다. 

=> 위의 모든 방법이 HTTP 를 통해 통신하기 때문에 Request, Response 둘 다 Header 가 불필요하게 크다. 
```

## 웹 소켓의 동작 방법
```text
1. Hand Shaking 
핸드쉐이킹은 http(s) 프로토콜을 통해 이루어진다. 
```
![websocket 요청](https://github.com/JadenKim940105/TIL/blob/master/techtalk/img/websockethandshake.png)  
````text
REQUEST 
Host : 웹 소켓 서버의 주소 
Upgrade : 현재 클라이언트-서버 전송 프로토콜 연결에서 다른 포로토콜로 업그레이드 또는 변경하기 위한 규칙
Connection : Upgrade 헤더 필드가 명시되었을 경우 송신자는 반드시 Upgrade 옵션을 지정한 Connection 헤더 필드도 전송 
Sec-Websocket-Key : 길이가 16바이트인 임의로 선택된 숫자를 base64로 인코딩한 값 (클라이언트, 서버가 서로간 신원확인)
Origin : 클라이언트로 웹 브라우저를 사용하는 경우에 필수 항목으로, 클라이언트의 주소 
Sec-Websocket-Protocol : 여러 서브프로토콜을 의미, 공백문자로 구분되며 순서에 따른 우선권 부여
Sec-Websocket-Version : 웹 소켓 버전정보 

RESPONSE 
101 Switching protocol 가 response 로 오면 웹소켓이 연결되었다는 상태 
Sec-Websocket-Accept : 클라이언트로 받은 웹소켓 키를 사용하여 계산된 값 
````

```text
2. 데이터 전송 
Handshake 과정이 끝나면 ws(s) 프로토콜로 변경(upgrade) 된다. 
데이터 전송 단위는 message 이며 message 는 여러 frame 이 모여서 구성하는 하나의 논리적 메세지 단위이다.
frame 은 communication 에서 가장 작은 단위의 데이터로 작은 헤더 + payload 로 구성되어있다. 

웹소켓 통신에 사용되는 데이터는 UTF8 인코딩 된 데이터만 지원한다. 
ex) 0x00 (보내고 싶은 데이터) 0xff

3. 연결종료
Close frame 을 주고 받으며 연결 종료 
```
![요약](https://github.com/JadenKim940105/TIL/blob/master/techtalk/img/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202021-07-13%20%EC%98%A4%EC%A0%84%2010.45.20.png)  

## 웹 소켓 프로토콜의 특징
```text
1. 최초 접속에서만 http 프로토콜 위에서 handshaking 을 위해 http header 를 사용한다.

2. 웹소켓을 위한 별도의 포트는 없으면, 기존 포트 (http-80, https-443) 을 사용한다.

3. 프레임으로 구성된 메세지라는 논리적 단위로 송수신

4. 메세지에 포함될 수 있는 교환 가능한 메세지는 텍스트와 바이너리
```
## 웹 소켓 한계
```text
1. 웹 소켓은 HTML5 이후에 나온 기술로 HTML5 이전의 기술로 구현된 서비스는 Socket.io, SockJS 를 사용하여
웹 소켓처럼 사용할 수 있도록 도와주는 기술을 사용해야한다.
   
2. 웹 소켓은 문자열들을 주고 받을 수 있을 뿐 그 이상의 일 X -> 주고 받은 문자열의 해독은 온전히 어플리케이션에 맡긴다.

3. HTTP는 형식이 있기 때문에 형식을 따르면 해석이 쉽다. 하지만 웹 소켓은 형식이 정해져 있지 않기 떄문에
   어플리케이션에서 쉽게 해석하기 힘들다 -> sub-protocol 을 사용해 메세지의 형태를 약속  
```

### STOMP(Simple Text Oriented Message Protocol)
```text
STOMP 는 채팅 통신을 하기 위한 형식을 정의 
HTTP 와 유사하게 정의되어 해석하기 편한 프로토콜
웹 소켓의 sub-protocol 로 많이 사용되는 프로토콜이다.

프레임 기반의 프로토콜로 프레임은 명령, 헤더, 바디로 구성되어 있다. 
자주 사용되는 명령은 CONNECT, SEND, SUBSCRIBE, DISCONNECT 등이 있다.
헤더와 바디는 빈 라인으로 구분하며, 바디의 끝은 NULL 문자로 설정한다.
```




