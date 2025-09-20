# io.github.mlinardos.kvstore.server.kvServer Application

In order to compile the io.github.mlinardos.kvstore.server.kvServer application, the use of maven is necessary.
To download Maven please visit the https://maven.apache.org/ website and follow the instructions to download it and install it there.



## Compiling

Compile the project

```bash
  cd [path]\io.github.mlinardos.kvstore.server.kvServer
  mvn clean install # to compile every file from the start and erase the target folder
  #The compiled .class files will be placed at the ./target/classes folder
```

## Run Locally

Run the project


```bash
  # do not cd to other folders

  mvn exec:java -Dexec.mainClass="io.github.mlinardos.kvstore.server.kvServer" -Dexec.args="-a <ip> -p  <port>"
```