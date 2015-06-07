package team5.cs560.kaist.cs560team5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class SetActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText hr;
    private EditText dist;
    private float la[];
    private float lo[];
    private ImageView im1;
    private Button applySetting;
    private int tcount;
    private TextView mtView = null;
   // private View mdView = null;
    //private TipsView mtipView = null;

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
     //   la1 = (EditText)findViewById(R.id.editLa1);
     //   lo1 = (EditText)findViewById(R.id.editLo1);
     //   la2 = (EditText)findViewById(R.id.editLa2);
     //   lo2 = (EditText)findViewById(R.id.editLo2);
        im1 = (ImageView) findViewById(R.id.mapimage1);
        mtView = (TextView) findViewById(R.id.mtextView);
        //mdView = (View)findViewById(R.id.mView1);
        //mtipView = (TipsView) findViewById(R.id.mTipsView);

        //mtipView.setOnTouchListener();

        im1.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent Ev) {
                //mtipView.setOnTouchListener(this);
                if (view == findViewById(R.id.mapimage1)) {

                    switch(Ev.getAction()){
                        case MotionEvent.ACTION_DOWN:
                        //case MotionEvent.ACTION_MOVE:
                            if(tcount < 2) {

                                float x = Ev.getX();
                                float y = Ev.getY();
                                mtView.setText("Coord: " + x + ", " + y + " tc: " + tcount);
                                lo[tcount] = x;
                                lo[tcount] = y;
                                tcount++;

                            }

                    }
                    /*
                    if(tcount < 2) {
                        switch

                        //Log.d("tag", "click" + x + ", " + y);
                    }
                    */
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
            editor.putFloat("lo0", lo[0]);
            editor.putFloat("lo1", lo[1]);
            editor.putFloat("la0", la[0]);
            editor.putFloat("la1", la[1]);

    //r        editor.putFloat("la1", Float.parseFloat(la1.getText().toString()));
    //        editor.putFloat("lo1", Float.parseFloat(lo1.getText().toString()));
    //        editor.putFloat("la2", Float.parseFloat(la2.getText().toString()));
    //        editor.putFloat("lo2", Float.parseFloat(lo2.getText().toString()));
            editor.commit();
        }catch(Exception e_empty){
            ;//not commit();
        }
        Log.d("tag", "here3?");

        finish();
    }
}
