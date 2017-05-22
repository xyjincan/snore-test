
package cc.watchers.snoreview.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.watchers.snoreview.R;
import cc.watchers.snoreview.audioservice.Show;
import cc.watchers.snoreview.audioservice.SnoreLogService;
import cc.watchers.snoreview.audioservice.utils.DEV;
import cc.watchers.snoreview.audioservice.utils.TimeTools;
import cc.watchers.snoreview.db.model.SnoreHistory;

/**
 * 录音结果显示
 */
public class ListViewBarChartActivity extends DemoBase {

    public static final String detailSnoreHistoryId = "detailSnoreHistoryId";

    Map<Integer,Show> map = null;
    int hourStart = -1;

    private TextView recordtittle;
    private TextView recordtime;
    private TextView recordinfmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_listview_chart);

        recordtime = (TextView) findViewById(R.id.recordtime);
        recordinfmation = (TextView) findViewById(R.id.recordinfmation);

        SnoreHistory snoreHistory =(SnoreHistory)getIntent().getSerializableExtra(detailSnoreHistoryId);
        Log.i(DEV.TAG,"ListViewBarChartActivity snoreHistory:"+snoreHistory);
        recordtime.setText(TimeTools.getTime2Time(snoreHistory.getCreateTime(),snoreHistory.getLastUpdate()));
        countWarning=0;

        map = SnoreLogService.readLogFile(snoreHistory);
        if(map.size()>0){

            hourStart = map.get(-1).hour;
            Log.i(DEV.TAG,"ListViewBarChartActivity map size:"+map.size());

            ListView lv = (ListView) findViewById(R.id.listView1);
            ArrayList<BarData> list = new ArrayList<BarData>();
            // 20 items
            for (int i = 0; i < (map.size()-1)*2; i++) {
                list.add(generateData(i + 1));
            }
            //
            ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
            lv.setAdapter(cda);
            recordinfmation.setText(getWarningInf());
        }
    }

    private class ChartDataAdapter extends ArrayAdapter<BarData> {

        public ChartDataAdapter(Context context, List<BarData> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BarData data = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_barchart, null);
                holder.chart = (BarChart) convertView.findViewById(R.id.chart);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // apply styling
            data.setValueTypeface(mTfLight);
            data.setValueTextColor(Color.BLACK);
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setDrawGridBackground(false);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxisPosition.BOTTOM);
            xAxis.setTypeface(mTfLight);
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setTypeface(mTfLight);
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(15f);

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setTypeface(mTfLight);
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(15f);

            // set data
            holder.chart.setData(data);
            holder.chart.setFitBars(true);
            // do not forget to refresh the chart
//            holder.chart.invalidate();
            holder.chart.animateY(700);

            return convertView;
        }

        private class ViewHolder {
            BarChart chart;
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     * @return
     */
    private BarData generateData(int cnt) {

        int item = 0;
        if (cnt % 2 == 1) {
            item = (cnt / 2 + 1);
        }else {
            item = ((cnt - 1) / 2 + 1);
        }
        int realHour = item+hourStart-1;
        Show show = map.get(realHour);
        Map<Integer, Integer> counts = null;
        if (cnt % 2 == 1) {
            counts = show.scounts;//鼾声数据
        }else {
            counts = show.pcounts;//呼吸暂停数据
        }

        int tsum=0;
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        for (int i = 0; i < 6; i++) {
            int key = (i+1)*10;
            Integer value = counts.get(key);
            if (null==value){
                value=0;
            }else{
                tsum+=value;
            }
            entries.add(new BarEntry(key, value));
        }
        BarDataSet d = null;
        if (cnt % 2 == 1) {
            d = new BarDataSet(entries, "表" + cnt + " 鼾声记录" +realHour+"(小时中每十分钟)" );
            d.setColors(VORDIPLOM_COLORS);
        } else {
            countWarning+=tsum;
            d = new BarDataSet(entries, "表" + cnt + " 呼吸暂停"+realHour +"(小时中每十分钟)" );
            d.setColors(VORDIPLOM_COLORS_WARM);
        }
        d.setBarShadowColor(Color.rgb(203, 203, 203));
        ArrayList<IBarDataSet> sets = new ArrayList<IBarDataSet>();
        sets.add(d);
        BarData cd = new BarData(sets);
        cd.setBarWidth(0.9f);
        return cd;
    }

    public void sendTest(String inf){
        Toast toast = Toast.makeText(this,inf, Toast.LENGTH_SHORT);
        toast.show();
    }


    private int countWarning=0;

    private static final int[] VORDIPLOM_COLORS = {Color.rgb(140, 234, 255)};
    private static final int[] VORDIPLOM_COLORS_WARM = {Color.rgb(255, 0, 0)};

    String getWarningInf(){
        return  "检测呼吸暂停次数："+countWarning;
    }

}


/*            int[] VORDIPLOM_COLORS = {
                    Color.rgb(138,43,226),Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
                    Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
            };*/