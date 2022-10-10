-- password: test
insert into teacher(name, email, password_hash, school_id)
values ('Učitelj Jože', 'uciteljjoze@example.org', '$argon2i$v=19$m=65536,t=10,p=1$JYJq9hWxJob0lGOJsoxP4g$Qj+IfKiprIEKjF4Xlwz2qsnZsVyDPrb6j9Vklyh4YSM', (select school_id from school limit 1));