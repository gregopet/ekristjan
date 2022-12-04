ALTER TABLE departure
    ADD cancelled_at TIMESTAMP WITH TIME ZONE,
    ADD cancelled_by_teacher_id INTEGER REFERENCES teacher(teacher_id);

COMMENT ON COLUMN departure.cancelled_at IS 'If the departure was later invalidated, the date at which it was cancelled is given here. Serves as the discriminator for invalid departures';
COMMENT ON COLUMN departure.cancelled_by_teacher_id IS 'If the departure was later invalidated, the teacher who invalidated it is given here';


DROP INDEX idx_departure_pupil;
CREATE INDEX idx_departure_pupil ON departure(pupil_id, time) WHERE cancelled_at IS NULL;