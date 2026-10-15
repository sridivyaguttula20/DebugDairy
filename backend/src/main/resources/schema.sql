CREATE TABLE IF NOT EXISTS code_analyses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    language VARCHAR(50) NOT NULL,
    submitted_code LONGTEXT NOT NULL,
    issue_title VARCHAR(255),
    explanation TEXT,
    corrected_code LONGTEXT,
    category VARCHAR(100),
    concept_to_revise VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    what_went_wrong TEXT,
    where_is_the_mistake TEXT,
    why_it_happened TEXT,
    how_to_correct_it TEXT
);
