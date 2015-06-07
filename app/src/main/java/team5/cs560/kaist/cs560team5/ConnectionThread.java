package team5.cs560.kaist.cs560team5;

/**
 * Created by jsim on 2015-06-07.
 */
import java.io.IOException;

import kr.ac.kaist.idb.snql.connector.ClientConnector;

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