
package com.example;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {

            String response =
                "Hello! Java Docker Jenkins POC is running!";

            exchange.sendResponseHeaders(200, response.length());

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.start();

        System.out.println("Application started on port 8080");
    }
}


