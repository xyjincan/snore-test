package demo.watchers.cc.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import demo.watchers.cc.myapplication.audioservice.utils.L;
import demo.watchers.cc.myapplication.db.SnoreHistory;
import demo.watchers.cc.myapplication.db.SnoreLog;

public class ScrollingActivity extends AppCompatActivity {

    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        StringBuilder stringBuilder = new StringBuilder();
        List<SnoreHistory> list = SnoreLog.getHistory();
        for(SnoreHistory s:list){
            Log.i(L.TAG+"list:", s.toString());
            stringBuilder.append(s.toString());
            stringBuilder.append("\n");
            stringBuilder.append("\n");
            stringBuilder.append("\n");
        }
        mTextMessage = (TextView) findViewById(R.id.scroollingmessage);
        mTextMessage.setText(stringBuilder.toString());
    }
}
