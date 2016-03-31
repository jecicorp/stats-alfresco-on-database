# Jeci - Stats Alfresco on Database

## Description / Features

This software is made to perform some statistics on [Alfresco](http://alfresco.com) Database.

This software uses queries to perform statistics on content stored in Alfresco. We directly access sql database for performance reason. In consequence, this tool can works with offline server or sql backup dump. We don't need to access to disk "content store" or Solr indexes.

Currently only one functionality was developed : "Alfresco Disk Usage"

### Limitations

Tried with :

*   Alfresco Enterprise 4.1 - MySQL & Oracle
*   Alfresco Community 5.0 - MariaDB

### Dependencies

* Java 8
* oracle JDBCDriver

### Quick start

``` bash
git clone https://github.com/jeci-sarl/stats-alfresco-on-database.git
cd stats-alfresco-on-database
gradle clean build
cp src/test/resources/application-mysql.properties application.properties
vim application.properties
java -jar build/libs/jeci-saod-0.?.?.war
```

## Alfresco Disk Usage

This tool prints size of directories in Alfresco.

* First we need to load data from the databases. (This may take a while.)
* Then you would browse directories

#### Screen-shots

![Print Childs Nodes size](http://jeci.fr/blog/jeci-saod/captures/2016-03-22_print.png)

#### Problems

* This version considers files with thumbnails are folders.
* Problem loading sql file inside war application, for the moment we need sql files readable on disk

## Configuration

*  SAOF use [externalized configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) from Spring Boot Framework.

*   Use file from `src/test/resources/` as sample configuration

*   We use a local [HSQLDB](http://hsqldb.org/) to store data localy. See `@VAR_DIR@/sqldb/local`

*   You may occure probleme with jmx on jetty, add this parameters.

``` properties
endpoints.jmx.unique-names=true
endpoints.jmx.enabled=false
```

## Security

For the moment, grant access is hard code in `fr.jeci.alfresco.saod.SaodApplication.ApplicationSecurity` and `fr.jeci.alfresco.saod.SaodApplication.ApplicationSecurity`

Default users are :

 * `admin / admin`
 * `user / user`

## Performance

We tried computation script with big Alfresco Database (~500GB of office files) all compute takes 67 seconds on my computer.

## Future Plans

* Authentication based on Alfresco user local account
* Add support to Postgresql
* Add count of files
* Add REST interfaces to make javascript UI


## License

   Copyright 2016 [Jeci](http://jeci.fr) - Jérémie Lesage

   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

This work may reference software licensed under other open source licenses, please refer to these respective works for more information on license terms.
