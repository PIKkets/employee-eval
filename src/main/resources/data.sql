-- Dummy Data for Bootstrap

-- Departments
INSERT INTO DEPARTMENT (name) VALUES ('기획팀'), ('개발1팀'), ('개발2팀'), ('마케팅팀'), ('인사팀');

-- Employees
INSERT INTO EMPLOYEE (department_id, login_id, password, name, email, position, is_leader, role) VALUES 
(5, 'admin', '{noop}1234', '관리자', 'admin@eval.com', '총괄', 0, 'ADMIN'),
-- 기획팀 (dept 1): emp001 리더
(1, 'emp001', '{noop}1234', '박지훈', 'jihoon.park@eval.com', '팀장', 1, 'USER'),
(1, 'emp002', '{noop}1234', '김민지', 'minji.kim@eval.com', '과장', 0, 'USER'),
(1, 'emp003', '{noop}1234', '최영수', 'youngsoo.choi@eval.com', '사원', 0, 'USER'),
-- 개발1팀 (dept 2): emp004 리더
(2, 'emp004', '{noop}1234', '정재윤', 'jaeyoon.jeong@eval.com', '팀장', 1, 'USER'),
(2, 'emp005', '{noop}1234', '이수진', 'sujin.lee@eval.com', '과장', 0, 'USER'),
(2, 'emp006', '{noop}1234', '박태현', 'taehyun.park@eval.com', '대리', 0, 'USER'),
-- 개발2팀 (dept 3): emp007 리더
(3, 'emp007', '{noop}1234', '김철수', 'chulsoo.kim@eval.com', '팀장', 1, 'USER'),
(3, 'emp008', '{noop}1234', '이영희', 'younghee.lee@eval.com', '대리', 0, 'USER'),
(3, 'emp009', '{noop}1234', '윤도현', 'dohyun.yoon@eval.com', '사원', 0, 'USER'),
-- 마케팅팀 (dept 4): emp010 리더
(4, 'emp010', '{noop}1234', '강지영', 'jiyoung.kang@eval.com', '팀장', 1, 'USER'),
(4, 'emp011', '{noop}1234', '송승헌', 'seungheon.song@eval.com', '차장', 0, 'USER'),
(4, 'emp012', '{noop}1234', '오나미', 'nami.oh@eval.com', '대리', 0, 'USER'),
-- 인사팀 (dept 5): emp013 리더
(5, 'emp013', '{noop}1234', '장동건', 'donggun.jang@eval.com', '팀장', 1, 'USER'),
(5, 'emp014', '{noop}1234', '김태희', 'taehee.kim@eval.com', '사원', 0, 'USER'),
(1, 'emp015', '{noop}1234', '유재석', 'jaesuk.yoo@eval.com', '부장', 0, 'USER'),
(1, 'emp016', '{noop}1234', '이효리', 'hyori.lee@eval.com', '차장', 0, 'USER'),
(1, 'emp017', '{noop}1234', '아이유', 'iu@eval.com', '신입', 0, 'USER'),
(2, 'emp018', '{noop}1234', '박명수', 'myungsoo.park@eval.com', '차장', 0, 'USER'),
(2, 'emp019', '{noop}1234', '정준하', 'junha.jung@eval.com', '과장', 0, 'USER'),
(2, 'emp020', '{noop}1234', '노홍철', 'hongchul.noh@eval.com', '대리', 0, 'USER'),
(3, 'emp021', '{noop}1234', '정형돈', 'hyungdon.jung@eval.com', '과장', 0, 'USER'),
(3, 'emp022', '{noop}1234', '하하', 'haha@eval.com', '대리', 0, 'USER'),
(3, 'emp023', '{noop}1234', '길', 'gil@eval.com', '사원', 0, 'USER'),
(4, 'emp024', '{noop}1234', '황광희', 'kwanghee.hwang@eval.com', '대리', 0, 'USER'),
(4, 'emp025', '{noop}1234', '조세호', 'seho.cho@eval.com', '사원', 0, 'USER'),
(4, 'emp026', '{noop}1234', '남창희', 'changhee.nam@eval.com', '신입', 0, 'USER'),
(5, 'emp027', '{noop}1234', '전소민', 'somin.jeon@eval.com', '대리', 0, 'USER'),
(5, 'emp028', '{noop}1234', '양세찬', 'sechan.yang@eval.com', '사원', 0, 'USER'),
(5, 'emp029', '{noop}1234', '이광수', 'kwangsoo.lee@eval.com', '주임', 0, 'USER'),
(1, 'emp030', '{noop}1234', '유연석', 'yeonseok.yoo@eval.com', '사원', 0, 'USER');

