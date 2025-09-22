# KV Data Generator

Test data generator for the Distributed Key-Value Store project. Generates syntactically correct nested key-value data for testing the distributed database.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Building

From the genData directory:
```bash
mvn clean install
```
Or from the project root:
```bash
mvn clean install -pl kvClient
```

## Usage
```bash
mvn exec:java -Dexec.args="-s <serverFile> -i <dataFile> -k <replication>"
```
| Parameter | Description                        | Example         | 
|-----------|------------------------------------|-----------------|
| **-s**    | Path to kServer configuration file | serverFile.txt  | 
| **-i**    | Data file to index                 | dataToIndex.txt | 
| **-k**    | Replication factor                 | 2               | 



## Server File Format

Create a space-separated file with server IPs and ports:

localhost 8000

localhost 8001

localhost 8002

192.168.1.10 9000

## Client Commands

Once running, the client accepts these commands:

### GET

Retrieve a top-level key:

> GET person1

> person1 -> [ name -> John | age -> 22 ]

### DELETE

Delete a key (requires all servers online):

> DELETE person1

> OK

### QUERY

Access nested values using dot notation:

> QUERY person2.address.street

> person2.address.street -> Panepistimiou

### COMPUTE

Perform calculations on stored values:

> COMPUTE 2*x WHERE x = QUERY person1.age

> 44

> COMPUTE x+y WHERE x = QUERY person1.age AND y = QUERY person3.height

> 23.75

# Fault Tolerance

With replication factor k=2, the system tolerates 1 server failure
Client warns if k or more servers are down
DELETE operations require all servers to be o