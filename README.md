# Minor Planet Center Loader #

## Overview ##
This java program provides a way to load the Minor Planet Catalogues provided by the Minor Planets Center into a database.

Currently there are sql scripts and dependencies set up for PostgreSql. Pull requests for other databases are welcome.

## Setup ##

At the moment there are no prebuilt distribution packages available so you will need to build the tool your self. It is not difficult just follow the steps below.

First clone the code and build it.
```
cd /tmp
git clone git@github.com:wselwood/MPCLoader.git
cd MPCLoader
./gradlew distTar
```

At this point you will need to use the sql script in MPCLoader/sql/postgresql-install.sql to create the table in your database.
Next you need to unpack the distribution package that you just built.

```
cd <Install directory>
tar -xvzf /tmp/MPCLoader/build/distributions/MinorPlanetLoader-1.0.tar
cd MinorPlanetLoader-1.0.tar
```

Now set up your connection details in the connection.properties file

Finally you can run the program by calling

```bin/MinorPlanetLoader <path to MPCORB.DAT.gz>```

## Minor Planets Center ##

The data files this is designed to load are created by the minor planets center and can be found at the following two locations:
http://www.minorplanetcenter.net/iau/MPCORB.html
http://www.minorplanetcenter.net/iau/ECS/MPCAT/MPCAT.html