-- Evaluation Elements
-- Performance (성과) Total 100%
INSERT INTO EVALUATION_ELEMENT (eval_type, name, weight) VALUES 
('PERFORMANCE', '목표 달성도', 40),
('PERFORMANCE', '업무 품질 및 정확도', 30),
('PERFORMANCE', '개선 사례 도출', 30);

-- Competency (역량) Total 100%
INSERT INTO EVALUATION_ELEMENT (eval_type, name, weight) VALUES 
('COMPETENCY', '직무 전문성', 40),
('COMPETENCY', '책임감 및 윤리의식', 30),
('COMPETENCY', '의사소통 및 협업', 30);

-- Peer (다면) Total 100%
INSERT INTO EVALUATION_ELEMENT (eval_type, name, weight) VALUES 
('PEER', '동료와의 협동성', 50),
('PEER', '긍정적 조직문화 기여도', 50);

-- Interview (면담) Total 100%
INSERT INTO EVALUATION_ELEMENT (eval_type, name, weight) VALUES 
('INTERVIEW', '장기적 비전 및 팀 일치도', 50),
('INTERVIEW', '면담 내용의 발전 가능성', 50);

-- Evaluator Mappings - 성과평가: 자가평가(본인→본인) + 리더평가(팅장→팀원)
-- 기획팀: 리더 emp001(id=2)
-- 자가평가: emp001, emp002, emp003, emp015, emp016, emp017, emp030
INSERT INTO EVALUATOR_MAPPING (evaluatee_id, evaluator_id, eval_type) VALUES 
-- 기획팀 본인 자가평가
(2, 2, 'PERFORMANCE'),
(3, 3, 'PERFORMANCE'),
(4, 4, 'PERFORMANCE'),
(16, 16, 'PERFORMANCE'),
(17, 17, 'PERFORMANCE'),
(18, 18, 'PERFORMANCE'),
(31, 31, 'PERFORMANCE'),
-- 기획팀 리더(emp001=id 2) 평가 대상 (팀원)
(3, 2, 'PERFORMANCE'),
(4, 2, 'PERFORMANCE'),
(16, 2, 'PERFORMANCE'),
(17, 2, 'PERFORMANCE'),
(18, 2, 'PERFORMANCE'),
(31, 2, 'PERFORMANCE'),
-- 개발1팀 자가평가: emp004~006, emp018~020
(5, 5, 'PERFORMANCE'),
(6, 6, 'PERFORMANCE'),
(7, 7, 'PERFORMANCE'),
(19, 19, 'PERFORMANCE'),
(20, 20, 'PERFORMANCE'),
(21, 21, 'PERFORMANCE'),
-- 개발1팀 리더(emp004=id 5) 평가
(6, 5, 'PERFORMANCE'),
(7, 5, 'PERFORMANCE'),
(19, 5, 'PERFORMANCE'),
(20, 5, 'PERFORMANCE'),
(21, 5, 'PERFORMANCE'),
-- 개발2팀 자가평가: emp007~009, emp021~023
(8, 8, 'PERFORMANCE'),
(9, 9, 'PERFORMANCE'),
(10, 10, 'PERFORMANCE'),
(22, 22, 'PERFORMANCE'),
(23, 23, 'PERFORMANCE'),
(24, 24, 'PERFORMANCE'),
-- 개발2팀 리더(emp007=id 8) 평가
(9, 8, 'PERFORMANCE'),
(10, 8, 'PERFORMANCE'),
(22, 8, 'PERFORMANCE'),
(23, 8, 'PERFORMANCE'),
(24, 8, 'PERFORMANCE'),
-- 마케팅팀 자가평가: emp010~012, emp024~026
(11, 11, 'PERFORMANCE'),
(12, 12, 'PERFORMANCE'),
(13, 13, 'PERFORMANCE'),
(25, 25, 'PERFORMANCE'),
(26, 26, 'PERFORMANCE'),
(27, 27, 'PERFORMANCE'),
-- 마케팅팀 리더(emp010=id 11) 평가
(12, 11, 'PERFORMANCE'),
(13, 11, 'PERFORMANCE'),
(25, 11, 'PERFORMANCE'),
(26, 11, 'PERFORMANCE'),
(27, 11, 'PERFORMANCE'),
-- 인사팀 자가평가: emp013~014, emp027~029
(14, 14, 'PERFORMANCE'),
(15, 15, 'PERFORMANCE'),
(28, 28, 'PERFORMANCE'),
(29, 29, 'PERFORMANCE'),
(30, 30, 'PERFORMANCE'),
-- 인사팀 리더(emp013=id 14) 평가
(15, 14, 'PERFORMANCE'),
(28, 14, 'PERFORMANCE'),
(29, 14, 'PERFORMANCE'),
(30, 14, 'PERFORMANCE');

