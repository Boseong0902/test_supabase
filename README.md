# 주요 기능
- 회원 가입
- 로그인
- 회원 정보 설정
- 웹소켓으로 실시간 채팅

  ## 회원가입 & 로그인
    - 회원가입 시, Auth.users에 해당 계정 생성 -> 이 스키마는 supabase에서 auth와 자동 연동시키기 위해 자동으로 생성해준 스키마
    - 허나 우린 해당 유저에 room_id 컬럼을 추가해야되므로 새 스키마 필요 -> public.users
    - 따라서 이 둘을 연동시키는게 spring server
    - 회원가입과 로그인 후, 대시보드 페이지로 들어가서 정보 입력하면 해당 정보대로 public.users에 유저 정보 생성됨
    - 추 후, 이 정보를 기반으로 RLS설정 예정

  ## public.rooms
    - 이 테이블은 메시지 페이지에서 채팅방 생성/참가를 해야 해당 채팅방 레코드가 생성됨
    - 그 레코드의 메시지 칼럼은 실시간으로 반영되고,
    - 디비에 반영된 변경사항은 웹소켓을 통해 해당 레코드를 구독하고있는 클라이언트에게 바로 반영됨


# 주요 코드

## 1. React 클라이언트 - Supabase 연동

### Supabase 클라이언트 설정
- `react-client/src/supabaseClient.js`: Supabase 클라이언트 초기화 및 설정

### 로그인 및 토큰 관리
- `react-client/src/Login.js`: Supabase Auth로 로그인 → access_token 추출 → Spring 서버로 토큰 전달

### 대시보드에서 사용자 정보 업데이트
- `react-client/src/Dashboard.js`: 현재 세션에서 토큰 가져와서 Spring 서버로 사용자 정보 업데이트 요청

### 실시간 채팅 - Realtime 구독
- `react-client/src/pages/Message.js`: Supabase Realtime 채널 구독으로 DB 변경사항 실시간 수신

## 2. Spring Boot 백엔드 - Supabase 연동

### Supabase 서비스 클래스
- `src/main/java/com/example/supabase/service/SupabaseService.java`: Supabase Auth API로 토큰 검증 및 사용자 정보 조회

### 사용자 컨트롤러 - 토큰 검증
- `src/main/java/com/example/supabase/controller/userController.java`: Authorization 헤더 검증 → 토큰에서 access_token 추출 → Supabase Auth 서버에서 사용자 정보 조회 → public.users 테이블에 사용자 정보 저장/업데이트

### 채팅방 컨트롤러
- `src/main/java/com/example/supabase/controller/roomController.java`: 방 생성/참가, 메시지 업데이트 (Realtime 트리거)

## 3. Supabase Realtime 설정

### Realtime Publication 자동 설정
- `src/main/java/com/example/supabase/startup/RealtimePublicationInitializer.java`: 앱 시작 시 public.rooms 테이블을 supabase_realtime publication에 자동 추가

## 4. 통신 흐름도

### 인증 흐름
```
React → Supabase Auth → access_token → Spring Server → Supabase Auth API → public.users 테이블
```

### 실시간 채팅 흐름
```
React → Spring Server → PostgreSQL → Supabase Realtime → React (실시간 업데이트)
```

### 데이터베이스 스키마
- `auth.users`: Supabase Auth가 관리하는 사용자 테이블
- `public.users`: 애플리케이션에서 관리하는 사용자 정보 (name, room_id 등)
- `public.rooms`: 채팅방 및 메시지 정보 (Realtime 구독 대상)

# 주요 로직
[회원가입]

React → Supabase SDK → auth.users

---

[로그인]

React
1. Supabase SDK
2. access_token 발급
3. React
4. Spring 서버로 토큰 전달
5. Spring 서버가 토큰 검증
6. Supabase Auth 서버에서 유저 정보(user id, email 등) 요청
7. Spring 서버가 public.users 테이블에 유저 정보 저장

---

[Realtime 로직]
1. 사용자가 room_id 입력
2. react → Spring Server(Post /rooms/join)
3. 서버가 DB에서 해당 room존재 여부 확인
4. 없으면 새 room 레코드 생성 후 반환, 존재하면 그대로 해당 레코드 반환
5. Spring -(json)→ React
6. realtime 구독 시작
   <Realtime으로 채팅은 사용자 계정과는 무관하게 작동 - 아직 RLS설정 전이라>


