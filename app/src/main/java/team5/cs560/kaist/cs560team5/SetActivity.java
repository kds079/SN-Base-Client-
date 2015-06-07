package team5.cs560.kaist.cs560team5;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class SetActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText hr;
    private EditText dist;
    private EditText la1;
    private EditText lo1;
    private EditText la2;
    private EditText lo2;
    private Button applySetting;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
/*
        hr = (EditText)findViewById(R.id.settingHR);
        dist = (EditText)findViewById(R.id.settingDist);
        la1 = (EditText)findViewById(R.id.setingLa1);
        lo1 = (EditText)findViewById(R.id.settingLo1);
        la2 = (EditText)findViewById(R.id.settingLa2);
        lo2 = (EditText)findViewById(R.id.settingLo2);
        applySetting = (Button)findViewById(R.id.settingApply);
*/
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

        editor.putFloat("hr", Float.parseFloat(hr.getText().toString()));
        editor.putFloat("dist", Float.parseFloat(dist.getText().toString()));
        editor.putFloat("la1", Float.parseFloat(la1.getText().toString()));
        editor.putFloat("lo1", Float.parseFloat(lo1.getText().toString()));
        editor.putFloat("la2", Float.parseFloat(la2.getText().toString()));
        editor.putFloat("lo2", Float.parseFloat(lo2.getText().toString()));

        editor.commit();

        finish();
    }
}
