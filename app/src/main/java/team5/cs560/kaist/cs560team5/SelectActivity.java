package team5.cs560.kaist.cs560team5;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import kr.ac.kaist.idb.snql.connector.ClientConnector;


public class SelectActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView selectListView;
    private ArrayList<String> proteges;
    private ArrayAdapter<String> protegesAdapter;
    private Button selectApply;
    private boolean[] checkboxes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Log.v("lanakim", "Start");
        selectListView = (ListView)findViewById(R.id.listView2);
        selectListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        selectApply = (Button)findViewById(R.id.selectApply);

        Log.v("lanakim", "1");

        proteges = new ArrayList<String>();

//        Intent intent = getIntent();
//        String name = intent.getStringExtra("name");
//        if(name != null){
//            proteges.add(name);
//        } else {
            /*
            add query results heere
             */
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
            int userSize = mPref.getInt("userSize", 0);

            for(int i=0; i<userSize; i++) {
                proteges.add(mPref.getString("user"+i, "default"));
            }
//            new ProcessGetUser().execute(null, null, null);
//        }

        checkboxes = new boolean[proteges.size()];
        Arrays.fill(checkboxes, false);

        Log.v("lanakim", "2");

        protegesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, proteges);

        Log.v("lanakim", "3");

        selectListView.setAdapter(protegesAdapter);

        Log.v("lanakim", "4");

        selectApply.setOnClickListener((View.OnClickListener) this);

        Log.v("lanakim", "5");

        selectListView.setOnItemClickListener(this);


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
                ClientConnector clientConnector = ListenerService.getServiceObject().getClientConnector();
                clientConnector.executeQuery(queryStmt);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void onClick(View v)
    {
        SparseBooleanArray positions = selectListView.getCheckedItemPositions();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        int size=positions.size();
        int i=0;
        int n = 0;
        String a = "";
        for(i = 0; i <= size; ++i){
            if(positions.get(i))
            {
                editor.putString("protege"+n, selectListView.getItemAtPosition(i).toString());
                a += selectListView.getItemAtPosition(i).toString();
                n++;
            }
        }
        editor.putInt("protegeno", n);
        //Toast.makeText(this, a.toString(), Toast.LENGTH_SHORT).show();
        editor.commit();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select, menu);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    //    selectListView.setItemChecked(position, true^selectListView.g);
        //Toast.makeText(this, "HELLO", Toast.LENGTH_SHORT).show();
        checkboxes[position] = true^checkboxes[position];
        selectListView.setItemChecked(position, checkboxes[position]);
    }
}
