CREATE TABLE school(
    school_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name TEXT UNIQUE
);
COMMENT ON TABLE school IS 'A school with pupils & teachers';
COMMENT ON COLUMN school.name IS 'Name of the school';

CREATE TABLE pupil(
    pupil_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    school_id INTEGER NOT NULL REFERENCES school(school_id),
    name TEXT NOT NULL,
    clazz TEXT NOT NULL,
    leaves_alone BOOLEAN NOT NULL,
    leave_mon TIME WITHOUT TIME ZONE,
    leave_tue TIME WITHOUT TIME ZONE,
    leave_wed TIME WITHOUT TIME ZONE,
    leave_thu TIME WITHOUT TIME ZONE,
    leave_fri TIME WITHOUT TIME ZONE
);
COMMENT ON TABLE pupil IS 'A pupil whose departures we are tracking';
COMMENT ON COLUMN pupil.school_id IS 'School this pupil is from';
COMMENT ON COLUMN pupil.name IS 'Pupil''s name';
COMMENT ON COLUMN pupil.clazz IS 'The class this pupil belongs to (class is a reserved word in languages, thus clazz)';
COMMENT ON COLUMN pupil.leaves_alone IS 'Can this pupil leave school on their own?';
COMMENT ON COLUMN pupil.leave_mon IS 'The time at which this pupil will leave school every monday';
COMMENT ON COLUMN pupil.leave_tue IS 'The time at which this pupil will leave school every tuesday';
COMMENT ON COLUMN pupil.leave_wed IS 'The time at which this pupil will leave school every wednesday';
COMMENT ON COLUMN pupil.leave_thu IS 'The time at which this pupil will leave school every thursday';
COMMENT ON COLUMN pupil.leave_fri IS 'The time at which this pupil will leave school every friday';

CREATE TABLE teacher(
    teacher_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    school_id INTEGER NOT NULL REFERENCES school(school_id),
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    password_hash TEXT,
    password_last_attempt TIMESTAMP WITH TIME ZONE,
    password_failed_attempts INTEGER NOT NULL DEFAULT 0
);
COMMENT ON TABLE teacher IS 'A teacher authorized to supervise children';
COMMENT ON COLUMN teacher.school_id IS 'School this teacher is from';
COMMENT ON COLUMN teacher.name IS 'The name of this school';
COMMENT ON COLUMN teacher.email IS 'The teacher''s email (used for password resets)';
COMMENT ON COLUMN teacher.password_hash IS 'Hashed password of this teacher';
COMMENT ON COLUMN teacher.password_last_attempt IS 'Time at which the last password change was attempted';
COMMENT ON COLUMN teacher.password_failed_attempts IS 'Number of times wrong password was entered in current ''session'' (application defined)';

CREATE TABLE registered_device (
    registered_device_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    last_teacher_id INTEGER NOT NULL REFERENCES teacher(teacher_id),
    user_agent TEXT,
    push_endpoint TEXT UNIQUE,
    push_auth TEXT,
    push_p256dh TEXT,
    classes TEXT[] NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp
);
COMMENT ON TABLE registered_device IS 'A device registered to receive notifications, ';
COMMENT ON COLUMN registered_device.last_teacher_id IS 'The teacher most recently logged into this device';
COMMENT ON COLUMN registered_device.user_agent IS 'User-Agent of the device that can be used to identify its type';
COMMENT ON COLUMN registered_device.push_endpoint IS 'The endpoint to which a push notification must be made. It is unique but can possibly be null for devices that don''t support push';
COMMENT ON COLUMN registered_device.push_auth IS 'The push notification authentication secret';
COMMENT ON COLUMN registered_device.push_p256dh IS 'The push notification authentication public key';
COMMENT ON COLUMN registered_device.classes IS 'The classes this device wants to receive notifications for';
COMMENT ON COLUMN registered_device.created_at IS 'The date on which this device was first seen';

