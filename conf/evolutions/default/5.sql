# --- Update to Project table

# --- !Ups

ALTER TABLE project ADD intro longtext;
ALTER TABLE project ADD dates varchar(255);

# --- !Downs

ALTER TABLE project DROP intro;
ALTER TABLE project DROP dates;
