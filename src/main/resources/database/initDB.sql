
create table if not exists client
(
    id    serial primary key,
    name  varchar(200) not null,
    email varchar(254) not null unique,
    phone varchar(50) not null unique
);

alter table client
    owner to "user";

create table if not exists timetable
(
    id        serial primary key,
    date      date    not null,
    time      time    not null,
    client_id integer not null,
    UNIQUE (date, time, client_id),
    foreign key (client_id) references client(id) on delete cascade
);

alter table timetable
    owner to "user";

