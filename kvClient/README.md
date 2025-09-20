# io.github.mlinardos.kvstore.client.kvClient Application

In order to compile the io.github.mlinardos.kvstore.client.kvClient application, the use of maven is necessary.
To download Maven please visit the https://maven.apache.org/ website and follow the instructions to download it and install it there.



## Compiling

Compile the project

```bash
  cd [path]\io.github.mlinardos.kvstore.client.kvClient
  mvn clean install # to compile every file from the start and erase the target folder
  #The compiled .class files will be placed at the ./target/classes folder
```

## Run Locally

You can

```bash
  # do not cd to other folders

  mvn exec:java -Dexec.mainClass="io.github.mlinardos.kvstore.client.kvClient" -Dexec.args="-s <keyFile> -i <datafile> -k <replication-factor>"
```