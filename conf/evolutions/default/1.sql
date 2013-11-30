# --- First database schema

# --- !Ups

create table user (
  id                        bigint not null primary key auto_increment,
  email                     varchar(255) not null,
  firstName                 varchar(255) not null,
  lastName                  varchar(255) not null,
  password                  varchar(255) not null
);

create table blogpost (
  id		  				bigint not null primary key auto_increment,
  title						varchar(255) not null,
  status					varchar(32) default 'public',
  style                     varchar(32),
  author					bigint not null,
  published					date,
  slug                      text,
  content                   longtext,
  description               text,
  keywords                  text,
  foreign key(author)	references user(id) on delete cascade
);

# --- !Downs

drop table if exists blogpost;
drop table if exists user;
