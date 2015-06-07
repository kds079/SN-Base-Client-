package team5.cs560.kaist.cs560team5;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class notiService extends Service {

    // receive threshold from SetActivity



    public notiService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        String baseAddress = "143.248.56.243";
//        int planPort = 11722;
//        int dataPort = 11723;
//
//        ClientConnector clientConnector = new ClientConnector(baseAddress, planPort, dataPort);
//        clientConnector.setListener(new ConnectionListener(clientConnector));
//        new ConnectionThread(clientConnector).start();

        new Thread(task).start();
    }

    Runnable task = new Runnable() {
        public void run() {
            while(!MainActivity.isDestroy) {

                try {
                    //Log.d("tags", "Flag: " + MainActivity.isSetFlag);
                    if (MainActivity.isSetFlag) {
                        // TODO: write the condition of threshold to alert 1. heart rate, 2. escape, 3. far from protector
                        // author:Jaeseong
                        // notification code
                        // http://itmir.tistory.com/457
                        // reference: 26-3 "old" version notification representation
                        // if ( heart rate > threshold ) {
                        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);   // call notification manager
                        Notification notification = new Notification(R.mipmap.heart_attack, "Warnning! Heart Attack!", System.currentTimeMillis());   // icon, tickerText, when
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                        notification.number = 13;
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MonitorActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.setLatestEventInfo(getApplicationContext(), "Heart Attack!", "Heart attack is happend!!", pendingIntent);   // context, contentTitle, contentText, contentIntent
                        nm.notify(1234, notification);  // id, notification object
                        //}
//                if ( escape == true) {
//                     NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);   // call notification manager
                        NotificationManager nm1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);   // call notification manager
                        Notification notification1 = new Notification(R.mipmap.escape, "Warnning! Escape!", System.currentTimeMillis());   // icon, tickerText, when
                        notification1.flags = Notification.FLAG_AUTO_CANCEL;
                        notification1.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                        notification1.number = 13;
                        PendingIntent pendingIntent1 = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MonitorActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                        notification1.setLatestEventInfo(getApplicationContext(), "Escape!", "Your child escape from range!!", pendingIntent1);   // context, contentTitle, contentText, contentIntent
                        nm1.notify(1235, notification1);  // id, notification object
//                }
//                if ( far_from_protector == true ) {
                        NotificationManager nm2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);   // call notification manager
                        Notification notification2 = new Notification(R.mipmap.distance, "Warnning! far from you!", System.currentTimeMillis());   // icon, tickerText, when
                        notification2.flags = Notification.FLAG_AUTO_CANCEL;
                        notification2.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                        notification2.number = 13;
                        PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MonitorActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                        notification2.setLatestEventInfo(getApplicationContext(), "Far from you!", "Your child too far from you!!", pendingIntent2);   // context, contentTitle, contentText, contentIntent
                        nm2.notify(1236, notification2);  // id, notification object
//                }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
