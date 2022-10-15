ALTER TABLE teacher
    DROP password_used_reset_identifiers,
    ADD password_reset_generation INT NOT NULL DEFAULT 0;

COMMENT ON COLUMN teacher.password_reset_generation IS 'A counter on the current generation of password resets (no older generations are valid)';