CREATE TABLE IF NOT EXISTS public.hotels
(
    id   integer auto_increment,
    name varchar(25),
    CONSTRAINT "pk_hotels" PRIMARY KEY (id)
);