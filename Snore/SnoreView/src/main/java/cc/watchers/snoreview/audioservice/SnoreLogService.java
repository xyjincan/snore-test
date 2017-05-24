package cc.watchers.snoreview.audioservice;

import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.watchers.snoreview.audioservice.utils.DEV;
import cc.watchers.snoreview.audioservice.utils.FileTools;
import cc.watchers.snoreview.audioservice.utils.TimeTools;
import cc.watchers.snoreview.db.model.SnoreEvent;
import cc.watchers.snoreview.db.model.SnoreHistory;

// 鼾声记录运行日志解析

public class SnoreLogService {

    public static Map<Integer, Show>  readLogFile(SnoreHistory snoreHistory) {

        String filepath = FileTools.getDataFilePath() + snoreHistory.getLogFile();
        String[] logs = FileTools.read(filepath, null);
        Log.i(DEV.TAG, "SnoreLogService log size:" + logs.length);
        List<SnoreEvent> list = new ArrayList<SnoreEvent>();
        for (String log : logs) {
            String[] detail = log.split(",");
            SnoreEvent snoreEvent = new SnoreEvent();
            snoreEvent.setSoundDb(Double.valueOf(detail[0]));
            snoreEvent.setMaxAudio(Integer.valueOf(detail[1]));
            snoreEvent.setAvgAudio(Integer.valueOf(detail[2]));
            snoreEvent.setEk(Double.valueOf(detail[3]));
            snoreEvent.setLogTime(new Timestamp(TimeTools.getLongByViewDate(detail[4])));
            list.add(snoreEvent);
        }
        Log.i(DEV.TAG, "formatData map init:" + list.size());
        Map<Integer, Show> shows = formatData(list);
        return shows;

    }


    //小时
    //分钟段
    //次数

    public static Map<Integer, Show> formatData(List<SnoreEvent> list) {

        Map<Integer, Show> shows = new HashMap<Integer, Show>();
        if (list == null || list.size() == 0) {
            return shows;
        }
        int startHour = list.get(0).getLogTime().getHours();
        int stopHour = list.get(list.size() - 1).getLogTime().getHours();

        Log.i(DEV.TAG, "startHour:" + startHour +" stopHour"+stopHour);
        shows.put(-1,new Show(startHour));//特殊标记开始时间

        for (int i = startHour; i <= stopHour; i++) {
            Show show = new Show(i);
            shows.put(i, show);
        }
        for (SnoreEvent snoreEvent : list) {
            Show show = shows.get(snoreEvent.getLogTime().getHours());
            show.addSnoreLog(snoreEvent);
        }
        return shows;
    }

}
