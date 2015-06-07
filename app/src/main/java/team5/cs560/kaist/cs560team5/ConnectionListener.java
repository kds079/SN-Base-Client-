package team5.cs560.kaist.cs560team5;

/**
 * Created by jsim on 2015-06-07.
 */
import java.sql.Timestamp;
import java.util.Date;

import kr.ac.kaist.idb.snql.connector.ClientConnector;
import kr.ac.kaist.idb.snql.connector.ClientConnectorListener;
import kr.ac.kaist.idb.snql.planner.PlanKey;
import kr.ac.kaist.idb.snql.relation.Attribute;
import kr.ac.kaist.idb.snql.relation.ResultTable;

/**
 * Created by dongshin on 2015-06-05.
 */
public class ConnectionListener implements ClientConnectorListener{
    private ClientConnector clientConnector;

    ConnectionListener(ClientConnector connector) {
        this.clientConnector = connector;
    }

    @Override
    public void onConnect() {

        String queryStmt = null;
        PlanKey planKey = null;
        System.out.println("==>>>  Query time : " + new Timestamp(new Date().getTime()));
//		planKey = clientConnector.executeQuery("SHOW tables");
//		planKey = clientConnector.executeQuery("SHOW events");

//		queryStmt = "SELECT name, birth, phoneno, teamno, hr, latitude, longitude, timestamp()\n"
//				+ "FROM node, profile, gps";
//		planKey = clientConnector.executeQuery(queryStmt);

//		queryStmt = "SELECT name, hr, latitude, longitude, timestamp()\n"
//				+ "FROM node, profile, gps\n" + "SAMPLE PERIOD 5s FOR 15s";
//		planKey = clientConnector.executeQuery(queryStmt);

        queryStmt = "CREATE EVENT heart_rate_event\n"
                + "FROM node WHEN hr < 90";
        planKey = clientConnector.executeQuery(queryStmt);

        queryStmt = "ON EVENT (normal_heart_rate, 5s, 30s, REPEAT)\n"
                + "SELECT name, hr, latitude, longitude, timestamp() FROM node, profile, gps";
        planKey = clientConnector.executeQuery(queryStmt);
    }

    @Override
    public void onDisconnect() {
        System.out.println("Connection lost.");
    }

    @Override
    public void onReceiveClientPlan(PlanKey pKey1, PlanKey pKey2) {
        System.out.println("======>>> " + pKey1 + " : " + pKey2);
    }

    @Override
    public void onReceiveResult(PlanKey planKey, ResultTable table) {
        printResult(planKey, table);

//		resultMap.put(planKey, table);

    }

    void printResult(PlanKey planKey, ResultTable table){
        Attribute[] attrs = table.getAttributes();
        Object[] tuples = null;
        StringBuffer sb = new StringBuffer();
        System.out.println("==================================================");
        System.out.println("==>>>  Resp time : " + new Timestamp(new Date().getTime()));
        System.out.println("size : " + table.size());

        for( Attribute attr : attrs){
            sb.append(attr.getName() + " | ");
        }
        System.out.println(sb);
        sb.setLength(0);

        while (table.hasNext()) {
            tuples = table.getTuple();
            for( Object tuple : tuples){
                sb.append(tuple + " | ");
            }
            System.out.println(sb);
            sb.setLength(0);
        }
        System.out.println("==================================================");
    }
}
