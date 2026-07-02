-- Row-Level Security for concept_content
-- Ensures each student can only read their own AI-generated concept explanations.
-- The Spring Boot service sets app.student_id as a session variable before queries
-- that originate from authenticated endpoints.

ALTER TABLE concept_content ENABLE ROW LEVEL SECURITY;

-- Drop and recreate so this script is idempotent on re-run
DROP POLICY IF EXISTS concept_content_student_isolation ON concept_content;

-- Students may only SELECT rows where student_id matches the session variable.
-- INSERT/UPDATE/DELETE are performed by the service role which bypasses RLS.
CREATE POLICY concept_content_student_isolation
    ON concept_content
    FOR SELECT
    USING (
        student_id = NULLIF(current_setting('app.student_id', true), '')::bigint
    );
