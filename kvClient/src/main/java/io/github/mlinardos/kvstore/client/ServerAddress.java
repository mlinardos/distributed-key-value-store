package io.github.mlinardos.kvstore.client;

public class ServerAddress {
    protected String ip;
    protected int port;

    public ServerAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return "ServerAddress [ip=" + ip + ", port=" + port + "]";
    }
}
