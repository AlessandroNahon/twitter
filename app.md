# Local Development Setup

## Requirements

- Java
  https://javadl.oracle.com/webapps/download/AutoDL?BundleId=248766_8c876547113c4e4aab3c868e9e0ec572

- MySQL 8.1.0
  https://dev.mysql.com/downloads/file/?id=520743

Check that Java and MySQL are installed by opening System Preferences on your Mac and noticing the icons

## Create and populate MySQL database

1. Open Terminal application in Mac

2. Run following command:

```
open ~/.bashrc
```

3. Paste following code, save and close the file:

```
export PATH=${PATH}:/usr/local/mysql/bin/
```

4. Refresh terminal configurations

```
source ~/.bashrc
```

5. Go to project directory

6. Setup database by pasting the following MySQL command:

```
mysql -u root -p < twitter_db.sql
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

10. Application will start on http://localhost:8083/
