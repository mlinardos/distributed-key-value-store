# io.github.mlinardos.kvstore.generator.genData Application

In order to compile the io.github.mlinardos.kvstore.generator.genData application, the use of maven is necessary.
To download Maven please visit the https://maven.apache.org/ website and follow the instructions to download it and install it there.



## Compiling

Compile the project

```bash
  cd [path]\io.github.mlinardos.kvstore.generator.genData
  mvn clean install # to compile every file from the start and erase the target folder
  #The compiled .class files will be placed at the ./target/classes folder
```

## Run Locally

Run the project

```bash
  cd ./target/classes
  # place the keyFile.txt in this folder if you dont want to define the full path
  java -cp . io.github.mlinardos.kvstore.generator.genData -k <keyFilepath> -n <number-of-lines> -d <max Level Of Nesting> -l <max Length Of Sting> -m <keys inside each value>
```

Alternativelly you can

```bash
  # do not cd to other folders

  mvn exec:java -Dexec.mainClass="io.github.mlinardos.kvstore.generator.genData" -Dexec.args="-k <keyFile> -n <number-of-lines> -d <max Level Of Nesting> -l <max Length Of Sting> -m <keys inside each value>"
```