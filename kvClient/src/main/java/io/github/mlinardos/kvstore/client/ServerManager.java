package io.github.mlinardos.kvstore.client;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerManager {

   private List<ServerAddress> serverList;
   private List<ServerConnection> serverConnections;


    public ServerManager(List<ServerAddress> serverList) {
        this.serverList = serverList;
    }




    public  List<ServerConnection> getRandomServers(int replicationFactor) {
        List<ServerConnection> randomServers = new ArrayList<>();
        for(int i = 0; i < replicationFactor; i++) {
            int randomIndex = (int) (Math.random() * serverConnections.size());
            while(randomServers.contains(serverConnections.get(randomIndex))) {
                randomIndex = (int) (Math.random() * serverConnections.size());
            }
            randomServers.add(serverConnections.get(randomIndex));
        }
        return randomServers;
    }



    public  List<ServerConnection> connectToServers(List<ServerAddress> serverList) {
       this.serverConnections  = new CopyOnWriteArrayList<>();
        serverList.forEach(server -> {
            try {
                ServerConnection connection = new ServerConnection(server);
                boolean isConnected = connection.connect();
                if(isConnected) this.serverConnections.add(connection);
            }
            catch (Exception e) {
                System.out.println("Error while connecting to server: " + server.toString() + " " + e.getMessage());

            }
        });

        return serverConnections;
    }

    public List<ServerConnection> getServerConnections() {
        return serverConnections;
    }



    public ConcurrentLinkedQueue<ServerConnection> getConnectedServers() {
        ConcurrentLinkedQueue<ServerConnection> connectedServers = new ConcurrentLinkedQueue<>();
        serverConnections.parallelStream().forEach(serverConnection -> {
                    try {
                        serverConnection.out.println("PING");
                        String response = serverConnection.in.readLine();
                        if(response.equals("PONG")) connectedServers.add(serverConnection);
                    }
                    catch (SocketTimeoutException e) {
                        System.out.println("Timeout reached. No response from server.");
                        serverConnection.connectionStatus = ConnectionStatus.DISCONNECTED;
                    }
                    catch (Exception e) {
                        serverConnection.connectionStatus = ConnectionStatus.DISCONNECTED;
                        System.out.println("Error while getting data from server: " + serverConnection.serverAddress.toString() + " " + e.getMessage());
                    }
                });

        return connectedServers;
    }


    public boolean disconnectFromServers() {
        serverConnections.forEach(serverConnection -> {
            try {
                serverConnection.out.println("DISCONNECT");
            }
            catch (Exception e) {
                System.out.println("Error while disconnecting from server: " + serverConnection.serverAddress.toString() + " " + e.getMessage());
            }
        });
        return true;
    }


    public int getTotalServers() {
        return serverConnections.size(); // or however you store all servers
    }



}
