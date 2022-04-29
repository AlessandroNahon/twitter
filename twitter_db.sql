create database twitter;

use twitter;

create table hibernate_sequence (next_val bigint) engine=MyISAM;
insert into hibernate_sequence values ( 1 );
INSERT INTO twitter.hibernate_sequence (next_val) VALUES (0);

create table image (id bigint not null auto_increment, image longblob, image_content varchar(255), reply_id bigint, tweet_id bigint, primary key (id)) engine=MyISAM;
create table responses (id bigint not null, text longtext, text_sentiment varchar(255), original_tweet_id bigint, primary key (id)) engine=MyISAM;
create table sentiment (id bigint not null, appearances integer not null, sentiment varchar(255), primary key (id)) engine=MyISAM;
create table tweets (id bigint not null, conversation_id varchar(255), created_at datetime, text longtext, text_sentiment varchar(255), username varchar(255), primary key (id)) engine=MyISAM;
create table words (id bigint not null, count integer not null, syntax varchar(255), word varchar(255), primary key (id)) engine=MyISAM;
alter table image add constraint FK77q0ik93l0weslcgk9lmqblfv foreign key (reply_id) references responses (id);
alter table image add constraint FK4gwcxsdf0bla790cylb0ockg5 foreign key (tweet_id) references tweets (id);
alter table responses add constraint FKgdox8gbcm2xqb6hl0bs6q2shd foreign key (original_tweet_id) references tweets (id);

insert into twitter.sentiment (id,sentiment,appearances) values (1,'POSITIVE',0);
insert into twitter.sentiment (id,sentiment,appearances) values (2,'VERY_POSITIVE',0);
insert into twitter.sentiment (id,sentiment,appearances) values (3,'NEUTRAL',0);
insert into twitter.sentiment (id,sentiment,appearances) values (4,'NEGATIVE',0);
insert into twitter.sentiment (id,sentiment,appearances) values (5,'VERY_NEGATIVE',0);