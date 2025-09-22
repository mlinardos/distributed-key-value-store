# KV Client
Client application for the Distributed Key-Value Store. Handles data indexing, replication, and query operations across multiple servers.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Building
From the kvClient directory:
```bash
mvn clean install
```

## Run Locally

You can

```bash
  # do not cd to other folders

  mvn exec:java -Dexec.mainClass="io.github.mlinardos.kvstore.client.kvClient" -Dexec.args="-s <keyFile> -i <datafile> -k <replication-factor>"
```