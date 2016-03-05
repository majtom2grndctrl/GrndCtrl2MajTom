# --- Update to Project table

# --- !Ups

ALTER TABLE project ADD slug varchar(255);
ALTER TABLE project ADD indepth longtext;

# --- !Downs

ALTER TABLE project DROP slug;
ALTER TABLE project DROP indepth;
