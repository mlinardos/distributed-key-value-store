package io.github.mlinardos.kvstore.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerConnection {

    ServerAddress serverAddress;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    ConnectionStatus connectionStatus;
    int numberOfSuccessfulPUTRequests;
    Boolean SuccessfullyIndexed =false;

    public ServerConnection(ServerAddress serverAddress) {
        this.serverAddress = serverAddress;

    }

    public Boolean connect() {

        try {
            socket = new Socket();
            socket.setSoTimeout(5000);
            socket.connect(new InetSocketAddress(this.serverAddress.ip, this.serverAddress.port), 1000);
            if (socket.isConnected()) {
                System.out.println("Successfully connected to server" + this.serverAddress.toString());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                connectionStatus = ConnectionStatus.CONNECTED;
                return true;
            } else {
                System.out.println("Failed to connect to server" + this.serverAddress.toString());
                return false;
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout reached. No response from server.");
        } catch (Exception e) {
            System.out.println("Error while connecting to server: " + this.serverAddress.toString() + " " + e.getMessage());
            return false;

        }


        return null;
    }
}
