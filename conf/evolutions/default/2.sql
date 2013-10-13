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

insert into page values (
  null,
  "Home",
  "published",
  "/",
  "<p>Welcome to your new website. You can edit this content by logging into the content manager.</p>",
  "Write a summary of what your website is about",
  "metadata keywords"
);

insert into page values (
  null,
  "Contact",
  "published",
  "contact",
  "<p>Use this page to let visitors know how to contact you.</p>",
  "Write a summary of what this page is about",
  "contact"
);


# --- !Downs

drop table if exists page;
