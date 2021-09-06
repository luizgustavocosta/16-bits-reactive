-- create table posts
-- (
--     id       uuid default random_uuid() not null primary key,
--     user     varchar(100),
--     content  varchar(256),
--     created_at TIMESTAMP WITH TIME ZONE
-- );
CREATE TABLE IF NOT EXISTS public.customers
(
    id             integer auto_increment,
    name           varchar(25),
    middlename     varchar(25),
    lastname       varchar(25),
    becamecustomer date,
    CONSTRAINT "pk_customers" PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.reservations
(
    id   integer auto_increment,
    name varchar(25),
    CONSTRAINT "pk_reservations" PRIMARY KEY (id)
);