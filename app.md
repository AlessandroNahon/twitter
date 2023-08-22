# Local Development Setup

## Requirements

- Java
  https://javadl.oracle.com/webapps/download/AutoDL?BundleId=248766_8c876547113c4e4aab3c868e9e0ec572

- MySQL 8.1.0
  https://dev.mysql.com/downloads/file/?id=520743

Check that Java and MySQL are installed by opening System Preferences on your Mac and noticing the icons

## Run MySQL in terminal

1. Open Terminal application in Mac

2. Run following command:

```
open .bashrc
```

3. Paste following code, save and close the file:

```
  export PATH=${PATH}:/usr/local/mysql/bin/
```

4. Start MySQL REPL and enter your password:

```
  mysql -u root -p
```

5. Paste the following commands:

```
create database twitter;

use twitter;

create table hibernate_sequence (next_val bigint) engine=MyISAM;
insert into hibernate_sequence values ( 1 );

create table hibernate_sequence (next_val bigint) engine=MyISAM;
insert into hibernate_sequence values ( 1 );
INSERT INTO twitter.hibernate_sequence (next_val) VALUES (0);
create table image (id bigint not null auto_increment, image longblob, image_content varchar(255), image_objects longtext, reply_id bigint, tweet_id bigint, primary key (id)) engine=MyISAM;
create table responses (id bigint not null, text longtext, text_sentiment varchar(255), original_tweet_id bigint, primary key (id)) engine=MyISAM;
create table sentiment (id bigint not null, appearances integer not null, sentiment varchar(255), primary key (id)) engine=MyISAM;
create table tweets (id bigint not null, conversation_id varchar(255), created_at datetime, possibly_sensitive bit not null, text longtext, text_sentiment varchar(255), username varchar(255), primary key (id)) engine=MyISAM;
create table words (id bigint not null, belongs_to varchar(255), count integer not null, syntax varchar(255), word varchar(255), primary key (id)) engine=MyISAM;
alter table image add constraint FK77q0ik93l0weslcgk9lmqblfv foreign key (reply_id) references responses (id);
alter table image add constraint FK4gwcxsdf0bla790cylb0ockg5 foreign key (tweet_id) references tweets (id);
alter table responses add constraint FKgdox8gbcm2xqb6hl0bs6q2shd foreign key (original_tweet_id) references tweets (id);

insert into twitter.sentiment (id,sentiment,appearances) values (1,'POSITIVE',0);
insert into twitter.sentiment (id,sentiment,appearances) values (2,'VERY_POSITIVE',0);
insert into twitter.sentiment (id,sentiment,appearances) values (3,'NEUTRAL',0);
insert into twitter.sentiment (id,sentiment,appearances) values (4,'NEGATIVE',0);
insert into twitter.sentiment (id,sentiment,appearances) values (5,'VERY_NEGATIVE',0);
```

6. Exit the MySQL REPL:

```
quit;
```

7. Import data into database:

```
mysql -u root -p -v twitter < ~/root/to/fileback.sql
```

## Run Application

8. Go to root of project folder:

```
cd to/project/folder
```

9. Run application with Java command:

```
java -debug -jar target/twitter-0.0.1-SNAPSHOT.jar
```

10. Application will be running on:

```
http://localhost:8083/
```
