ALTER TABLE summon
    ADD cancelled_at TIMESTAMP WITH TIME ZONE,
    ADD cancelled_by_teacher_id INTEGER REFERENCES teacher(teacher_id);

COMMENT ON COLUMN summon.cancelled_at IS 'If the summon was later invalidated, the date at which it was cancelled is given here. Serves as the discriminator for invalid departures';
COMMENT ON COLUMN summon.cancelled_by_teacher_id IS 'If the summon was later invalidated, the teacher who invalidated it is given here';

CREATE INDEX idx_summon_pupil ON summon(pupil_id, created_at) WHERE cancelled_at IS NULL;