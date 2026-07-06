create table customers (
    id uuid primary key,
    name varchar(120) not null,
    email varchar(180) not null unique,
    document varchar(14) not null unique,
    status varchar(30) not null,
    created_at timestamp with time zone not null
);
