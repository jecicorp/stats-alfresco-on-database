# Jeci - Stats Alfresco on Database

## Description / Features

This software is made to perform some statistics on [Alfresco](http://alfresco.com) Database.

Tried with Alfresco Enterprise 4.1, 4.2 and 5.0 (Might works with community version) but only works with MySQL.

Currently only one functionality was developed : "Alfresco Disk Usage"

### Alfresco Disk Usage

This tool prints size of directories in Alfresco.

* First we need to load data from the databases. (This may take a while.)
* Then you would browse directories

#### Screen-shots

![Print Childs Nodes size](http://jeci.fr/blog/jeci-saod/captures/2016-03-22_print.png)

#### Problems

* This version considers files with thumbnails are folders.
* Problem loading sql file inside war application, for the moment we need sql files readable on disk

## Install

Simply copie war file in webapps

``` bash
gradle clean war
cp /srv/pitaya-share/webapps/jeci-saod-0.?.?.war ${tomact.home}/webapps/
```

## Configuration

Create a "application.properties" file in `${tomcat.home}/shared/config/` or `${jetty.home}/resources/config/`

``` properties
alfresco.datasource.jdbcUrl=jdbc:mysql://localhost/alfresco
alfresco.datasource.driverClassName=com.mysql.jdbc.Driver
alfresco.datasource.username=alfresco
alfresco.datasource.password=alfresco
alfresco.datasource.connectionTestQuery=SELECT 1


sql.alfresco.query_path_folder=@SOURCE_DIR@/src/main/resources/sql/mysql/alfresco41
sql.local.query_path_folder=@SOURCE_DIR@/src/main/resources/sql/hsqldb/localdb

local.datasource.jdbcUrl=jdbc:hsqldb:file:@VAR_DIR@/sqldb/local;shutdown=false
local.datasource.driverClassName=org.hsqldb.jdbc.JDBCDriver
local.datasource.connectionTestQuery=SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS

## Uncomment if probem with jetty
#endpoints.jmx.unique-names=true
#endpoints.jmx.enabled=false
```

I have a problem loading sql file inside war application, for the moment we need source folder :(

We use a local [HSQLDB](http://hsqldb.org/) to store data localy. See `@VAR_DIR@/sqldb/local`

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
