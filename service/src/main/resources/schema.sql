create table if not exists users(
id serial primary key,
uuid uuid,
password varchar,
phone_number varchar,
passport_number varchar
);