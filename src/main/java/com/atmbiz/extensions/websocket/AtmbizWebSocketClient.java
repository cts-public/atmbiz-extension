package com.atmbiz.extensions.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class AtmbizWebSocketClient extends WebSocketClient {

    public AtmbizWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
        this.send("Hello from Java client!"); // Send a message to server once connected
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed. Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Error occurred:" + ex.getMessage());
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        URI serverUri;
        try {
            serverUri = new URI("ws://localhost:8080");
            AtmbizWebSocketClient client = new AtmbizWebSocketClient(serverUri);
            client.connect();

            // Keep the main thread alive for a while to allow testing
            Thread.sleep(10000);

            client.close(); // Close the WebSocket connection

        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}