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

The `kvClient/serverFile.txt` file should contain the server addresses. Use `host.docker.internal` on Mac and Windows with Docker Desktop, or `172.17.0.1` on Linux:

host.docker.internal 8000

host.docker.internal 8002

host.docker.internal 8003

### 3.2 Build the client image:

```bash
docker build -f kvClient/Dockerfile -t kv-client:latest .
```

#### 3.3 Run the client container:
```bash
docker run -it -v $(pwd):/workspace kv-client:latest \
-s kvClient/serverFile.txt -i dataToIndex.txt -k 2
```
Parameters:

-s : Server configuration file

-i : Data file to index

-k : Replication factor (data stored on k servers)

## Alternatively with docker-compose

A `docker-compose.yml` file is provided for easier setup. It will start 3 servers and the client. Server addresses are pre-configured in `kvClient/serverFileCompose.txt` — no manual editing needed.

### 1. Generate test data

#### 1.1 Create a key definition file (keyFile.txt) as described above.

#### 1.2 Build and run the data generator:

```bash
docker compose --profile tools run --rm gendata
```

### 2. Start all servers
```bash
docker compose up -d
```

The client will automatically wait for all 3 servers to be ready before connecting.

### 3. Run the client
```bash
docker compose --profile client run --rm kvclient
```

### 4. Stopping and Cleaning Up
To stop and remove all containers, run:
```bash
docker compose down
```

### 5. Starting client and servers together
```bash
docker compose --profile client up
```

### 6. Accessing Logs
#### All servers
```bash
docker compose logs -f
```
#### Specific server
```bash
docker compose logs -f kvserver1
```

## Available Commands

Once the client is running and data is indexed, the following commands are available interactively.

> **Note:** Keys and key-path members must **not** be enclosed in quotation marks.

### GET
Retrieve the full value for a top-level key:
```
GET <top-level-key>
```
```
GET person2
```
Returns `NOT FOUND` if the key does not exist. Returns a warning if fewer servers than the replication factor responded, but still attempts to return the data.

---

### QUERY
Retrieve a nested value by dot-separated key path:
```
QUERY <key-path>
```
```
QUERY person2.address.street
```
Each segment of the path is separated by a `.`. Works for both top-level keys and arbitrarily deep nested keys.

---

### DELETE
Delete a record by top-level key:
```
DELETE <top-level-key>
```
```
DELETE person2
```
The delete is sent to every connected server. Only works for top-level keys.

---

### COMPUTE
Evaluate a math expression, optionally using values from the store as variables:
```
COMPUTE <expression>
COMPUTE <expression> WHERE <var> = QUERY <key-path>
COMPUTE <expression> WHERE <var> = QUERY <key-path> AND <var2> = QUERY <key-path2>
```

Examples:
```
COMPUTE 1+1
COMPUTE 2*x WHERE x = QUERY person2.address.number
COMPUTE 2/(x+3*(y+z)) WHERE x = QUERY person2.address.number AND y = QUERY person1.age
```
Supports standard operators and functions such as `log`, `sin`, `cos`, and all other functions provided by the [mXParser library](https://mathparser.org/). If a queried key does not exist or its value cannot be converted to a number, that variable is set to `0`.

---

### Quit
```
q
```
Gracefully disconnects from all servers before exiting.

---

### Help
```
h
```
Prints the list of available commands.

---

## Data Format

Records follow this structure:
```
"person2" -> [ "name" -> "Mary" | "address" -> [ "street" -> "Panepistimiou" | "number" -> 12 ]]
```
- `->` separates a key from its value
- `|` separates multiple key-value pairs
- `[` `]` denote nested objects

---

## Alternatively with maven (without Docker)

 Refer to the individual README files in kvClient, kvServer, and genData directories for detailed instructions on building and running each component using Maven.