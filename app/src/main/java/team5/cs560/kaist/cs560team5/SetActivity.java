package team5.cs560.kaist.cs560team5;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.sql.Timestamp;
import java.util.Date;

import kr.ac.kaist.idb.snql.connector.ClientConnector;
import kr.ac.kaist.idb.snql.planner.PlanKey;


public class SetActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText hr;
    private EditText dist;
    private float la[];
    private float lo[];
    private ImageView imageInside;
    private ImageView imageOutside;
    private Button applySetting;
    private int tcount;
    //private TextView mtView = null;
    private Bitmap bmap;
    private Canvas cvas;
    private Paint paintR;
    private Paint paintC;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        tcount = 0;
        hr = (EditText)findViewById(R.id.editHR);
        dist = (EditText)findViewById(R.id.editDIST);
        la = new float[2];
        lo = new float[2];
        imageInside = (ImageView) findViewById(R.id.inside_imageview);
        imageOutside = (ImageView) findViewById(R.id.outside_imageview);
        //mtView = (TextView) findViewById(R.id.mtextView);
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        currentDisplay.getSize(size);
        int dw = size.x;
        int dh = size.y;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageInside.getLayoutParams();
        params.width = 720;
        params.height = 720;
        imageInside.setLayoutParams(params);

        bmap = Bitmap.createBitmap((int) dw, (int) (dh*0.7), Bitmap.Config.ARGB_8888);
        //bmap.eraseColor(Color.GRAY);
        cvas = new Canvas(bmap);
        paintR = new Paint();
        paintR.setColor(Color.argb(130, 200, 200, 200));
        paintC = new Paint();
        paintC.setColor(Color.argb(200, 255, 0, 0));
        imageOutside.setImageBitmap(bmap);
        //mdView = (View)findViewById(R.id.mView1);
        //mtipView = (TipsView) findViewById(R.id.mTipsView);

        //mtipView.setOnTouchListener();

        imageOutside.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent Ev) {
                //mtipView.setOnTouchListener(this);
                if (view == findViewById(R.id.outside_imageview)) {
                    switch (Ev.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //case MotionEvent.ACTION_MOVE:
                            if (tcount < 2) {

                                float x = Ev.getX();
                                float y = Ev.getY();
                                //mtView.setText("Coord: " + x + ", " + y + " tc: " + tcount);
                                la[tcount] = x;
                                lo[tcount] = y;
                                cvas.drawCircle(x, y, 12, paintC);
                                if (tcount == 1) {
                                    cvas.drawRect(la[0],lo[0],la[1], lo[1],paintR);
                                }
                            }
                            tcount++;
                    }
                }
                return true;
            }

        });


        applySetting = (Button)findViewById(R.id.settingApply);
        applySetting.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set, menu);
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

    @Override
    public void onClick(View v) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();

        try {
            editor.putFloat("hr", Float.parseFloat(hr.getText().toString()));
            editor.putFloat("dist", Float.parseFloat(dist.getText().toString()));
            editor.putFloat("lo1", lo[0]);
            editor.putFloat("lo2", lo[1]);
            editor.putFloat("la1", la[0]);
            editor.putFloat("la2", la[1]);

    //r        editor.putFloat("la1", Float.parseFloat(la1.getText().toString()));
    //        editor.putFloat("lo1", Float.parseFloat(lo1.getText().toString()));
    //        editor.putFloat("la2", Float.parseFloat(la2.getText().toString()));
    //        editor.putFloat("lo2", Float.parseFloat(lo2.getText().toString()));
            editor.commit();
        }catch(Exception e_empty){
            ;//not commit();
        }
        MainActivity.isSetFlag = true;
        Log.d("jplee", "here3?");

        new ProcessSetHrEvent().execute(null, null, null);
        finish();
    }

    private class ProcessSetHrEvent extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Float hr = mPref.getFloat("hr", 0);
                Float dist = mPref.getFloat("dist", 0);
                Float la1 = mPref.getFloat("la1", 0);
                Float lo1 = mPref.getFloat("lo1", 0);
                Float la2 = mPref.getFloat("la2", 0);
                Float lo2 = mPref.getFloat("lo2", 0);

                ListenerService listenerService = ListenerService.getServiceObject();
                ClientConnector clientConnector = listenerService.getClientConnector();

                String queryStmt = null;
                PlanKey planKey = null;
                //Event query for hr, region
//                String queryStmt = "CREATE EVENT heart_rate_event_1\n"
//                        + "FROM node WHEN hr < " + hr;// + " AND " + ;
//                PlanKey planKey = clientConnector.executeQuery(queryStmt);
//
//                queryStmt = "ON EVENT (heart_rate_event_1, 3s, 120s, REPEAT)\n"
//                        + "SELECT name, hr, latitude, longitude, timestamp() FROM node, profile, gps";
////				+ "WHERE name='test kim'";
//                planKey = clientConnector.executeQuery(queryStmt);
//                listenerService.setQueryMap(planKey, "HrEvent");
//                Log.v("dskim", "==>>>  planKey : " + planKey + "Query time : " + new Timestamp(new Date().getTime()));

                //Select query for distance event
                queryStmt = "SELECT name, latitude, longitude, hr, timestamp()\n"
                        + "FROM profile, gps, node";
                planKey = clientConnector.executeQuery(queryStmt);
                listenerService.setQueryMap(planKey, "DistEvent");

//                queryStmt = "SELECT name, latitude, longitude, timestamp()\n"
//				        + "FROM profile, gps";
//        		planKey = clientConnector.executeQuery(queryStmt);
//                listenerService.setQueryMap(planKey, "DistEvent");
                Log.v("dskim", "==>>>  planKey : " + planKey + "Query time for distance event : " + new Timestamp(new Date().getTime()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
