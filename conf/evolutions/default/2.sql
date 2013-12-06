# --- First database schema

# --- !Ups

create table project (
  id		  				bigint not null primary key auto_increment,
  title						varchar(255) not null,
  status					varchar(32) default 'public',
  style                     varchar(32),
  author					bigint not null,
  published					date,
  slug                      text,
  description               text,
  keywords                  text,
  url                       text,
  roles                     text,
  foreign key(author) references user(id) on delete cascade
);

# --- !Downs

drop table if exists project;
