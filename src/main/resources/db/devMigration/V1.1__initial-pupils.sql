insert into school(name) values ('Ivana Roba');

insert into pupil(name, clazz, leaves_alone, leave_mon, leave_tue, leave_wed, leave_thu, leave_fri, school_id)
select a.*, '15:30', '16:20', '17:30', '13:00', (select school_id from school limit 1)
from ( values
('Jana Vanič', '1A', false, '16:20'::time without time zone),
('Gašper Žbogar', '1A', false, '15:30'::time without time zone),
('Klemen Kotnik', '1B', false, '16:20'::time without time zone),
('Anita Borštnar', '1B', false, '13:10'::time without time zone),
('Klara Cetkin', '2A', true, null),
('Karel Maks', '2A', false, '17:00'::time without time zone),
('Benito Harez', '2B', true, '16:20'::time without time zone),
('Ivar Svenson', '2B', true, null)
) as a;

-- password: test
insert into teacher(name, email, password_hash, school_id)
values ('Učiteljica Majda', 'ucitelj@example.org', '$argon2i$v=19$m=65536,t=10,p=1$JYJq9hWxJob0lGOJsoxP4g$Qj+IfKiprIEKjF4Xlwz2qsnZsVyDPrb6j9Vklyh4YSM', (select school_id from school limit 1));