package team5.cs560.kaist.cs560team5;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.ac.kaist.idb.snql.connector.ClientConnector;
import kr.ac.kaist.idb.snql.connector.ClientConnectorListener;
import kr.ac.kaist.idb.snql.planner.PlanKey;
import kr.ac.kaist.idb.snql.relation.Attribute;
import kr.ac.kaist.idb.snql.relation.ResultTable;

/**
 * Created by dongshin on 2015-06-07.
 */
public class ListenerService extends Service  {
//    private ClientConnector clientConnector;
    private static ListenerService self;
    private ClientListener clientListener;
    private ClientConnector clientConnector;
    private Map queryMap;

    public ListenerService(){
        clientListener = new ClientListener();
        String baseAddress = "143.248.56.243";
        int planPort = 11722;
        int dataPort = 11723;

        clientConnector = new ClientConnector(baseAddress, planPort, dataPort);
        clientListener = new ClientListener();
        clientConnector.setListener(clientListener);
//        clientConnector.setListener(new ListenerService(clientConnector, ClientConnectionHandler.context));
        new ConnectionThread(clientConnector).start();
        queryMap = new HashMap<PlanKey, String>();
        Log.d("dskim", "construct ListenerService");
    }

    public void setQueryMap(PlanKey planKey, String query){
        queryMap.put(planKey, query);
    }

    public static ListenerService getServiceObject(){
        return self;
    }

    public ClientConnector getClientConnector(){
        return clientConnector;
    }

    @Override
    public void onCreate () {
        super.onCreate();
        self = this;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ClientListener implements ClientConnectorListener{

        @Override
        public void onConnect() {
            PlanKey planKey = null;
//            new ProcessGetUser().execute(null, null, null);
//        String queryStmt = null;
//        PlanKey planKey = null;
//        Log.D("dskim", "==>>>  Query time : " + new Timestamp(new Date().getTime()));
////		planKey = clientConnector.executeQuery("SHOW tables");
////		planKey = clientConnector.executeQuery("SHOW events");
//
////		queryStmt = "SELECT name, birth, phoneno, teamno, hr, latitude, longitude, timestamp()\n"
////				+ "FROM node, profile, gps";
////		planKey = clientConnector.executeQuery(queryStmt);
//
////		queryStmt = "SELECT name, hr, latitude, longitude, timestamp()\n"
////				+ "FROM node, profile, gps\n" + "SAMPLE PERIOD 5s FOR 15s";
////		planKey = clientConnector.executeQuery(queryStmt);
//
//        queryStmt = "CREATE EVENT heart_rate_event\n"
//                + "FROM node WHEN hr < 90";
//        planKey = clientConnector.executeQuery(queryStmt);
//
//        queryStmt = "ON EVENT (normal_heart_rate, 5s, 30s, REPEAT)\n"
//                + "SELECT name, hr, latitude, longitude, timestamp() FROM node, profile, gps";
//        planKey = clientConnector.executeQuery(queryStmt);
        }

        @Override
        public void onDisconnect() {
            Log.d("dskim", "Connection lost.");
        }

        @Override
        public void onReceiveClientPlan(PlanKey pKey1, PlanKey pKey2) {
            Log.d("dskim", "======>>> " + pKey1 + " : " + pKey2);
        }

        @Override
        public void onReceiveResult(PlanKey planKey, ResultTable table) {
            printResult(planKey, table);
//            Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
//            intent.putExtra("name", "dskim");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getApplicationContext().startActivity(intent);

            if( "getUser".equals(queryMap.get(planKey))) {
                Log.d("dskim", "onReceiveResut : getUser");
                setUserList(table);
                queryMap.remove(planKey);
            } else{
                Log.d("dskim", "onReceiveResut : event");
                ;
            }
        }

        private void setUserList(ResultTable table){
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = mPref.edit();
            int userSize = mPref.getInt("userSize", 0);
            for(int i=0; i<userSize; i++) {
                editor.remove("user"+i);
            }
            ArrayList<String> users = getUser(table);
            for(int i=0; i<users.size(); i++) {
                editor.putString("user"+i, users.get(i));
            }
            editor.putInt("userSize", users.size());
            //Toast.makeText(this, a.toString(), Toast.LENGTH_SHORT).show();
            editor.commit();
        }

        private ArrayList<String> getUser(ResultTable table){
            ArrayList<String> users = new ArrayList<String>();
            Object[] tuples = null;
            table.reset();
            while (table.hasNext()) {
                tuples = table.getTuple();
                users.add((String)tuples[0]);
            }

            return users;
        }

        private void printResult(PlanKey planKey, ResultTable table){
            Attribute[] attrs = table.getAttributes();
            Object[] tuples = null;
            StringBuffer sb = new StringBuffer();
            Log.d("dskim", "==================================================");
            Log.d("dskim", "==>>>  PlanKey : " + planKey + " - Resp time : " + new Timestamp(new Date().getTime()));
            Log.d("dskim", "size : " + table.size());

            for ( Attribute attr : attrs){
                sb.append(attr.getName() + " | ");
            }
            Log.d("dskim", sb.toString());
            sb.setLength(0);

            while (table.hasNext()) {
                tuples = table.getTuple();
                for (Object tuple : tuples){
                    sb.append(tuple + " | ");
                }
                Log.d("dskim", sb.toString());
                sb.setLength(0);
            }
            Log.d("dskim", "==================================================");
        }

        private class ProcessGetUser extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                try
                {
                    Log.v("dskim", "==>>>  Query time : " + new Timestamp(new Date().getTime()));
                    String queryStmt = "SELECT name, birth, phoneno, teamno, hr, latitude, longitude, timestamp()\n"
                            + "FROM node, profile, gps";
//                planKey = clientConnector.executeQuery(queryStmt);
//                    ListenerService listernService = ListenerService.getServiceObject();
//                    ClientConnector clientConnector = listernService.getClientConnector();
                    PlanKey planKey = clientConnector.executeQuery(queryStmt);
                    queryMap.put(planKey, "getUser");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }
}

