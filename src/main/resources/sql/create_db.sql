DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS topics;
DROP TABLE IF EXISTS messages;

CREATE TABLE accounts (
username varchar(30) NOT NULL,
password varchar(30) DEFAULT '',
email varchar(50) NOT NULL,
sex ENUM('M', 'F'),
birthDate DATE,
PRIMARY KEY (username)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE authorities (
accountUsername varchar(30),
authority ENUM('ADMIN', 'USER') NOT NULL,
CONSTRAINT authorities_accounts_fk FOREIGN KEY(accountUsername)
REFERENCES accounts(username)
ON DELETE CASCADE
ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE topics (
id int(10) unsigned NOT NULL AUTO_INCREMENT,
title varchar(30) NOT NULL,
PRIMARY KEY (id),
UNIQUE KEY topics_pk (title)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE messages (
id int(10) unsigned NOT NULL AUTO_INCREMENT,
topicId int(10) NOT NULL,
accountUsername varchar(30),
date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
text TEXT NOT NULL,
PRIMARY KEY (id),
UNIQUE KEY id (id),
CONSTRAINT messages_accounts_fk FOREIGN KEY(accountUsername)
REFERENCES accounts(username)
ON DELETE CASCADE
ON UPDATE CASCADE,
CONSTRAINT messages_topic_fk FOREIGN KEY(topicId)
REFERENCES topics(id)
ON DELETE CASCADE
ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;