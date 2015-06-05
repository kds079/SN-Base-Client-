package edu.kaist.cs.cs560.team5.snql;

import java.io.IOException;

import kr.ac.kaist.idb.snql.connector.ClientConnector;

/**
 * Created by dongshin on 2015-06-05.
 */
public class ConnectionThread extends Thread {
    private ClientConnector connector;

    public ConnectionThread(ClientConnector connector) {
        this.connector = connector;

        setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Connection fail.");
            }
        });
    }

    @Override
    public void run() {
        try {
            connector.connect();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.println("Connection successfully.");
    }
}