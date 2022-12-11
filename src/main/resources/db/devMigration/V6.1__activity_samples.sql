INSERT INTO activity(name) values ('Lovljenje polžev'), ('Lupljenje solate'), ('Hlajenje čaja');

INSERT INTO pupil_activity(pupil_id, activity_id)
select
    (select pupil_id from pupil where pupil.given_name || ' ' || pupil.family_name like p),
    (select activity_id from activity where name like a)
from ( VALUES
   ('Jana Vanič', 'Lovljenje polžev'),
   ('Jana Vanič', 'Lupljenje solate'),
   ('Gašper Žbogar', 'Hlajenje čaja'),
   ('Klemen Kotnik', 'Lovljenje polžev'),
   ('Klemen Kotnik', 'Hlajenje čaja'),
   ('Anita Borštnar', 'Lovljenje polžev'),
   ('Anita Borštnar', 'Hlajenje čaja'),
   ('Anita Borštnar', 'Lupljenje solate'),
   ('Klara Cetkin', 'Hlajenje čaja'),
   ('Karel Maks', 'Lovljenje polžev'),
   ('Karel Maks', 'Lupljenje solate'),
   ('Ivar Svenson', 'Hlajenje čaja')
) AS mp(p, a);
