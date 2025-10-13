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
- 

# 주요 로직
[회원가입] 

React → Supabase SDK → auth.users

---

[로그인]   

React 
→ Supabase SDK 
→ access_token 발급 
→ React 
→ Spring 서버로 토큰 전달
→ Spring 서버가 토큰 검증
→ Supabase Auth 서버에서 유저 정보(user id, email 등) 요청
→ Spring 서버가 public.users 테이블에 유저 정보 저장

---

[Realtime 로직]
사용자가 room_id 입력 
→ react → Spring Server(Post /rooms/join) 
→ 서버가 DB에서 해당 room존재 여부 확인 
→ 없으면 새 room 레코드 생성 후 반환, 존재하면 그대로 해당 레코드 반환 
→ Spring -(json)→ React 
→ realtime 구독 시작
<Realtime으로 채팅은 사용자 계정과는 무관하게 작동 - 아직 RLS설정 전이라>


