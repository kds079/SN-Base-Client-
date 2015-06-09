package team5.cs560.kaist.cs560team5;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MonitorActivity extends ActionBarActivity {
    private TableLayout monitorTable;
    private TableRow[] rowList;
    private TextView[] nameList;
    private TextView[] hrList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);



        monitorTable = (TableLayout)findViewById(R.id.monitorTable);
        if(monitorTable == null)
            Log.v("Lanakim", "table layout not found");

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

        Log.v("Lanakim", "1");
        //For Test
        pn = 3;

        rowList = new TableRow[pn];
        nameList = new TextView[pn];
        hrList = new TextView[pn];

        int i = 0;



        Log.v("Lanakim", "1.1");


        for(i = 0; i < pn; ++i)
        {
            nameList[i] = new TextView(this);
            nameList[i].setText("Name");
            //nameList[i].setId(1000 + i);
            //nameList[i].setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            hrList[i] = new TextView(this);
            hrList[i].setText("99.99");
            //hrList[i].setId(2000 + i);
            //hrList[i].setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        }



        Log.v("Lanakim", "2");


        for(i = 0; i < pn; ++i)
        {
            rowList[i] = new TableRow(this);
            Log.v("Lanakim", "2.1");
            rowList[i].addView(nameList[i]);
            Log.v("Lanakim", "2.2");
            rowList[i].addView(hrList[i]);
            Log.v("Lanakim", "2.3");
            monitorTable.addView(rowList[i]);
            Log.v("Lanakim", "2.4");
        }

        Log.v("Lanakim", "3");





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
        Log.d("tags","La/lo2: "+la2+","+lo2);
        //mtView.setText("hr: "+hr+", dist: "+dist+"\n la1"+la1+" lo1 "+lo1+" la2 "+la2+" lo2 "+lo2);
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
