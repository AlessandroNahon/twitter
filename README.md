# Nature & Animals (CURRENTLY WORKING)
This project will collect tweets from the specified users using the Twitter API. After that, Google Cloud Vision API, Google Cloud NLP and Stanford Core NLP will analyze both text and images from those Tweets and classify them into a Database

## How does it work?

This project uses both Twitter API, Google Cloud API and Stanford Core NLP in order to gather, analyze, parse and store Tweets, Images and replies from any user's timeline of Twitter. This project is used for a research in the Ottawa's university, but is customizable and you can modify the algorithm to do your own research about communication and the economy of the words in Twitter. Once the application is running, you'll access to it in the following link:

```
https://localhost:<Port>
```
Where you'll find the Dashboard. Here you can see the result of the analysis of the last search. I if is the first time you open the app, you'll probably won't have any
infomation available, so go to the tab ***Search*** and configure your search

## Config

This application uses a MySQL database, the Google Cloud Services and the Twitter API services, so you'll need to set this parameters correctly before running the app. First thing you need is to create the application.yml file, or similar, and write into it the next configuration:
```
server:
  port: <The port where you want to run your application>

spring:
  datasource:
    url: jdbc:mysql://localhost:<Port of your database>/<Name of your database>?useSSL=false
    username: <DatabaseUser>
    password: <DatabasePassword>
    driverClassName: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
    ddl-auto: validate
    naming:
      physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    database-platform: org.hibernate.dialect.MySQL5Dialect
    database: mysql
    show-sql: false

BEARER_TOKEN: <Twitter developer account bearer token>
```

To configure the Database as it was originally, you can execute this code:

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

Here you can configure the parameters. The most important one is the BEARER_TOKEN
> BEARER_TOKEN: Auth token from the Twitter Developer Account

In order to get access to the google cloud services, is important to have the google cloud CLI installed in your computer. After that, create a developer account and activate both Google Cloud NLP and Google Cloud Vision API, then, create a service account with owner permission and generate a auth key file.
> Information about accounts here: https://cloud.google.com/iam/docs/service-accounts?hl=es_419

Once you have your auth key file, set in a safe place, and authenticate yourself with the following commands in cmd/bash
```
gcloud auth activate-service-account <Name of the service account> --key-file=<Absolute path to the auth key file>

gcloud config set project <Name of the current project>

set GOOGLE_APPLICATION_CREDENTIALS=<Absolute path to the auth key file>

gcloud auth application-default login
```
The last step will redirect you to the Google login page, where you'll have to login your Google Developer account. Once you've done this, you're ready to run the application
