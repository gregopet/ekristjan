ALTER TABLE pupil
    ADD given_name TEXT,
    ADD family_name TEXT;
COMMENT ON COLUMN pupil.given_name IS 'First name of pupil';
COMMENT ON COLUMN pupil.family_name IS 'Last name of pupil';

-- Naive algorithm, any pre-existing pupils that don't follow the 'GivenName FamilyName' pattern will need to be checked
-- manually & have their names properly distributed between the new fields
UPDATE pupil
    SET given_name =  reverse(substr(reverse(name),strpos(reverse(name),' ') + 1)),
        family_name = reverse(substr(reverse(name),0,strpos(reverse(name),' ')));

ALTER TABLE pupil
    ALTER given_name SET NOT NULL,
    ALTER family_name SET NOT NULL,
    DROP name;
