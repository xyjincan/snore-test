package cc.watchers.snoreview.audioservice.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by jc on 2017/5/11.
 */

public class TimeTools {

    private static final String timeformatOFAndroid = "y-M-d H:mm:ss.SSS";

    public static Timestamp occerTime(Timestamp beginTime, Timestamp finishTime,int index,int length) {
        if(index<=0 || length<=0){
            return beginTime;
        }
        long span =  finishTime.getTime() -  beginTime.getTime();
        long t_index = (index*span)/length;
        return new Timestamp(beginTime.getTime()+t_index);
    }

    static private Long getLongByViewDate(String viewdate) {

        if(viewdate==null || viewdate.equals("null")){
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(timeformatOFAndroid);// 设置日期格式
        try {
            return df.parse(viewdate).getTime();
        } catch (ParseException e) {
            // 时间解析失败,,, AndroidAPP直接崩溃
        }
        return null;
    }

    public static String getTime2Time(String beginTime, String finishTime){

        StringBuilder itemname = new StringBuilder();
        String[] base = beginTime.split(" ");
        itemname.append("睡眠记录 "+ base[0] +" ");
        Long lbegin = getLongByViewDate(beginTime);
        Long lfinish = getLongByViewDate(finishTime);
        if(lbegin!=null){
            Timestamp begin = new Timestamp(lbegin);
            itemname.append(begin.getHours()+":"+begin.getMinutes()+ "-");
        }
        if(lfinish!=null){
            Timestamp finish = new Timestamp(lfinish);
            itemname.append(finish.getHours()+":"+finish.getMinutes());
        }else {
            itemname.append("无相关数据");
        }
        return itemname.toString();
    }

}
