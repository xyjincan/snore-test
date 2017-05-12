package demo.watchers.cc.myapplication.audioservice.utils;

import java.sql.Timestamp;

/**
 * Created by jc on 2017/5/11.
 */

public class TimeTools {

    public static Timestamp occerTime(Timestamp beginTime, Timestamp finishTime,int index,int length) {
        if(index<=0 || length<=0){
            return beginTime;
        }
        long span =  finishTime.getTime() -  beginTime.getTime();
        long t_index = (index*span)/length;
        return new Timestamp(beginTime.getTime()+t_index);
    }
}
