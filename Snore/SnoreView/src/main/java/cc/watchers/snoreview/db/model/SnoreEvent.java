package cc.watchers.snoreview.db.model;

import java.sql.Timestamp;

        /*

        数据文件位于(sdcard/2017snore-test/*.txt)\m\n
        dbAudio,maxAudio,avgAudio,EK,amongTime(beginTime-finishTime)\n
        分贝,极值,均值,Ek=E1/E2(E1250-400Hz E2 600-750Hz) ,声音事件时间（取中值）

        * */


public class SnoreEvent {

    @Override
    public String toString(){
        return "soundDb:"+soundDb+"  " + logTime;
    }

    private double soundDb;
    private int maxAudio;
    private int avgAudio;
    private double ek;
    private Timestamp logTime;


    public double getSoundDb() {
        return soundDb;
    }

    public void setSoundDb(double soundDb) {
        this.soundDb = soundDb;
    }

    public int getMaxAudio() {
        return maxAudio;
    }

    public void setMaxAudio(int maxAudio) {
        this.maxAudio = maxAudio;
    }

    public int getAvgAudio() {
        return avgAudio;
    }

    public void setAvgAudio(int avgAudio) {
        this.avgAudio = avgAudio;
    }

    public double getEk() {
        return ek;
    }

    public void setEk(double ek) {
        this.ek = ek;
    }

    public Timestamp getLogTime() {
        return logTime;
    }

    public void setLogTime(Timestamp logTime) {
        this.logTime = logTime;
    }




}
