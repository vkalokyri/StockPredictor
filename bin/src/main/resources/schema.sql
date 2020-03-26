CREATE DATABASE IF NOT EXISTS stockpred;

USE stockpred;

CREATE TABLE IF NOT EXISTS users (
	id bigint AUTO_INCREMENT NOT NULL,
	userID varchar(50) NOT NULL,
	fname varchar(30) NOT NULL,
	lname varchar(30) NOT NULL,
	mname varchar(30) DEFAULT '',
	email varchar(200) NOT NULL,
	password varchar(255) NOT NULL,
	PRIMARY KEY (id)
) Engine=InnoDB;

INSERT INTO users (userID,fname,lname,mname,email,password) VALUES ('admin','Admin','Traitor','Is','admin@stockpred.com','adminpwd');

CREATE TABLE IF NOT EXISTS ticker (
	tsymbol varchar(10) NOT NULL,
	tname varchar(50) NOT NULL,
	PRIMARY KEY (tsymbol)
) Engine=InnoDB;

INSERT IGNORE INTO ticker (tsymbol,tname) VALUES ('GOOG','Google Inc.');
INSERT IGNORE INTO ticker (tsymbol,tname) VALUES ('AAPL','Apple Inc.');
INSERT IGNORE INTO ticker (tsymbol,tname) VALUES ('TSLA','Tesla Motors, Inc');
INSERT IGNORE INTO ticker (tsymbol,tname) VALUES ('YHOO','Yahoo! Inc.');
INSERT IGNORE INTO ticker (tsymbol,tname) VALUES ('FB','Facebook, Inc.');

CREATE TABLE IF NOT EXISTS histData (
	tsymbol varchar(10) NOT NULL,
	entryDate date NOT NULL,
	openPrice double,
	closePrice double,
	minPrice double,
	maxPrice double,
	volume bigint,
	PRIMARY KEY (tsymbol, entryDate),
	FOREIGN KEY (tsymbol) REFERENCES ticker(tsymbol)
) Engine=InnoDB;

CREATE TABLE IF NOT EXISTS instData (
	tsymbol varchar(10) NOT NULL,
	entryDate datetime NOT NULL,
	instPrice double,
	volume bigint,
	PRIMARY KEY (tsymbol, entryDate),
	FOREIGN KEY (tsymbol) REFERENCES ticker(tsymbol)
) Engine=InnoDB;