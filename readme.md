# Jeci - Stats Alfresco on Database

[![Build Status](https://travis-ci.org/jeci-sarl/stats-alfresco-on-database.svg?branch=master)](https://travis-ci.org/jeci-sarl/stats-alfresco-on-database)

## Description / Features

This software is made to perform some statistics on [Alfresco](http://alfresco.com)
Database.

This software uses queries to perform statistics on content stored in Alfresco.
We directly access sql database for performance reason. In consequence, this tool
can works with offline server or sql backup dump. We don't need to access to disk
"content store" or Solr indexes.

### Features

These features are described below

* Alfresco Disk Usage
* Stores 
* Export 

### Limitations

Tried with :

*   Alfresco Enterprise 4.1 - MySQL & Oracle
*   Alfresco Community 5.0 - MariaDB/PostgreSQL

### Dependencies

*   Java 8
*   oracle JDBCDriver

### Quick start

#### Local Build

``` bash
git clone https://github.com/jeci-sarl/stats-alfresco-on-database.git
cd stats-alfresco-on-database
./gradlew clean build
cp src/test/resources/application-mysql.properties build/libs/application.properties
cd build/libs/
vim application.properties
java -jar jeci-saod-0.?.?.war
```

Then go to http://localhost:8080 user `admin` / `adm1n`

#### With Docker Compose

``` bash
git clone https://github.com/jeci-sarl/stats-alfresco-on-database.git
cd stats-alfresco-on-database
docker-compose -f docker/docker-compose.yml up --build -d
```

Then go to http://saod.docker.localhost/ user `admin` / `adm1n`


## Alfresco Disk Usage

This tool prints size of directories in Alfresco.

*   First we need to load data from the databases. (This may take a while.)
*   Then you would browse directories

#### Screen-shots

![Print Childs Nodes size](http://jeci.fr/blog/jeci-saod/captures/2016-03-22_print.png)

#### Problems

*   This version considers files with thumbnails are folders.
*   Problem loading sql file inside war application, for the moment we need sql files
readable on disk

## Stores

This tool permit to see all files and directories in your database.

#### MetaData 

* Node DB Id 
* NodeRef 
* Local Size 
* Aggregate Size 
* Full Size

## Export

This tool permit to download a CSV File with informations about all files and directories from the page where you are.

#### Download modes

* Files
* Directories
* Files & Directories

#### Data

Depend on the download mode you choose.

* Type
* Name
* Full Size
* Full path

## Configuration

*   SAOD use [externalized configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
from Spring Boot Framework.

*   Use file from `src/test/resources/` as sample configuration

*   We use a local [HSQLDB](http://hsqldb.org/) to store data localy. See `sqldb/local`

*   You may occure probleme with jmx on jetty, add this parameters.

```
endpoints.jmx.unique-names=true
endpoints.jmx.enabled=false
```

*   You can define port or context path with these parameters:

```
server.port=8787
server.contextPath=/saod
```


- You can define default sorting with this two parameters:

```
saod.sort.default=full
saod.sort.lang=fr
```

You can sort on the four columns : "name", "local", "aggregate", "full". default
is set to "none". For the sort "name" column, you can define the language to use
with `saod.sort.lang` parameter.


## Security

Security is perform with [Spring Security](http://projects.spring.io/spring-security/).
There is two roles `ROLE_ADMIN` and `ROLE_USER`.

*   `ROLE_ADMIN` can load date from Alfresco database
*   `ROLE_USER` can only read content.

Default users are :

 *   `admin / admin` with `ROLE_ADMIN`
 *   `user / user` with `ROLE_USER`

You can choose login and password for this default account with these parameters:

```
flyway.placeholders.admin.name=admin
flyway.placeholders.admin.password=adm1n
flyway.placeholders.user.name=user
flyway.placeholders.user.password=us3r
```

These parameters must be define before the first boot, or change directly in the
local HSQL database, for example using [DBeaver](http://dbeaver.jkiss.org/). Take
care to first stop application before editing the local database and shutdown
DBeaver before restart application.


## Performance

We tried computation script with big Alfresco Database (~500GB of office files) all compute takes 67 seconds on my computer.

## Future Plans

* Add support to Postgresql (done)
* UI "Alfresco Disk Usage"
    * Print "association name" instead of NodeRef when available
    * Print date of last update on alfresco
    * Add row ".." to go on parent folder(done)
    * Add number of file in folder and tree
    * Link in "Add count of files"
* New Component
    * "Alfresco Disk Usage" report as PDF
    * UI to create and manage local user
    * Add REST interfaces to make javascript UI
* Authentication based on Alfresco user local account
* Translation in French and other languages (with your help)
* Adding Unit Tests and Load Tests

## License

   Copyright 2016 [Jeci](http://jeci.fr) - Jérémie Lesage

   Licensed under the Apache License, Version 2.0 (the "License"); you may not use
   this file except in compliance with the License. You may obtain a copy of the
   License at

      http://www.apache.org/licenses/LICENSE-2.0

This work may reference software licensed under other open source licenses, please
refer to these respective works for more information on license terms.