-- ============================================================
-- COMPETENCY 역량 평가: 자가평가 + 리더 평가 (PERFORMANCE와 동일 구조)
-- ============================================================
INSERT INTO EVALUATOR_MAPPING (evaluatee_id, evaluator_id, eval_type) VALUES
-- 기획팀 자가평가 (emp001~003, emp015~017, emp030)
(2, 2, 'COMPETENCY'),
(3, 3, 'COMPETENCY'),
(4, 4, 'COMPETENCY'),
(16, 16, 'COMPETENCY'),
(17, 17, 'COMPETENCY'),
(18, 18, 'COMPETENCY'),
(31, 31, 'COMPETENCY'),
-- 기획팀 리더(emp001=id 2) 역량 평가
(3, 2, 'COMPETENCY'),
(4, 2, 'COMPETENCY'),
(16, 2, 'COMPETENCY'),
(17, 2, 'COMPETENCY'),
(18, 2, 'COMPETENCY'),
(31, 2, 'COMPETENCY'),
-- 개발1팀 자가평가 (emp004~006, emp018~020)
(5, 5, 'COMPETENCY'),
(6, 6, 'COMPETENCY'),
(7, 7, 'COMPETENCY'),
(19, 19, 'COMPETENCY'),
(20, 20, 'COMPETENCY'),
(21, 21, 'COMPETENCY'),
-- 개발1팀 리더(emp004=id 5) 역량 평가
(6, 5, 'COMPETENCY'),
(7, 5, 'COMPETENCY'),
(19, 5, 'COMPETENCY'),
(20, 5, 'COMPETENCY'),
(21, 5, 'COMPETENCY'),
-- 개발2팀 자가평가 (emp007~009, emp021~023)
(8, 8, 'COMPETENCY'),
(9, 9, 'COMPETENCY'),
(10, 10, 'COMPETENCY'),
(22, 22, 'COMPETENCY'),
(23, 23, 'COMPETENCY'),
(24, 24, 'COMPETENCY'),
-- 개발2팀 리더(emp007=id 8) 역량 평가
(9, 8, 'COMPETENCY'),
(10, 8, 'COMPETENCY'),
(22, 8, 'COMPETENCY'),
(23, 8, 'COMPETENCY'),
(24, 8, 'COMPETENCY'),
-- 마케팅팀 자가평가 (emp010~012, emp024~026)
(11, 11, 'COMPETENCY'),
(12, 12, 'COMPETENCY'),
(13, 13, 'COMPETENCY'),
(25, 25, 'COMPETENCY'),
(26, 26, 'COMPETENCY'),
(27, 27, 'COMPETENCY'),
-- 마케팅팀 리더(emp010=id 11) 역량 평가
(12, 11, 'COMPETENCY'),
(13, 11, 'COMPETENCY'),
(25, 11, 'COMPETENCY'),
(26, 11, 'COMPETENCY'),
(27, 11, 'COMPETENCY'),
-- 인사팀 자가평가 (emp013~014, emp027~029)
(14, 14, 'COMPETENCY'),
(15, 15, 'COMPETENCY'),
(28, 28, 'COMPETENCY'),
(29, 29, 'COMPETENCY'),
(30, 30, 'COMPETENCY'),
-- 인사팀 리더(emp013=id 14) 역량 평가
(15, 14, 'COMPETENCY'),
(28, 14, 'COMPETENCY'),
(29, 14, 'COMPETENCY'),
(30, 14, 'COMPETENCY');

-- Mark one employee as retired to test the status lockout
UPDATE dbo.EMPLOYEE SET status = 'RETIRED' WHERE login_id = 'emp030';
