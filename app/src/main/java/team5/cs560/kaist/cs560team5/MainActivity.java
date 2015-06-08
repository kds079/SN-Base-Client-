package team5.cs560.kaist.cs560team5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements OnItemClickListener {

    private ListView listView;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    public static boolean isSetFlag = false;
    public static boolean isDestroy = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // author: Jaeseong
        // to Service connection to DB and notification
//        Intent Service = new Intent(this, notiService.class);
//        startService(Service);

        Intent listenerService = new Intent(this, ListenerService.class);
        startService(listenerService);

        listView = (ListView)findViewById(R.id.listView);

        items = new ArrayList<String>();
        items.add("Select Proteges ");
        items.add("Set Thresholds");
        items.add("Monitor");

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener((OnItemClickListener) this);
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        super.onDestroy();
    }

    public void onStart()
    {
        super.onStart();


    }

    public void onItemClick(AdapterView<?> partent, View v, final int position, long id)
    {
        //
        String str = String.valueOf(position);
        Intent intent;
        if(position == 0)
        {
            intent = new Intent(this, SelectActivity.class);
            startActivity(intent);
        }
        else if(position == 1)
        {
            intent = new Intent(this, SetActivity.class);
            startActivity(intent);
        }
        else if(position == 2)
        {
            intent = new Intent(this, MonitorActivity.class);
            startActivity(intent);
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
