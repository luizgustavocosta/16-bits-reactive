## Installing PostgreSQL

### Install via Homebrew

``brew install postgresql``

### start via brew

``brew services start postgresql``

### stop via brew

```brew services stop postgresql```

Useful links
https://stackoverflow.com/questions/15301826/psql-fatal-role-postgres-does-not-exist
https://blog.testdouble.com/posts/2021-01-28-how-to-completely-uninstall-homebrew-postgres/

## Database table

```sql
-- Table: public.customers

-- DROP TABLE public.customers;

CREATE TABLE IF NOT EXISTS public.customers
(
    id             integer NOT NULL DEFAULT nextval('customers_id_seq'::regclass),
    name           character varying(25) COLLATE pg_catalog."default" NOT NULL,
    middlename     character varying(25) COLLATE pg_catalog."default",
    lastname       character varying(25) COLLATE pg_catalog."default",
    becamecustomer date,
    CONSTRAINT "primary" PRIMARY KEY (id)
) TABLESPACE pg_default;

ALTER TABLE public.customers
    OWNER to luizcosta;
```