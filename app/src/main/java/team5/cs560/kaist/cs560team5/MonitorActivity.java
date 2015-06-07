package team5.cs560.kaist.cs560team5;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class MonitorActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        // Preference file
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        int pn = mPref.getInt("protegeno", 0);
        //Toast.makeText(this, json_str, Toast.LENGTH_SHORT).show();
        try {
            String a = "";
            List<String> proteges_list = new ArrayList<String>();
            int i;
            for(i = 1; i <= pn; ++i)
            {
                proteges_list.add(mPref.getString("protege"+i, null));
                a += mPref.getString("protege"+i, null);
            }
            //Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        Float hr = mPref.getFloat("hr", 0);
        Float dist = mPref.getFloat("dist", 0);
        Float la1 = mPref.getFloat("la1", 0);
        Float lo1 = mPref.getFloat("lo1", 0);
        Float la2 = mPref.getFloat("la2", 0);
        Float lo2 = mPref.getFloat("lo2", 0);

        ////

        




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
}
