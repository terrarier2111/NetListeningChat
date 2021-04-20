package de.terrarier.nlchat;

import de.terrarier.netlistening.Client;
import de.terrarier.netlistening.api.event.*;

public final class NLChatClient {

    public static void main(String[] args) {
        final Client client = new Client.Builder("localhost", 6790)
                .timeout(15000L)
                .build();

        client.registerListener(new DecodeListener() {
            @Override
            public void trigger(DecodeEvent decodeEvent) {
                final String message = decodeEvent.getData().read();
                System.out.println("Server: " + message);
            }
        });

        client.registerListener(new ConnectionPostInitListener() {
            @Override
            public void trigger(ConnectionPostInitEvent connectionPostInitEvent) {
                System.out.println("The connection was opened!");
            }
        });

        client.registerListener(new ConnectionDisconnectListener() {
            @Override
            public void trigger(ConnectionDisconnectEvent connectionDisconnectEvent) {
                System.out.println("The connection was closed!");
            }
        });

        client.registerListener(new ConnectionTimeoutListener() {
            @Override
            public void trigger(ConnectionTimeoutEvent connectionTimeoutEvent) {
                System.out.println("The connection timed out!");
            }
        });

        final NLChatClient chatClient = new NLChatClient(client);
        chatClient.sendMessage("Hello world!"); // this gets sent when the connection was established
        // Do other stuff with the client
    }

    private final Client client;

    public NLChatClient(Client client) {
        this.client = client;
    }

    public void sendMessage(String message) {
        client.sendData(message);
    }

    public void stop() {
        client.stop();
    }

}
