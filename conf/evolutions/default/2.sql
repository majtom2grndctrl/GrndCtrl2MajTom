# --- First database schema

# --- !Ups

create table project (
  id		  				bigint not null primary key auto_increment,
  title						varchar(255) not null,
  filename        varchar(255),
  status					varchar(32) default 'public',
  roles           varchar(255) not null,
  tools           varchar(255) not null,
  techstack       varchar(255) not null,
  about           text

);

# --- !Downs

drop table if exists project;
