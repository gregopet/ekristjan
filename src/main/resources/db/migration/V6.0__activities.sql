CREATE TABLE activity(
    activity_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name TEXT NOT NULL
);

COMMENT ON TABLE activity IS 'An after-school activity pupils may have on their schedule';
COMMENT ON COLUMN activity.name IS 'Name of the activity';

CREATE TABLE pupil_activity(
    pupil_id INTEGER NOT NULL REFERENCES pupil(pupil_id),
    activity_id INTEGER NOT NULL REFERENCES activity(activity_id),
    primary key (pupil_id, activity_id)
);

COMMENT ON TABLE pupil_activity IS 'The mapping between pupils and their activities';