package team5.cs560.kaist.cs560team5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;


public class MonitorActivity extends ActionBarActivity {
    private TableLayout monitorTable;
    private TableRow[] rowList;
    private TextView[] nameList;
    private TextView[] hrList;


    private TableThread tThread;
    private SharedPreferences mPref;
    private TextView monitorName;
    private TextView[][] rcs;
    private Long[] hrs;
    private Float[] las;
    private Float[] los;
    private double[] dis;


    private android.os.Handler tHandler = new android.os.Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        monitorName = (TextView)findViewById(R.id.monitorName);
        monitorTable = (TableLayout)findViewById(R.id.monitorTable);
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        rcs = new TextView[5][4];
        rcs[0][0] = (TextView)findViewById(R.id.monitorHR0); rcs[0][1] = (TextView)findViewById(R.id.monitorLa0); rcs[0][2] = (TextView)findViewById(R.id.monitorLo0); rcs[0][3] = (TextView)findViewById(R.id.monitorDist0);
        rcs[1][0] = (TextView)findViewById(R.id.monitorHR1); rcs[1][1] = (TextView)findViewById(R.id.monitorLa1); rcs[1][2] = (TextView)findViewById(R.id.monitorLo1); rcs[1][3] = (TextView)findViewById(R.id.monitorDist1);
        rcs[2][0] = (TextView)findViewById(R.id.monitorHR2); rcs[2][1] = (TextView)findViewById(R.id.monitorLa2); rcs[2][2] = (TextView)findViewById(R.id.monitorLo2); rcs[2][3] = (TextView)findViewById(R.id.monitorDist2);
        rcs[3][0] = (TextView)findViewById(R.id.monitorHR3); rcs[3][1] = (TextView)findViewById(R.id.monitorLa3); rcs[3][2] = (TextView)findViewById(R.id.monitorLo3); rcs[3][3] = (TextView)findViewById(R.id.monitorDist3);
        rcs[4][0] = (TextView)findViewById(R.id.monitorHR4); rcs[4][1] = (TextView)findViewById(R.id.monitorLa4); rcs[4][2] = (TextView)findViewById(R.id.monitorLo4); rcs[4][3] = (TextView)findViewById(R.id.monitorDist4);
        hrs = new Long[5];
        las = new Float[5];
        los = new Float[5];
        dis = new double[5];



        //
        Float hr = mPref.getFloat("hr", 0);
        Float dist = mPref.getFloat("dist", 0);
        Float la1 = mPref.getFloat("la1", 0);
        Float lo1 = mPref.getFloat("lo1", 0);
        Float la2 = mPref.getFloat("la2", 0);
        Float lo2 = mPref.getFloat("lo2", 0);
        Log.d("tags","DIST: "+dist);
        Log.d("tags","HR: "+hr);
        Log.d("tags","La/Lo1: "+la1+","+lo1);
        Log.d("tags", "La/lo2: " + la2 + "," + lo2);
        //mtView.setText("hr: "+hr+", dist: "+dist+"\n la1"+la1+" lo1 "+lo1+" la2 "+la2+" lo2 "+lo2);
        ////

        int i,j;
        for(i = 0; i < 5; ++i)
        {
            for(j = 0; j < 4; ++j)
            {
//                rcs[i][j].setText(""+i+j);
                rcs[i][j].setText("");
            }
        }


        tThread = new TableThread();
        tThread.start();



    }


    class TableThread extends Thread
    {
        int i;
        private int cycle = 4000;
        private boolean isRunning = true;
        @Override
        public void run()
        {
            super.run();
            Random rand = new Random();
            while (isRunning)
            {
                for(i = 3; i >= 0; --i)
                {
                    hrs[i+1] = hrs[i]; las[i+1] = las[i]; los[i+1] = los[i]; dis[i+1] = dis[i];
                }
                hrs[0] = mPref.getLong("monHr", 0); las[0] = mPref.getFloat("monLa", 0); los[0] = mPref.getFloat("monLo", 0);
                dis[0] = ListenerService.getServiceObject().getDistance();

                // random for test
//                hrs[0] = rand.nextLong() % 150;
//                las[0] = rand.nextFloat();
//                los[0] = rand.nextFloat();
//                dis[0] = rand.nextFloat();
                //Log.v("lanakim", "Getting data from preference");
                //Log.v("lanakim", hrs[0].toString() + " " + las[0].toString() + " " + los[0].toString() + " " + dis[0].toString());
                tHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Log.v("lanakim", "Enter run()");
                        //Log.v("lanakim", "Changing name");
                        monitorName.setText(mPref.getString("monName", "LanaKim"));
                        int ii;

                        //Log.v("lanakim", "Changing values");
                        try {
                            for (ii = 0; ii < 5; ++ii) {
                                //Log.v("lanakim", "hrs update : " + hrs[ii].toString());
                                rcs[ii][0].setText(String.format("%d", hrs[ii]!=null?hrs[ii]:""));
                                //Log.v("lanakim", "las update : " + las[ii].toString());
                                rcs[ii][1].setText(String.format("%.2f", las[ii]!=null?las[ii]:""));
                                //Log.v("lanakim", "los update : " + los[ii].toString());
                                rcs[ii][2].setText(String.format("%.2f", los[ii]!=null?los[ii]:""));
                                //Log.v("lanakim", "dis update : " + dis[ii].toString());
                                rcs[ii][3].setText(String.format("%.2f", dis[ii]!=0?dis[ii]:""));

                            }
                        } catch(Exception e){
                            Log.v("lanakim", e.getMessage().toString());
                            Log.v("lanakim", "ERROR!!!");
                        }

                    }
                });


                try {
                    sleep(cycle, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_monitor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onDestory()
    {
        tThread.interrupt();
        super.onDestroy();
    }
}
