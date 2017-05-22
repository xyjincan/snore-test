package cc.watchers.snoreview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import cc.watchers.snoreview.activity.RecordActivity;
import cc.watchers.snoreview.audioservice.utils.Util;
import cc.watchers.snoreview.audioservice.AudioRecordThread;
import cc.watchers.snoreview.audioservice.utils.DEV;
import cc.watchers.snoreview.db.model.SnoreHistory;
import cc.watchers.snoreview.db.SnoreLog;


public class MainActivity extends AppCompatActivity {

    public static Context context;


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
                    Log.i(DEV.TAG+"鼾声分析", "This is Verbose.");
                    sendTest("设备录音缓存最小值（44100）:"+ AudioRecordThread.getDeviceAudioMinBufferSize());
                    mTextMessage.setText(R.string.title_home_inf);
                    return true;
                }
                case R.id.navigation_start: {
                    Log.i(DEV.TAG+"正在录音", "This is Verbose.");
                    mTextMessage.setText(R.string.title_start_inf);
                    if (canStart) {
                        initAudioRecord();
                        canStart = false;
                        thread.start();
                    }
                    return true;
                }
                case R.id.navigation_stop: {
                    Log.i(DEV.TAG+"停止录音", "This is Verbose.");
                    mTextMessage.setText(R.string.title_stop_inf);
                    if(!canStart){
                        artrun.stop();
                        clearAudioRecord();
                        canStart=true;
                    }
                    return true;
                }
                case R.id.navigation_history: {
                    Log.i(DEV.TAG+"查看历史记录", "This is Verbose.");
                    StringBuilder stringBuilder = new StringBuilder();
                    List<SnoreHistory> list = SnoreLog.getHistory();
                    for(SnoreHistory s:list){
                        //Log.i(DEV.TAG+"list:", s.toString());
                        stringBuilder.append(s.toString());
                        stringBuilder.append("\n");
                        stringBuilder.append("\n");
                        stringBuilder.append("\n");
                    }
                    if(list.size()!=0) {
                        mTextMessage.setText(stringBuilder.toString());
                        Intent i = new Intent(MainActivity.this, RecordActivity.class);
                        startActivity(i);
                    }else {
                        mTextMessage.setText(R.string.title_history_no_inf);
                    }
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context=this;
        Log.i(DEV.TAG, new Date() + "开启应用 start:"+MainActivity.context);

        mTextMessage = (TextView) findViewById(R.id.message);
        mTextMessage.setText(R.string.title_home_inf);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Util.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Util.requestPermission(this, Manifest.permission.WAKE_LOCK);


    }


    public void sendTest(String inf){
        if(context==null){
            Log.i(DEV.TAG, new Date() + "永不应该出现的日志 弹框失败"+MainActivity.context);
            return;
        }
        Toast toast = Toast.makeText(context,inf, Toast.LENGTH_SHORT);
        toast.show();
    }

}

/*

*/