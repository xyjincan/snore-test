package demo.watchers.cc.myapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import demo.watchers.cc.myapplication.audioservice.utils.L;
import demo.watchers.cc.myapplication.db.SnoreHistory;
import demo.watchers.cc.myapplication.db.SnoreLog;


public class MainActivity extends AppCompatActivity {

    AudioRecordThread artrun = null;
    Thread thread = null;
    boolean canStart = true;

    private TextView mTextMessage;

    private void initAudioRecord(){
        artrun = new AudioRecordThread();
        thread = new Thread(artrun);
        canStart = true;
    }
    private void clearAudioRecord(){
        artrun = null;
        thread = null;
        canStart = false;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    mTextMessage.setText(R.string.title_home);
                    return true;
                }
                case R.id.navigation_start: {
                    mTextMessage.setText(R.string.title_start);
                    Log.i(L.TAG+"start record", "This is Verbose.");
                    if(canStart){
                        initAudioRecord();
                        canStart=false;
                        thread.start();
                    }
                    return true;
                }
                case R.id.navigation_stop: {
                    mTextMessage.setText(R.string.title_stop);
                    Log.i(L.TAG+"stop record", "This is Verbose.");
                    if(!canStart){
                        artrun.stop();
                        clearAudioRecord();
                        canStart=true;
                    }
                    return true;
                }
                case R.id.navigation_history: {

                    StringBuilder stringBuilder = new StringBuilder();
                    List<SnoreHistory> list = SnoreLog.getHistory();
                    for(SnoreHistory s:list){
                        Log.i(L.TAG+"list:", s.toString());
                        stringBuilder.append(s.toString());
                        stringBuilder.append("\n");
                        stringBuilder.append("\n");
                        stringBuilder.append("\n");
                    }
                    mTextMessage.setText(stringBuilder.toString());
                    Intent i = new Intent(MainActivity.this,ScrollingActivity.class);startActivity(i);
                    return true;
                }
                default:{
                    return false;
                }
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //R.color
        //R.string.app_name


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Log.v(L.TAG+ new Date() + "app start:", "true");

        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Util.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Util.requestPermission(this, Manifest.permission.WAKE_LOCK);

    }

}

/*
                    Toast toast = Toast.makeText(MainActivity.this,R.string.title_home, Toast.LENGTH_SHORT);
                    toast.setText(R.string.title_home);
                    toast.show();
*/