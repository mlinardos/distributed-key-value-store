package io.github.mlinardos.kvstore.client;

import java.util.concurrent.CopyOnWriteArrayList;

public class kvClient {

    public static int replicationFactor;
    public static int numberOfConnectedServers;
    public static CopyOnWriteArrayList<ServerConnection> connections;

    public static void main(String[] args) {

        try {
            if (args.length < 6 || (!args[0].equals("-s") || !args[2].equals("-i") || !args[4].equals("-k"))) {
                System.out.println("Usage: java -cp . io.github.mlinardos.kvstore.client.kvClient -s <server-file> -i <data-file> -k  <replication-factor>");
                System.exit(1);
            }
            String serverFileName = args[1];
            String dataFileName = args[3];
            replicationFactor = Integer.parseInt(args[5]);
            kvClientAppManager kvClientAppManager =  new kvClientAppManager(serverFileName,dataFileName, replicationFactor);
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }





    }

















}