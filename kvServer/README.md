# KV Server

Distributed server implementation for the Key-Value Store with Trie-based indexing and fault-tolerant replication.

## Prerequisites

    Java 17 or higher
    Maven 3.6 or higher

## Building

From the genData directory:

```bash
mvn clean install
```
Or from the project root:

```bash
mvn clean install -pl kvServer
```
## Usage

```bash
mvn exec:java -Dexec.args="-a <address> -p <port>"
```


| Parameter | Description                            | Example    | 
|-----------|----------------------------------------|------------|
| -a        | IP address to bind to                  | localhost  | 
| -p        | Port to listen on                      | 8000       | 

For fault tolerance, run multiple server instances on different ports

## Supported Operations

PUT - Store key-value pairs with nested data

GET - Retrieve top-level keys

DELETE - Remove keys from storage

QUERY - Access nested values using dot notation

COMPUTE - Execute mathematical operations on stored values

## Error Handling 

Error Handling


The server returns:

OK - Operation successful

ERROR - Syntax error or malformed query

NOT FOUND - Key doesn't exist