package team5.cs560.kaist.cs560team5;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
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
    private LocationManager mLocMgr;
    private double protectorLocationLat;
    private double protectorLocationLon;
    private boolean gpsFlag = false;
    private double distance;
    private boolean hrFlag = true;
    private boolean escapeFlag = true;
    private boolean distanceFlag = true;


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
        // author: jaeseong
        // for protector location
        mLocMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String locProv = mLocMgr.getBestProvider(getCriteria(), true);
        mLocMgr.requestLocationUpdates( locProv, 3000, 3, mLocListener );
        //mLocMgr.requestLocationUpdates( LocationManager.GPS_PROVIDER, 3000, 3, mLocListener );
        Log.d("tag", "Location Service Start");
        self = this;
    }

    public double getDistance(){
        if (gpsFlag) {
            return distance;
        } else {
            return -1;
        }
    }

    // author: jaeseong
    // for protector location
    LocationListener mLocListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            protectorLocationLat = location.getLatitude();
            protectorLocationLon = location.getLongitude();
            gpsFlag = true;
        }

        public void onProviderDisabled(String provider) {
            Log.d("tag", "Provider Disabled");
        }

        public void onProviderEnabled(String provider) {
            Log.d("tag", "Provider Enabled");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("tag", "Provider Out of Service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("tag", "Provider Temporarily Unavailable");
                    break;
                case LocationProvider.AVAILABLE:
                    Log.d("tag", "Provider Available");
                    break;
            }
        }
    };
    // for protector location
    // author: Jaeseong
    public static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        return criteria;
    }

    @Override
    public void onDestroy() {
        mLocMgr.removeUpdates(mLocListener);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ClientListener implements ClientConnectorListener{

        @Override
        public void onConnect() {
            PlanKey planKey = null;
            new ProcessGetUser().execute(null, null, null);
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

        public double calDistance(double lat1, double lon1, double lat2, double lon2){

            double theta, dist;
            theta = lon1 - lon2;
            dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);

            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
            dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

            return dist;
        }

        // degree to radian
        private double deg2rad(double deg){
            return (double)(deg * Math.PI / (double)180d);
        }

        // radian to degree
        private double rad2deg(double rad){
            return (double)(rad * (double)180d / Math.PI);
        }

        @Override
        public void onReceiveResult(PlanKey planKey, ResultTable table) {
            printResult(planKey, table);
//            Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
//            intent.putExtra("name", "dskim");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getApplicationContext().startActivity(intent);
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Float hr = mPref.getFloat("hr", 0);
            Float dist = mPref.getFloat("dist", 0);
            Float la1 = mPref.getFloat("la1", 0);
            Float lo1 = mPref.getFloat("lo1", 0);
            Float la2 = mPref.getFloat("la2", 0);
            Float lo2 = mPref.getFloat("lo2", 0);

            if( "getUser".equals(queryMap.get(planKey))) {          //for Select Activity
                Log.d("dskim", "onReceiveResut : getUser");
                setUserList(table);
//                queryMap.remove(planKey);
            } else if("DistEvent".equals(queryMap.get(planKey))) {  //Event query for hr, region
                Log.d("dskim", "onReceiveResut : DistEvent");

                //Check Distnace Event
                double[] gps = getGps(table);
                //gps[0] : latitude
                //gps[1] : longitude
                //if distance condition
                if (gpsFlag) {
                    distance = calDistance(protectorLocationLat, gps[0], protectorLocationLon, gps[1]);
                    //distance = Math.sqrt( Math.pow((protectorLocationLat - gps[0]) * 1800, 2) + Math.pow((protectorLocationLon - gps[1]) * 1500, 2) );
                    Log.w("dskim", "Check Dist Event >> resultDist : " + distance  + " - setDist : " + dist);
                    // activate when distance_threshold is defined
                    if (distance > dist && distanceFlag == true) {
                        Log.w("dskim", "Check Hr Event >> startDistNoti");
                        startDistNoti();
                        distanceFlag = false;
                    }
                }

                //Check Hr Event
                long hrResult = getHr(table);
                Log.w("dskim", "Check Hr Event >> hrResult : " + hrResult  + " - hr : " + hr);
                if(hr > hrResult && hrFlag == true){
                    Log.w("dskim", "Check Hr Event >> startHeartNoti");
                    startHeartNoti();
                    hrFlag = false;
                }

                //Check Region Event
                float highLa = 0, lowLa = 0;
                float highLo = 0, lowLo = 0;
                if(la1 > la2){
                    highLa = la1;
                    lowLa = la2;
                } else{
                    highLa = la2;
                    lowLa = la1;
                }
                if(lo1 > lo2){
                    highLo = lo1;
                    lowLo = lo2;
                } else{
                    highLo = lo2;
                    lowLo = lo1;
                }
                //gps[0] : latitude
                //gps[1] : longitude
                Log.w("dskim", "Check Region Event >> la1 : " + la1  + " la2 : " + la2 + " lo1 : " + lo1 + " lo2 : " + lo1);
                Log.w("dskim", "Check Region Event >> resultLa : " + gps[0]  + " resultLo : " + gps[1]);
                if((gps[0] < lowLa || gps[0] > highLa || gps[1] < lowLo || gps[1] > highLo) && escapeFlag == true){
                    Log.w("dskim", "Check Hr Event >> startEscapeNoti");
                    startEscapeNoti();
                    escapeFlag = false;
                }

                setMonitorData(table);
                new ProcessDistQuery().execute(null, null, null);
            } else {                                                    //Select query for distance event
                Log.d("dskim", "onReceiveResut : event");
            }
            queryMap.remove(planKey);
        }

        private void setMonitorData(ResultTable table){
            Object[] tuples = null;
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = mPref.edit();
            table.reset();
            while (table.hasNext()) {
                tuples = table.getTuple();
                editor.putString("monName", (String) tuples[0]);
                editor.putFloat("monLa", ((Double)tuples[1]).floatValue());
                editor.putFloat("monLo", ((Double) tuples[2]).floatValue());
                try {
                    editor.putLong("monHr", (Long) tuples[3]);
                } catch(Exception e){
                    editor.putLong("monHr", (Integer) tuples[3]);
                }
            }
            editor.commit();
        }

        private long getHr(ResultTable table){
            long hr=0;
            Object[] tuples = null;
            table.reset();
            while (table.hasNext()) {
                tuples = table.getTuple();
                try {
                    hr = (Long)tuples[3];
                } catch(Exception e){
                    hr = (Integer)tuples[3];
                }
            }
            return hr;
        }

        private double[] getGps(ResultTable table){
            double[] gps = new double[2];
            Object[] tuples = null;
            table.reset();

            while (table.hasNext()) {
                tuples = table.getTuple();

                gps[0] = (Double)tuples[1];
                gps[1] = (Double)tuples[2];
            }
            return gps;
        }

        private void startDistNoti(){

            NotificationManager nm2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);   // call notification manager
            Notification notification2 = new Notification(R.mipmap.distance, "Warning! far from you!", System.currentTimeMillis());   // icon, tickerText, when
            notification2.flags = Notification.FLAG_AUTO_CANCEL;
            notification2.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
            notification2.number = 13;
            PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MonitorActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            notification2.setLatestEventInfo(getApplicationContext(), "Far from you!", "Your child too far from you!!", pendingIntent2);   // context, contentTitle, contentText, contentIntent
            nm2.notify(1236, notification2);  // id, notification object
        }

        private void startHeartNoti() {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);   // call notification manager
            Notification notification = new Notification(R.mipmap.heart_attack, "Warning! Heart Attack!", System.currentTimeMillis());   // icon, tickerText, when
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
            notification.number = 13;
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MonitorActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(getApplicationContext(), "Heart Attack!", "Heart attack is happend!!", pendingIntent);   // context, contentTitle, contentText, contentIntent
            nm.notify(1234, notification);  // id, notification object
        }

        private void startEscapeNoti() {
            NotificationManager nm1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);   // call notification manager
            Notification notification1 = new Notification(R.mipmap.escape, "Warning! Escape!", System.currentTimeMillis());   // icon, tickerText, when
            notification1.flags = Notification.FLAG_AUTO_CANCEL;
            notification1.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
            notification1.number = 13;
            PendingIntent pendingIntent1 = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MonitorActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            notification1.setLatestEventInfo(getApplicationContext(), "Escape!", "Your child escape from range!!", pendingIntent1);   // context, contentTitle, contentText, contentIntent
            nm1.notify(1235, notification1);  // id, notification object
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

        private class ProcessDistQuery extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                try
                {
                    SystemClock.sleep(1000);
                    Log.v("dskim", "==>>>  Query time : " + new Timestamp(new Date().getTime()));

                    SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    int userSize = mPref.getInt("protegeno", 0);
                    StringBuffer userCondition = new StringBuffer();
                    for(int i=0; i<userSize ; i++) {
                        userCondition.append("name = '").append(mPref.getString("protege" + i, "default")).append("' ");
                        if (i != (userSize - 1)) {
                            userCondition.append("or ");
                        }
                    }
                    String queryStmt = "SELECT name, latitude, longitude, hr, timestamp()\n"
                            + "FROM profile, gps, node\n"
                            + "WHERE " + userCondition.toString();
//                    String queryStmt = "SELECT name, latitude, longitude, timestamp()\n"
//                            + "FROM profile, gps";
                    PlanKey planKey = clientConnector.executeQuery(queryStmt);
                    queryMap.put(planKey, "DistEvent");
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

