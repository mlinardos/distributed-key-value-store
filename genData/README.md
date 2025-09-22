# KV Data Generator

Test data generator for the Distributed Key-Value Store project. Generates syntactically correct nested key-value data for testing the distributed database.

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
mvn clean install -pl genData
```
## Usage
```bash
mvn exec:java -Dexec.args="-k <keyFile> -n <lines> -d <depth> -l <length> -m <keys>"
```
| Parameter | Description                            | Example     | 
|-----------|----------------------------------------|-------------|
| -k    | Path to key definition file            | keyFile.txt | 
| -n    | Number of data lines to generate       | 1000        | 
| -d    | Maximum nesting depth (0 = no nesting) | 3           | 
| -l    | Maximum string length                  | 4           |
| -m    | Maximum keys per value                 | 5           |

## Key file Format

Create a space-separated file with key names and types:
name string
age int
height float
street string
level int