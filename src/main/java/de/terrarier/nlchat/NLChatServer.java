package de.terrarier.nlchat;

import de.terrarier.netlistening.Server;
import de.terrarier.netlistening.api.event.*;

import java.nio.charset.StandardCharsets;

public final class NLChatServer {

    public static void main(String[] args) {
        final Server server = new Server.Builder(6790)
                .timeout(15000L)                                                        // timeout
                .encryption().build()                                                  // encryption
                .compression().nibbleCompression(true).varIntCompression(true).build() // compression
                .stringEncoding(StandardCharsets.UTF_16)                               // encoding
                .build();

        /*
        final Server minimalServer = new Server.Builder(6790)
                .timeout(15000L)
                .build();
        */

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
                connectionPostInitEvent.getConnection().sendData("Hey, i'm the server!");
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
        server.sendData(message);
    }

    public void sendMessage(String message, int connectionId) {
        server.getConnection(connectionId).sendData(message);
    }

    public void stop() {
        server.stop();
    }

}
