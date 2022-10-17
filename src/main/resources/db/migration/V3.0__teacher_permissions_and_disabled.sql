ALTER TABLE teacher
    ADD enabled BOOLEAN NOT NULL DEFAULT true,
    ADD backoffice_access BOOLEAN NOT NULL DEFAULT false;

COMMENT ON COLUMN teacher.enabled IS 'If not true, the teacher cannot log in';
COMMENT ON COLUMN teacher.backoffice_access IS 'If true, the teacher may access the backoffice part of the application';