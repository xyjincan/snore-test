package cc.watchers.snoreview.audioservice;


//小时
//分钟段
//次数

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import cc.watchers.snoreview.db.model.SnoreEvent;

public class Show {
    public int hour;
    public Map<Integer, Integer> scounts = new HashMap<Integer, Integer>();//鼾声报表
    public Map<Integer, Integer> pcounts = new HashMap<Integer, Integer>();//呼吸暂停报表

    Timestamp lastAdd = null;

    public Show(int hour) {
        this.hour = hour;
    }

    public void addSnoreLog(SnoreEvent snoreEvent) {

        if (lastAdd == null) {
            lastAdd = snoreEvent.getLogTime();
            addReginCount(scounts,snoreEvent.getLogTime().getMinutes());
            return;
        }

        long timeLength = snoreEvent.getLogTime().getTime() - lastAdd.getTime();//ms
        //鼾声间距过小合并0.5*1000ms
        if(timeLength<500){
            return;
        }else if(timeLength>
                10000 && timeLength < 50000){
            //呼吸暂停
            addReginCount(scounts,snoreEvent.getLogTime().getMinutes());
            addReginCount(pcounts,snoreEvent.getLogTime().getMinutes());
        }else {
            //特大正常记录（更新时间）
            addReginCount(scounts,snoreEvent.getLogTime().getMinutes());
        }
    }

    public void addReginCount(Map<Integer, Integer> counts,int minute){

        int region=10;//获取声音区间10 - 60
        while (region<minute){
            region+=10;
        }
        Integer count = counts.get(region);
        if(count==null){
            counts.put(region,1);
        }else {
            counts.put(region,count+1);
        }
    }

}
        /*

        吸气 呼气 呼吸间隔期
        麻醉医学里一般认为正常的吸气时间为0.8-1.2s,呼气时间为0.5-1秒.
        也就是说正常的时间为1.3-2.2秒一次 但是还应该有个呼吸间隔期,大约为1-2秒
        正常成年人在平静时的呼吸频率约为每分钟16～20次 3-3.75
        一分钟15至18次，4秒就大概一次  3.3-4

        阻塞性睡眠呼吸暂停，一般是指每次发作时，口、鼻气流停止流通达10秒或更长时间

        呼吸阻断时间平均为25—30秒，有时可能超过1分钟
        每晚有200或更多次的呼吸暂停发作，伴有缺氧

        并伴有血氧饱和度下降等。成人每晚7小时的睡眠期间，发作次数常达30次以上。



        数据文件位于(sdcard/2017snore-test/*.txt)\m\n
        dbAudio,maxAudio,avgAudio,EK,amongTime(beginTime-finishTime)\n
        分贝,极值,均值,Ek=E1/E2(E1250-400Hz E2 600-750Hz) ,声音事件时间（取中值）

        * */
