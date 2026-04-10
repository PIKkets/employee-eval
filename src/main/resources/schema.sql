-- ============================================================
-- 테이블 생성 (DROP은 DatabaseInitConfig.java에서 처리)
-- ============================================================
CREATE TABLE dbo.DEPARTMENT (
    id   BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL
);

CREATE TABLE dbo.EMPLOYEE (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    department_id BIGINT,
    login_id      NVARCHAR(50)  NOT NULL CONSTRAINT UQ_EVAL_EMP_LOGIN UNIQUE,
    password      NVARCHAR(255) NOT NULL,
    name          NVARCHAR(50)  NOT NULL,
    email         NVARCHAR(100) NOT NULL,
    position      NVARCHAR(50),
    is_leader     BIT           NOT NULL DEFAULT 0,
    role          NVARCHAR(20)  NOT NULL DEFAULT 'USER',
    total_score   FLOAT,
    final_grade   NVARCHAR(5),
    failed_attempts INT         NOT NULL DEFAULT 0,
    is_locked     BIT           NOT NULL DEFAULT 0,
    status        NVARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT FK_EVAL_EMP_DEPT FOREIGN KEY (department_id) REFERENCES dbo.DEPARTMENT(id) ON DELETE SET NULL
);

CREATE TABLE dbo.EVALUATION_ELEMENT (
    id        BIGINT IDENTITY(1,1) PRIMARY KEY,
    eval_type NVARCHAR(20)  NOT NULL,
    name      NVARCHAR(100) NOT NULL,
    weight    INT           NOT NULL
);

CREATE TABLE dbo.EVALUATOR_MAPPING (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    evaluatee_id BIGINT       NOT NULL,
    evaluator_id BIGINT       NOT NULL,
    eval_type    NVARCHAR(20) NOT NULL,
    is_anonymous BIT          NOT NULL DEFAULT 0,
    CONSTRAINT FK_EVAL_MAP_EVALUATEE FOREIGN KEY (evaluatee_id) REFERENCES dbo.EMPLOYEE(id),
    CONSTRAINT FK_EVAL_MAP_EVALUATOR FOREIGN KEY (evaluator_id) REFERENCES dbo.EMPLOYEE(id)
);

CREATE TABLE dbo.EVALUATION_SCORE (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    mapping_id BIGINT        NOT NULL,
    element_id BIGINT        NOT NULL,
    score      INT           NOT NULL,
    comment    NVARCHAR(MAX),
    CONSTRAINT FK_EVAL_SCORE_MAP  FOREIGN KEY (mapping_id) REFERENCES dbo.EVALUATOR_MAPPING(id) ON DELETE CASCADE,
    CONSTRAINT FK_EVAL_SCORE_ELEM FOREIGN KEY (element_id) REFERENCES dbo.EVALUATION_ELEMENT(id)
);
