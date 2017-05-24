package cc.watchers.snoreview.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.List;

import cc.watchers.snoreview.R;
import cc.watchers.snoreview.audioservice.utils.DEV;
import cc.watchers.snoreview.audioservice.utils.TimeTools;
import cc.watchers.snoreview.db.SnoreLog;
import cc.watchers.snoreview.db.model.SnoreHistory;


/**
 * 录音记录列表
 */

public class RecordActivity extends AppCompatActivity {

    private static  List<SnoreHistory> snoreList;
    public static Context context;

    //private List<ApplicationInfo> mAppList;
    private AppAdapter mAdapter;


    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.i(DEV.TAG,"position:"+"按下了back键   onKeyDown()");
            goMainExit();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecordActivity.context = this;
        RecordActivity.snoreList = SnoreLog.getHistory();

        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);
        mAdapter = new AppAdapter();
        listView.setAdapter(mAdapter);
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                createMenu2(menu);
            }

            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                item2.setWidth(dp2px(90));
                item2.setIcon(R.drawable.ic_action_discard);
                menu.addMenuItem(item2);
            }

        };
        // set creator
        listView.setMenuCreator(creator);
        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                SnoreHistory snoreHistory = snoreList.get(position);
                deleteRecord(position);//数据中删除
                snoreList.remove(position);//内存中删除
                mAdapter.notifyDataSetChanged();
                afterDeleteRecord();
                return false;
            }
        });


    }



    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return snoreList.size();
        }

        @Override
        public SnoreHistory getItem(int position) {
            return snoreList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.item_list_app, null);
                new ViewHolder(convertView);
            }

            convertView.setTag(R.id.tag_first,position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//添加触摸事件，触摸查看内容详细报告
                    getDetailRecord((Integer)view.getTag(R.id.tag_first));
                }
            });

            ViewHolder holder = (ViewHolder) convertView.getTag();
            SnoreHistory snoreHistory = snoreList.get(position);
            holder.tv_name.setText(
                    TimeTools.getTime2Time(snoreHistory.getCreateTime(),snoreHistory.getLastUpdate())
            );
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
            public ViewHolder(View view) {
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(this);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }


    static public void getDetailRecord(int position){
        if(context!=null){
            SnoreHistory snoreHistory = snoreList.get(position);
            Log.i(DEV.TAG,"position:"+position);
            Log.i(DEV.TAG,"record detail:"+snoreList.get(position));
            Intent i = new Intent(context,ListViewBarChartActivity.class);
            i.putExtra(ListViewBarChartActivity.detailSnoreHistoryId,snoreHistory);
            context.startActivity(i);//查看分析详情
        }
    }

    static public void deleteRecord(int position){

        SnoreHistory snoreHistory = snoreList.get(position);
        Log.i(DEV.TAG,"position:"+position);
        Log.i(DEV.TAG,"record remove of inf:" +snoreHistory.toString());
        SnoreLog.deleteFileLog(snoreHistory);
    }


    public void afterDeleteRecord(){
            if(snoreList.size()==0){
                goMainExit();
            }
    }

    public void goMainExit(){
        //Activity返回时传递数据，也是通过意图对象
        Intent data = new Intent();
        //把要传递的数据封装至意图对象中
        //data.putExtra("name", "按下了back键");
        //当前Activity销毁时，data这个意图就会传递给启动当前Activity的那个Activity
        setResult(1, data);
        finish();
    }


}
