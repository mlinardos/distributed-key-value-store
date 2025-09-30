# Distributed Key-Value Store

A fault-tolerant, distributed key-value database implementing:
- **Trie-based indexing** for efficient key lookups
- **K-replication** for fault tolerance
- **Nested data structures** with arbitrary depth
- **Query operations** with path navigation (e.g., `person.address.street`)
- **Mathematical computations** on stored values

## Architecture

- **kvClient**: Coordinates requests across multiple servers with configurable replication
- **kvServer**: Stores data in-memory using custom Trie implementation
- **genData**: Generates synthetic test data with configurable nesting and complexity

## Prerequisites

- Docker
- Docker Compose (optional)
- Maven (if you are not using Docker)

## Quick Start

### 1. Generate Test Data

#### 1.1 Create a key definition file (keyFile.txt) , if not already present in the genData folder, in the format:
```name string
age int
address object
street string
city string 
```
#### 1.2 Build  data generator image

```bash
docker build -f genData/Dockerfile -t kv-gendata:latest .
```

#### 1.3 Run the data generator container to create dataToIndex.txt

```bash
docker run -v $(pwd):/workspace kv-gendata:latest \
  -k genData/keyFile.txt -n 1000 -d 5 -l 5 -m 5
```

Parameters:

-k : Key definition file (key names and types)

-n : Number of records to generate (1000)

-d : Maximum nesting depth (5 levels)

-l : Maximum string length (5 characters)

-m : Maximum keys per nesting level (5 keys)


### 2. Start KV Servers

#### 2.1 Build the server image

```bash
docker build -f kvServer/Dockerfile -t kv-server:latest .
```
#### 2.2 Start 3 servers on different ports
```bash
docker run -d -p 8000:8000 --rm --name kvserver1 kv-server:latest -a 0.0.0.0 -p 8000
```
```bash
docker run -d -p 8002:8002 --rm --name kvserver2 kv-server:latest -a 0.0.0.0 -p 8002
```
```bash
docker run -d -p 8003:8003 --rm --name kvserver3 kv-server:latest -a 0.0.0.0 -p 8003
```

### 3. Run KV Client

#### 3.1 Create configuration files

Create serverFile.txt with server addresses(not localhost but the Docker host IP):

172.17.0.1 8000

172.17.0.1 8002

172.17.0.1 8003

### 3.2 Build the client image:

```bash
docker build -f kvClient/Dockerfile -t kv-client:latest .
```

#### 3.3 Run the client container:
```bash
docker run -it -v $(pwd):/workspace kv-client:latest \
-s serverFile.txt -i dataToIndex.txt -k 2
```
Parameters:

-s : Server configuration file

-i : Data file to index

-k : Replication factor (data stored on k servers)

## Alternatively with docker-compose

A `docker-compose.yml` file is provided for easier setup. It will start 3 servers and the client.

## 1. Generate test data

### 1.1 Create a key definition file (keyFile.txt) as described above.

### 1.2 Build and run the data generator:

```bash 
docker-compose --profile tools run --rm gendata
```

## 2. Start all servers
```bash
docker-compose up -d
```

In the serverFile.txt, replace localhost the IPs with

kvserver1 8000

kvserver2 8002

kvserver3 8003

## 3 Run the client
```bash
docker-compose --profile client run --rm kvclient
```
## 4. Stopping and Cleaning Up
To stop and remove all containers, run:
```bash
docker-compose down
```

## 5. Starting client and servers together
```bash
docker-compose --profile client up
```

## 6. Accessing Logs
### All servers
```bash 
docker-compose logs -f
```
### Specific server
```bash
docker-compose logs -f kvserver1
```

## Alternatively with maven (without Docker)
# Refer to the individual README files in kvClient, kvServer, and genData directories for detailed instructions on building and running each component using Maven.