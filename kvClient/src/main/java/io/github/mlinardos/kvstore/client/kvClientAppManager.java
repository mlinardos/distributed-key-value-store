package io.github.mlinardos.kvstore.client;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import org.mariuszgromada.math.mxparser.*;

public class kvClientAppManager {

    public int replicationFactor;
    public int numberOfConnectedServers;
    public  CopyOnWriteArrayList<ServerConnection> connections;
    public String serverFilePath;
    public String dataFilePath;
    public ServerManager  serverManager;




    public kvClientAppManager(String serverFilePath, String dataFilePath, int replicationFactor) {
       this.serverFilePath = serverFilePath;
       this.dataFilePath = dataFilePath ;
       this.replicationFactor = replicationFactor;
        kvClientFileReader fileReader = new kvClientFileReader();
        List<ServerAddress> serverList = fileReader.readServerFile(serverFilePath);
        List<String> dataLines = fileReader.readDataFile(dataFilePath);
        if(dataLines == null || serverList == null) return ;
        serverManager = new ServerManager(serverList);
        List<ServerConnection> serverConnections = serverManager.connectToServers(serverList);
        if(serverConnections.size() == 0) {System.out.println("All servers unavailable "); return ;}
        if(serverConnections.size() < replicationFactor) {System.out.println("Replication factor is greater than number of available servers"); return ;}
        Boolean dataIndexedSuccessfully = indexData(dataLines);
        if(!dataIndexedSuccessfully) {
            System.out.println("Data not indexed Successfully");
            return ;
        }
        while(true){
            System.out.print("Enter next command [h] for help, [q] to quit:");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            if (command.equals("q")) {
                disconnectFromServers();
                System.out.println("Quitting");
                break;
            } else if (command.split(" ")[0].equals("GET")) {
                String data = command.split(" ")[1];
                get(data);
            }
            else if(command.split(" ")[0].equals("DELETE")){
                String data = command.split(" ")[1];
                delete(data);
            }
            else if(command.split(" ")[0].equals("QUERY")){
                String data = command.split(" ")[1];
                query(data,true);
            }
            else if(command.split(" ")[0].equals("COMPUTE")){
                String expression = command.replaceFirst("COMPUTE", "").trim();
                compute(expression);


            }
            else if (command.equals("h")) {
                System.out.println("Available commands:");
                System.out.println("GET <key>");
                System.out.println("DELETE <key>");
                System.out.println("QUERY <key-path>");
                System.out.println("COMPUTE <query>");
                System.out.println("q - quit");
            } else {
                System.out.println("Unknown command");
            }

        }
    }
    public void disconnectFromServers(){
      System.out.println("Disconnecting from servers");
      serverManager.disconnectFromServers();
    }

    public void compute(String data){
        Expression e;
        if(data.contains("WHERE")){
            String mathExpression = data.split("WHERE")[0];
            e= new Expression(mathExpression);
            System.out.println("Computing expression: " + mathExpression);
            Map<String,Number>    resultsMap = evaluateWHEREClause(data);
            resultsMap.forEach((k, v)-> {

                if(v instanceof Integer){

                e.addArguments(new Argument(k, (Integer) v)) ;}
                else if(v instanceof Double){

                    e.addArguments(new Argument(k, (Double) v));
                }
                else if(v instanceof Float){
                    e.addArguments(new Argument(k, (Float) v));
                }
                else if(v instanceof Long){
                    e.addArguments(new Argument(k, (Long) v));
                }
                else{
                    e.addArguments(new Argument(k, (Integer) v));
                }

            });

        }else{   //IN CASE THERE IS NO WHERE CLAUSE
            e= new Expression(data);
        }

        Object result = e.calculate();
        System.out.println("Result: " + result);

    }

    public Map<String,Number> evaluateWHEREClause(String data){
        String[] variableDefinitions;
        String[] parts = data.split("WHERE");
        String whereExpression = parts[1];
        String mathExpression = parts[0];
        if(whereExpression.contains("AND")){
            variableDefinitions = whereExpression.split("AND");

        }
        else{
            variableDefinitions = new String[1];
            variableDefinitions[0] = whereExpression;
        }
        Map<String,String> variableMap = new HashMap<>();
        for(String variableDefinition : variableDefinitions){
            String[] variableParts = variableDefinition.split("=");
            String variableName = variableParts[0].trim();
            String variableValue = variableParts[1].trim();
            variableMap.put(variableName,variableValue);
        }
        Map<String,Number> resultsMap ;
        resultsMap = getVariableValues(variableMap);
        return resultsMap;

    }


