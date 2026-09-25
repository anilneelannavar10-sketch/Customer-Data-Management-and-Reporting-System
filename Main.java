package com.customerapp;

import com.customerapp.server.ApiHandler;
import com.customerapp.server.StaticFileHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Entry point. Starts an HTTP server on port 8080 serving:
 *  - the static web UI at /
 *  - the REST API at /api/customers
 */
public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/customers", new ApiHandler());
        server.createContext("/", new StaticFileHandler("web"));

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("=================================================");
        System.out.println(" Customer Data Management System");
        System.out.println(" Server running at: http://localhost:" + PORT);
        System.out.println(" API base:          http://localhost:" + PORT + "/api/customers");
        System.out.println("=================================================");
    }
}
