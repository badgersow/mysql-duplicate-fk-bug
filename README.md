### Reproduction of a duplicate FK bug for MySQL Connector

#### Background

There was a known problem when MySQL Connector JDBC Driver returned duplicated information about the foreign keys.
[This issue](https://bugs.mysql.com/bug.php?id=95280) has been closed as fixed in [the version 8.0.26](https://dev.mysql.com/doc/relnotes/connector-j/8.0/en/news-8-0-26.html).
I was still able to reproduce this issue with MySQL 8.0.26. This repository contains the sample data and test to reproduce the problem.

#### Steps to reproduce

1. Spin up MySQL Instance on local machine using Docker
```
$ docker run --name some-mysql -e MYSQL_ROOT_PASSWORD='Mysql123!' -p 3306:3306 -d mysql:8.0.23
```

2. Connect to the running database

```
$ docker exec -it some-mysql mysql -uroot -pMysql123!
```

3. Create the necessary database and tables
```
CREATE DATABASE IF NOT EXISTS dbdemo;

USE dbdemo;

CREATE TABLE IF NOT EXISTS `AO_9412A1_AOUSER` (
`CREATED` datetime DEFAULT NULL,
`ID` bigint NOT NULL AUTO_INCREMENT,
`LAST_READ_NOTIFICATION_ID` bigint DEFAULT '0',
`TASK_ORDERING` longtext COLLATE utf8mb4_bin,
`UPDATED` datetime DEFAULT NULL,
`USERNAME` varchar(255) COLLATE utf8mb4_bin NOT NULL,
PRIMARY KEY (`ID`),
UNIQUE KEY `U_AO_9412A1_AOUSER_USERNAME` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `AO_9412A1_USER_APP_LINK` (
`APPLICATION_LINK_ID` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
`AUTH_VERIFIED` tinyint(1) DEFAULT NULL,
`CREATED` datetime DEFAULT NULL,
`ID` bigint NOT NULL AUTO_INCREMENT,
`UPDATED` datetime DEFAULT NULL,
`USER_ID` bigint DEFAULT NULL,
PRIMARY KEY (`ID`),
KEY `fk_ao_9412a1_user_app_link_user_id` (`USER_ID`),
KEY `index_ao_9412a1_use643533071` (`APPLICATION_LINK_ID`),
CONSTRAINT `fk_ao_9412a1_user_app_link_user_id` FOREIGN KEY (`USER_ID`) REFERENCES `AO_9412A1_AOUSER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
```

4. Run the unit tests
```
$ mvn test
```

#### More info

The following code works for Connector version 8.0.25, but fails for 8.0.26. Something has been regressed.
```
$ mvn test -Dmysql.connector.version=8.0.25 # PASS
$ mvn test -Dmysql.connector.version=8.0.26 # FAIL
```