# --- Second database schema

# --- !Ups

create table page (
  id		  				bigint not null primary key auto_increment,
  title						varchar(255) not null,
  status					varchar(15),
  slug                      text,
  content                   longtext,
  description               text,
  keywords                  text
);

# --- !Downs

drop table if exists page;