CREATE TABLE summon(
    summon_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    pupil_id INTEGER NOT NULL REFERENCES pupil(pupil_id),
    teacher_id INTEGER NOT NULL REFERENCES teacher(teacher_id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp
);
COMMENT ON TABLE summon IS 'A request for the pupil to come to the door';
COMMENT ON COLUMN summon.pupil_id IS 'Pupil that was summoned';
COMMENT ON COLUMN summon.teacher_id IS 'Teacher who issued the summon';
COMMENT ON COLUMN summon.created_at IS 'Time at which the summon was triggered';

CREATE TABLE summon_ack(
    summon_ack_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    summon_id INTEGER NOT NULL REFERENCES summon(summon_id),
    teacher_id INTEGER NOT NULL REFERENCES teacher(teacher_id),
    time TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_summon_ack_summon ON summon_ack(summon_id);
COMMENT ON COLUMN summon_ack.summon_id IS 'The summon that was acknowledged';
COMMENT ON COLUMN summon_ack.teacher_id IS 'Teacher who acknowledged the summon';
COMMENT ON COLUMN summon_ack.time IS 'Time at which the summon was acknowledged';

CREATE TABLE extraordinary_departure(
    extraordinary_departure_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    pupil_id INTEGER NOT NULL REFERENCES pupil(pupil_id),
    teacher_id INTEGER REFERENCES teacher(teacher_id),
    date DATE NOT NULL,
    time TIME NOT NULL,
    leaves_alone BOOLEAN,
    remark TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    unique(date, pupil_id)
);
CREATE INDEX idx_extraordinary_departure_pupil ON extraordinary_departure(pupil_id, date);
COMMENT ON TABLE extraordinary_departure IS 'A planned departure at a non-standard time about which the teacher must be notified. Guaranteed there is only one extraordinary departure per day!';
COMMENT ON COLUMN extraordinary_departure.pupil_id IS 'The pupil who will leave at a non-standard time';
COMMENT ON COLUMN extraordinary_departure.teacher_id IS 'The teacher who recorded the non-standard departure';
COMMENT ON COLUMN extraordinary_departure.date IS 'The day on which the student is planned to leave';
COMMENT ON COLUMN extraordinary_departure.time IS 'The time at which the student is planned to leave';
COMMENT ON COLUMN extraordinary_departure.leaves_alone IS 'Is the pupil allowed to leave alone (without a parent fetching them)? Overrides the pupil''s setting.';
COMMENT ON COLUMN extraordinary_departure.remark IS 'Any special remarks about this departure?';

CREATE TABLE departure(
    departure_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    pupil_id INTEGER NOT NULL REFERENCES pupil(pupil_id),
    teacher_id INTEGER REFERENCES teacher(teacher_id),
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    entire_day BOOLEAN NOT NULL DEFAULT false,
    remark TEXT
);
CREATE INDEX idx_departure_pupil on departure(pupil_id, time);
COMMENT ON TABLE departure IS 'A record of when a pupil had left school';
COMMENT ON COLUMN departure.pupil_id IS 'The pupil who left';
COMMENT ON COLUMN departure.teacher_id IS 'The teacher confirming the kid has left the school (if any)';
COMMENT ON COLUMN departure.time IS 'The time at which the kid has left school';
COMMENT ON COLUMN departure.entire_day IS 'Did the pupil miss the entire day, i.e. did not come to school at all?';
COMMENT ON COLUMN departure.remark IS 'Any extra remarks about the departure (e.g. sick that day, on vacation)';

CREATE TABLE password_reset(
    password_reset_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    teacher_id INTEGER NOT NULL REFERENCES teacher(teacher_id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    token TEXT NOT NULL,
    spent BOOLEAN NOT NULL DEFAULT false
);
COMMENT ON TABLE password_reset IS 'A password reset request';
COMMENT ON COLUMN password_reset.teacher_id IS 'The teacher who requested the password reset';
COMMENT ON COLUMN password_reset.created_at IS 'The time at which the password reset was issued';
COMMENT ON COLUMN password_reset.token IS 'The secret token that needs to match for the password to be changed';
COMMENT ON COLUMN password_reset.spent IS 'Was this token spent (and is thus not valid anymore)?';