package io.github.mlinardos.kvstore.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection {
    Socket clientSocket ;
    public BufferedReader in;
    public PrintWriter out;


    public ClientConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("Client connected");
        }
        catch (Exception e){
            System.out.println("Error while creating client connection: " + e.getMessage());
        }
    }

    public void close(){
        try {
            in.close();
            out.close();
            clientSocket.close();
        }
        catch (Exception e){
            System.out.println("Error while closing client connection: " + e.getMessage());
        }
    }


}
