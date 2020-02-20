create sequence if not exists hibernate_sequence start with 1 increment by 1;

create table if not exists file_entity (id bigint not null, file_body blob, file_name varchar(255), source_file_body blob, date date, primary key (id));