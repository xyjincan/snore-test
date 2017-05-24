package cc.watchers.snoreview.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import cc.watchers.snoreview.R;
import cc.watchers.snoreview.audioservice.AudioRecordThread;
import cc.watchers.snoreview.audioservice.utils.DEV;
import cc.watchers.snoreview.db.SnoreLog;
import cc.watchers.snoreview.db.model.SnoreHistory;


public class MainActivity extends AppCompatActivity {

    public static Context context;
    private int infIndex=0;

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

            requestPermission(MainActivity.this);
            if(checkPermission(MainActivity.this)){
                return false;
            }

            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    Log.i(DEV.TAG+"鼾声分析", "点击");
                    sendText("设备录音（44100Hz）缓存最小值:"+ AudioRecordThread.getDeviceAudioMinBufferSize());
                    mTextMessage.setText(R.string.title_home_inf);
                    infIndex=0;
                    return true;
                }
                case R.id.navigation_start: {
                    Log.i(DEV.TAG+"正在录音", "点击");

                    mTextMessage.setText(R.string.title_start_inf);

                    if (canStart  ) {
                        mTextMessage.setText(R.string.title_start_inf);
                        initAudioRecord();
                        canStart = false;
                        thread.start();
                        infIndex=1;
                    }else {
                        if(infIndex!=0) {
                            mTextMessage.setText(R.string.title_stop_inf);
                            artrun.stop();
                            clearAudioRecord();
                            canStart = true;
                        }
                        infIndex=2;
                    }

                    return true;
                }
/*                case R.id.navigation_stop: {
                    Log.i(DEV.TAG+"停止录音", "点击");
                    mTextMessage.setText(R.string.title_stop_inf);
                    if(!canStart){
                        artrun.stop();
                        clearAudioRecord();
                        canStart=true;
                    }
                    return true;
                }*/
                case R.id.navigation_history: {
                    Log.i(DEV.TAG+"查看历史记录", "点击");
                    StringBuilder stringBuilder = new StringBuilder();
                    List<SnoreHistory> list = SnoreLog.getHistory();
/*                    for(SnoreHistory s:list){
                        //Log.i(DEV.TAG+"list:", s.toString());
                        stringBuilder.append(s.toString());
                        stringBuilder.append("\n");
                        stringBuilder.append("\n");
                        stringBuilder.append("\n");
                    }*/
                    if(list.size()!=0) {
                        mTextMessage.setText(stringBuilder.toString());
                        Intent i = new Intent(MainActivity.this, RecordActivity.class);
                        startActivityForResult(i,1);
                    }else {
                        mTextMessage.setText(R.string.title_history_no_inf);
                    }
                    return false;
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

        requestPermission(MainActivity.this);
    /*    requestPermission(this, Manifest.permission.RECORD_AUDIO);
        requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        requestPermission(this, Manifest.permission.WAKE_LOCK);
    */
    }


    public void sendText(String inf){
        Toast toast = Toast.makeText(context,inf, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(DEV.TAG,"Activity Back");
        mTextMessage = (TextView) findViewById(R.id.message);
        switch (infIndex){
            case 0: {
                mTextMessage.setText(R.string.title_home_inf);
                break;
            }
            case 1: {
                mTextMessage.setText(R.string.title_start_inf);
                break;
            }
            case 2: {
                mTextMessage.setText(R.string.title_stop_inf);
                break;
            }
            default:{
                finish();
            }
        }

        //finish();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.i(DEV.TAG,"用户点击返回键:"+"退出 onKeyDown()");
            clearAndExit();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
    public void clearAndExit(){
        sendText("已退出录音睡眠分析");
        if(artrun!=null){
            artrun.stop();
        }
        finish();
    }


    public  void requestPermission(Activity activity) {

        if(
                checkPermission(activity)
        )
        {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WAKE_LOCK
            }, 0);
        }

    }

    boolean checkPermission(Activity activity) {
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(activity, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED);
    }


}

/*

*/