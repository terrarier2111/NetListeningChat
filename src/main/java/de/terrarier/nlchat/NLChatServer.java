package de.terrarier.nlchat;

import de.terrarier.netlistening.Server;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.netlistening.api.event.*;

public final class NLChatServer {

    public static void main(String[] args) {
        final Server server = new Server.Builder(6790)
                .timeout(15000)                                                        // timeout
                .encryption().build()                                                  // encryption
                .compression().nibbleCompression(true).varIntCompression(true).build() // compression
                .build();

        server.registerListener(new DecodeListener() {
            @Override
            public void trigger(DecodeEvent decodeEvent) {
                final String message = decodeEvent.getData().read();
                System.out.println(decodeEvent.getConnection().getId() + ": " + message);
            }
        });

        server.registerListener(new ConnectionPostInitListener() {
            @Override
            public void trigger(ConnectionPostInitEvent connectionPostInitEvent) {
                System.out.println(connectionPostInitEvent.getConnection().getId() + " connected!");
            }
        });

        server.registerListener(new ConnectionDisconnectListener() {
            @Override
            public void trigger(ConnectionDisconnectEvent connectionDisconnectEvent) {
                System.out.println(connectionDisconnectEvent.getConnection().getId() + " disconnected!");
            }
        });

        server.registerListener(new ConnectionTimeoutListener() {
            @Override
            public void trigger(ConnectionTimeoutEvent connectionTimeoutEvent) {
                System.out.println(connectionTimeoutEvent.getConnection().getId() + " timed out!");
            }
        });

        final NLChatServer chatServer = new NLChatServer(server);
        // Do other stuff with the server
    }

    private final Server server;

    public NLChatServer(Server server) {
        this.server = server;
    }

    public void sendMessage(String message) {
        final DataContainer data = new DataContainer();
        data.add(message);
        server.sendData(data);
    }

    public void sendMessage(String message, int target) {
        final DataContainer data = new DataContainer();
        data.add(message);
        server.getConnection(target).sendData(data);
    }

    public void stop() {
        server.stop();
    }

}