    public Map<String,Number> getVariableValues(Map<String,String> variableMap){

        NumberFormat numberFormat = NumberFormat.getInstance();

        Map<String,Number> resultsMap = new HashMap<>();
        variableMap.forEach((k,v)->{
            String result = query(v.replaceFirst("QUERY", "").trim(),false);
            if(result != null){
                try{
                    String resultValue =result.split("->")[1].trim();
                   Number number= numberFormat.parse(resultValue);
                    resultsMap.put(k,number);
                }
                catch (Exception e){
                    System.out.println("Result for Query searching for:" + v.replaceFirst("QUERY", "").trim() + " is not a number, setting value to 0");
                    resultsMap.put(k,0);
                }
            }else {
                System.out.println("Result for Query searching for:" + v.replaceFirst("QUERY", "").trim()+ " is null, setting value to 0");
                resultsMap.put(k,0);
            }
        });

        return resultsMap;
    }
    public void delete (String data){
        ConcurrentLinkedQueue<String> serverResponses = new ConcurrentLinkedQueue<String>();
        ConcurrentLinkedQueue<ServerConnection> connectedServers = serverManager.getConnectedServers();
        if(connectedServers.size() < serverManager.getServerConnections().size()) {
            System.out.println("Some servers are down, delete cannot be reliably executed");
            return;
        }

        connectedServers.parallelStream().forEach(serverConnection -> {
            try {
                serverConnection.out.println("DELETE " + data);
                String response = serverConnection.in.readLine();
                serverResponses.add(response);
            } catch (Exception e) {
                System.out.println("Error while deleting data from server: " + serverConnection.serverAddress.toString() + " " + e.getMessage());
            }
        });
        if(serverResponses.size() == connectedServers.size() && serverResponses.stream().anyMatch(response -> response.equals("DELETED"))) {
            System.out.println("Data deleted successfully");
        }
        else if(serverResponses.size() == connectedServers.size() && serverResponses.stream().allMatch(response -> response.equals("NOT FOUND"))) {
            System.out.println("NOT FOUND");
        }
        else {
            System.out.println("Data not deleted successfully (possibly due to some servers being down)");
        }

    }

    public String query(String data,boolean printResponse){
        ConcurrentLinkedQueue<String> serverResponses = new ConcurrentLinkedQueue<String>();
        ConcurrentLinkedQueue<ServerConnection> connectedServers = serverManager.getConnectedServers();
        if(connectedServers.size() < replicationFactor) {
            System.out.println("WARNING - Not enough servers connected to query data reliably");
        }
        connectedServers.parallelStream().forEach(serverConnection -> {
            try {
                serverConnection.out.println("QUERY " + data);
                String response = serverConnection.in.readLine();
                serverResponses.add(response);
            }
            catch (Exception e) {
                System.out.println("Error while getting data from server: " + serverConnection.serverAddress.toString() + " " + e.getMessage());
            }
        });
        if(  serverResponses.size() < replicationFactor) {
            System.out.println("WARNING - Not enough servers responded");
        }
      return  getResponse(serverResponses,printResponse);

    }


    public String get(String data) {
        ConcurrentLinkedQueue<String> serverResponses = new ConcurrentLinkedQueue<String>();
        ConcurrentLinkedQueue<ServerConnection> connectedServers = serverManager.getConnectedServers();
        if(connectedServers.size() < replicationFactor) {
            System.out.println("WARNING - Not enough servers connected to get data reliably");
        }
        connectedServers.parallelStream().forEach(serverConnection -> {
            try {
                serverConnection.out.println("GET " + data);
                String response = serverConnection.in.readLine();
                serverResponses.add(response);
            }
            catch (Exception e) {
                System.out.println("Error while getting data from server: " + serverConnection.serverAddress.toString() + " " + e.getMessage());
            }
        });
        if(  serverResponses.size() < replicationFactor) {
            System.out.println("WARNING - Not enough servers responded");
        }

         return   getResponse(serverResponses,true);

    }




    public String getResponse(ConcurrentLinkedQueue<String> serverResponses, boolean printResponse){
        String response = serverResponses.poll();
        while(!serverResponses.isEmpty()) {
            String nextResponse = serverResponses.poll();
            if(!response.equals("NOT FOUND") && !response.equals(nextResponse)) {
                System.out.println("WARNING - Data is not consistent");
                return null;
            }
        }
        if(printResponse) {
            System.out.println(response);
        }

        if(response.equals("NOT FOUND") || response.equals(null)) {
            return null;
        }
        else {
            return response;
        }

    }



    public Boolean indexData(List<String> dataLines) {
        dataLines.forEach(dataLine -> {
            List<ServerConnection> randomServers = serverManager.getRandomServers(replicationFactor);
            randomServers.forEach(serverConnection -> {
                serverConnection.out.println("PUT " + dataLine);
                try {
                    String Response = serverConnection.in.readLine();
                    if (Response.equals("OK")) {
                        serverConnection.numberOfSuccessfulPUTRequests++;
                        if (serverConnection.numberOfSuccessfulPUTRequests == dataLines.size()) {
                            serverConnection.SuccessfullyIndexed = true;
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println("Error while reading response from server: " + e.getMessage());
                }
            });
        });

        if(serverManager.getServerConnections().stream().filter(serverConnection -> serverConnection.SuccessfullyIndexed).count() == replicationFactor) {
            System.out.println("Data indexed to all servers successfully");
            return true;
        }
        else {
            System.out.println(serverManager.getServerConnections().stream().filter(serverConnection -> serverConnection.SuccessfullyIndexed).count());
            System.out.println(replicationFactor);
            System.out.println("Data not indexed Successfully to all servers.Replication Errors occurred");
            return false;
        }


    }

}
