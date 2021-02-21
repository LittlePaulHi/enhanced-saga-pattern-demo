create table product
(
    id    serial           not null primary key,
    title varchar(20)      not null,
    price double precision not null
);
