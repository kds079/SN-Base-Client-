package team5.cs560.kaist.cs560team5;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.TextView;

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
    private TextView mtView = null;
    private Bitmap bmap;
    private Canvas cvas;
    private Paint paint;

    private float downx =0,downy=0, upx=0, upy=0;


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
        imageInside = (ImageView) findViewById(R.id.mapimage1);
        imageOutside = (ImageView) findViewById(R.id.outside_imageview);
        mtView = (TextView) findViewById(R.id.mtextView);
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        float dw = currentDisplay.getWidth();
        float dh = currentDisplay.getHeight();

        bmap = Bitmap.createBitmap((int) dw, (int) dh, Bitmap.Config.ARGB_8888);
        cvas = new Canvas(bmap);
        paint = new Paint();
        paint.setColor(Color.RED);
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
                                mtView.setText("Coord: " + x + ", " + y + " tc: " + tcount);
                                la[tcount] = x;
                                lo[tcount] = y;
                                cvas.drawCircle(x, y, 7, paint);
                                if (tcount == 1) {
                                    cvas.drawRect(la[0],lo[0],la[1], lo[1],paint);
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
                ListenerService listenerService = ListenerService.getServiceObject();
                ClientConnector clientConnector = listenerService.getClientConnector();

                String queryStmt = "CREATE EVENT heart_rate_event_1\n"
                        + "FROM node WHEN hr < 100";
                PlanKey planKey = clientConnector.executeQuery(queryStmt);

                queryStmt = "ON EVENT (heart_rate_event_1, 3s, 120s, REPEAT)\n"
                        + "SELECT name, hr, latitude, longitude, timestamp() FROM node, profile, gps";
//				+ "WHERE name='test kim'";
                planKey = clientConnector.executeQuery(queryStmt);
                listenerService.setQueryMap(planKey, "HrEvent");
                Log.v("dskim", "==>>>  planKey : " + planKey + "Query time : " + new Timestamp(new Date().getTime()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
