-- User 테이블 데이터 (5명의 사용자)
INSERT INTO user_tb (username, password, email, role, created_at) VALUES
                                                                ('admin', '1234', 'manager@world.com','ADMIN',   NOW()),
                                                                ('traveler', '1234', 'paris@nate.com', 'USER',  NOW()),
                                                                ('chef_kim', '1234', 'cook@gmail.com', 'USER', NOW()),
                                                                ('movie_fan', '1234','cinema@naver.com','USER',   NOW()),
                                                                ('health_king', '1234', 'muscle@daum.net','USER',   NOW());

-- admin (ID: 1)가 작성한 공지 및 가이드 (3개)
INSERT INTO board_tb (title, content, premium, user_id, created_at) VALUES
                                                               ('커뮤니티 이용 가이드', '우리 커뮤니티는 서로를 존중하는 문화를 지향합니다. 비방글은 자제해주세요.', false, 1, NOW()),
                                                               ('이달의 베스트 작성자 이벤트', '매달 활동 점수가 높은 분들께 소정의 선물을 드립니다.', true, 1, NOW()),
                                                               ('시스템 정기 점검 안내', '이번 주 일요일 새벽 2시부터 4시까지 서버 점검이 있을 예정입니다.', false, 1, NOW());

-- traveler (ID: 2)가 작성한 여행 관련 글 (3개)
INSERT INTO board_tb (title, content, premium, user_id, created_at) VALUES
                                                               ('제주도 한 달 살기 후기', '진정한 힐링이 무엇인지 느끼고 왔습니다. 숙소 선정 팁 공유합니다.', true, 2, NOW()),
                                                               ('일본 교토 벚꽃 명소 추천', '내년 봄 여행을 준비하신다면 교토의 이 장소들을 꼭 가보세요.', false, 2, NOW()),
                                                               ('혼자 떠나는 배낭여행 필수템', '보조배터리와 압축 파우치는 정말 필수 중의 필수입니다.', true, 2, NOW());

-- chef_kim (ID: 3)이 작성한 요리 관련 글 (2개)
INSERT INTO board_tb (title, content, premium, user_id,created_at) VALUES
                                                               ('초간단 5분 김치볶음밥 레시피', '자취생들을 위한 최고의 한 끼! 굴소스 한 스푼이 비법입니다.', true, 3, NOW()),
                                                               ('에어프라이어로 만드는 스테이크', '겉바속촉 스테이크, 온도 조절만 잘하면 집에서도 가능합니다.', false, 3, NOW());

-- movie_fan (ID: 4)이 작성한 영화 관련 글 (1개)
INSERT INTO board_tb (title, content, premium, user_id, created_at) VALUES
    ('올해 꼭 봐야 할 인생 영화 TOP 3', '감동과 여운이 가시지 않는 명작들만 골라봤습니다.', false, 4, NOW());

-- health_king (ID: 5)이 작성한 운동 관련 글 (1개)
INSERT INTO board_tb (title, content, premium, user_id, created_at) VALUES
    ('퇴근 후 오운완! 직장인 운동 루틴', '시간 없는 직장인들을 위한 고효율 전신 운동법입니다.', true, 5, NOW());

-- 댓글 테이블 데이터 (각 게시글에 댓글들을 추가)
-- 1번 게시글 (커뮤니티 가이드) 댓글
INSERT INTO reply_tb (comment, board_id, user_id, created_at) VALUES
                                                                  ('가이드 확인했습니다. 클린한 게시판 만들어요!', 1, 2, NOW()),
                                                                  ('운영자님 고생이 많으십니다.', 1, 3, NOW());

-- 4번 게시글 (제주도 한 달 살기) 댓글
INSERT INTO reply_tb (comment, board_id, user_id, created_at) VALUES
                                                                  ('부럽네요.. 저도 퇴사하고 가고 싶어요.', 4, 1, NOW()),
                                                                  ('숙소 정보 쪽지로 좀 부탁드려도 될까요?', 4, 3, NOW()),
                                                                  ('제주도는 언제 가도 참 좋은 것 같아요.', 4, 4, NOW());

-- 7번 게시글 (김치볶음밥 레시피) 댓글
INSERT INTO reply_tb (comment, board_id, user_id, created_at) VALUES
                                                                  ('오늘 저녁은 이걸로 결정했습니다!', 7, 2, NOW()),
                                                                  ('굴소스가 진짜 신의 한 수네요 ㅎㅎ', 7, 4, NOW()),
                                                                  ('요리 초보인데 따라하기 쉬워서 좋네요.', 7, 5, NOW());

-- 9번 게시글 (인생 영화 TOP 3) 댓글
INSERT INTO reply_tb (comment, board_id, user_id, created_at) VALUES
                                                                  ('여기에 나온 영화 다 봤는데 진짜 명작이죠.', 9, 1, NOW()),
                                                                  ('두 번째 영화는 아직 안 봤는데 이번 주말에 봐야겠어요.', 9, 2, NOW()),
                                                                  ('저는 개인적으로 멜로 장르도 추천받고 싶어요!', 9, 5, NOW());

-- 10번 게시글 (직장인 운동 루틴) 댓글
INSERT INTO reply_tb (comment, board_id, user_id, created_at) VALUES
                                                                  ('운동 가기 싫었는데 이 글 보고 헬스장 갑니다.', 10, 2, NOW()),
                                                                  ('루틴이 정말 알차네요. 감사합니다!', 10, 3, NOW()),
                                                                  ('단백질 쉐이크는 어떤 거 드시나요?', 10, 4, NOW());