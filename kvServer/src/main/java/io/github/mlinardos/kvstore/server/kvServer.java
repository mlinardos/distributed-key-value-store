package io.github.mlinardos.kvstore.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class kvServer {
    private static ServerSocket serverSocket;
    private static Trie trie;


    public static void main(String[] args) throws IOException {


        if (args.length < 4 || (!args[0].equals("-a") || !args[2].equals("-p") )) {
            System.out.println("Usage: java -cp . io.github.mlinardos.kvstore.server.kvServer -a <ip-address> -p <port>");
            System.exit(1);
        }
        String address = args[1];
        int PORT = Integer.parseInt(args[3]);
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        start(PORT,address);


    }

    public static void start(int PORT,String address) throws IOException {
        try {
            serverSocket = new ServerSocket(PORT);
        }
        catch (IOException e){
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
        trie = new Trie();
        while (true) {
            try {
                new ClientHandler(serverSocket.accept()).start();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
        }
    }


    private static class ClientHandler extends Thread {
        ClientConnection clientConnection;

        public ClientHandler(Socket socket) {
            clientConnection = new ClientConnection(socket);
        }

        public void run() {
            String inputLine;
            try {
                while ((inputLine = clientConnection.in.readLine()) != null) {

                    if (inputLine.equals("PING")) {
                        clientConnection.out.println("PONG");
                    }else if (inputLine.equals("DISCONNECT")) {

                        clientConnection.close();
                        System.out.println("Client disconnected");
                        break;
                    }

                    else {
                        String[] parts = inputLine.split(" ", 2);
                        String command = parts[0];
                        String value = parts[1];

                        if ((command.equals("PUT"))) {

                            if (SaveToDataStore(value))
                                clientConnection.out.println("OK");
                            else
                                clientConnection.out.println("ERROR");

                        } else if (command.equals("GET")) {
                            try {
                                String result = getData(value);
                                clientConnection.out.println(result);
                            } catch (RuntimeException e) {
                                clientConnection.out.println("NOT FOUND");
                            }
                        } else if (command.equals("DELETE")) {
                            try {
                                deleteFromDataStore(value);
                                clientConnection.out.println("DELETED");
                            } catch (RuntimeException e) {
                                clientConnection.out.println("NOT FOUND");
                            }

                        }
                        else if (command.equals("QUERY")) {
                            try {
                                String result = queryData(value);
                                clientConnection.out.println(result);
                            } catch (RuntimeException e) {
                                clientConnection.out.println("NOT FOUND");
                            }
                        }

                    }
                }} catch(Exception e){
                    System.out.println("Initial Error while reading from client: " + e.getMessage());
                    clientConnection.close();
                }

            }

        public String queryData(String value) {
            try {
                String resultString;
                String primaryKey;
                String secondaryKeyPath;
                if(value.contains(".")){
                    String [] keys = value.split("\\.",2);
                     primaryKey = keys[0];
                     secondaryKeyPath = keys[1];
                     secondaryKeyPath = "/" + secondaryKeyPath.replace(".","/");
                }
                else{
                     primaryKey   = value;
                     secondaryKeyPath = "";
                }
                Parser parser = new Parser();
                Object result = trie.search(primaryKey);
                if(!secondaryKeyPath.isEmpty()) {
                    JsonNode json = parser.ObjectToJsonNode(result);
                    JsonNode resultJson = json.at(secondaryKeyPath);
                    if(resultJson.isMissingNode())
                        throw new RuntimeException("NOT FOUND");

                    secondaryKeyPath = secondaryKeyPath.substring(1).replace("/",".");
                    resultString =  parser.resultToString(primaryKey+"."+ secondaryKeyPath,resultJson);
                }
                else{
                     resultString =  parser.resultToString(primaryKey,result);
                }

                return resultString;
            } catch (RuntimeException e) {
                throw new RuntimeException("NOT FOUND");
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error while parsing json");
            }

        }

        public List<String> getKeySequenceList(String key){
            String[] keySequence = key.split(".");
            List<String> keySequenceList = Arrays.asList(keySequence);
            return keySequenceList;

        }


        public String getData(String key) {
            try {
                Parser parser = new Parser();
                Object result = trie.search(key);
                String resultString =  parser.resultToString(key,result);
                return resultString;
            } catch (RuntimeException e) {
                throw new RuntimeException("Key not found");
            } catch (JsonProcessingException e) {
                System.out.println("Error while processing results: " + e.getMessage());
            }
            return null;

        }


        public void deleteFromDataStore(String key) {
            try {
                Parser parser = new Parser();
                Object result = trie.search(key);
                 trie.delete(key);
            } catch (RuntimeException e) {
                throw new RuntimeException("Key not found");
            }
        }


        public boolean SaveToDataStore(String value) {
                try {
                    Parser parser = new Parser();
                    Map<String, Object> map = parser.parseString(value);
                    // map.forEach((k, v) -> System.out.println(k + "*:*" + v));
                    for(Map.Entry<String, Object> entry : map.entrySet()) {
                        String key1 = entry.getKey();
                        Object value1 = entry.getValue();
                        trie.insert(key1, value1);
                    }
                } catch (IOException e) {
                    return false;
                }
                return true;
            }
    }
}




