package edu.kaist.cs.cs560.team5.snql;

import kr.ac.kaist.idb.snql.connector.ClientConnector;

/**
 * Created by dongshin on 2015-06-05.
 */
public class SNQLBaseClient {

	public static void main(String args[]){
        String baseAddress = "143.248.56.243";
        int planPort = 11722;
        int dataPort = 11723;

        ClientConnector clientConnector = new ClientConnector(baseAddress, planPort, dataPort);
        clientConnector.setListener(new ConnectionListener(clientConnector));
        new ConnectionThread(clientConnector).start();
        
	}
}
