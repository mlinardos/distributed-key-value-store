# Distributed Key-Value Store
A fault-tolerant, distributed key-value database with Trie-based indexing, 
nested data support, and mathematical computation capabilities.

## Build and run the data generation

docker build -f genData/Dockerfile -t kv-gendata:latest .
docker run -v $(pwd):/workspace kv-gendata:latest   -k genData/keyFile.txt -n 1000 -d 5 -l 5 -m 5

## build and run the kv-servers

docker build -f kvServer/Dockerfile -t kv-server:latest .
docker run -d -p 8000:8000 --rm --name kvserver1 kv-server:latest -a 0.0.0.0 -p 8000
docker run -d -p 8002:8002  --rm --name kvserver2 kv-server:latest -a 0.0.0.0 -p 8002
docker run -d -p 8003:8003 --rm --name kvserver3 kv-server:latest -a 0.0.0.0 -p 8003

## build and  run the kv-client
docker build -f kvClient/Dockerfile -t kv-client:latest .
docker run -it -v $(pwd):/workspace kv-client:latest   -s serverFile.txt -i dataToIndex.txt -k 2