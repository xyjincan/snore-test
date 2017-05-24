package cc.watchers.snoreview.audioservice;

import java.sql.Timestamp;

import cc.watchers.snoreview.audioservice.utils.DEV;
import cc.watchers.snoreview.audioservice.utils.FINALV;

/**
 * 有声检测
 */

public class SoundCheck {

    public static final int QUIET_LEVEL  = FINALV.QUIET_LEVEL;//定义“平静”声音的等级 100 means: 25ms(声音大于均值)  ,4000Hz 1000ms

    private short[] shortArr;
    private Timestamp beginTime, finishTime;
    public SoundCheck(){
        super();
    }

    public SoundCheck(short[] shortArr, Timestamp beginTime, Timestamp finishTime) {
        super();
        this.shortArr = shortArr;
        this.beginTime = beginTime;
        this.finishTime = finishTime;
    }

    private static void maxVolAndavgVol(short[] audiodata, int size) {

        int max = 0;// 将内容取出，找最大值
        long sum=0;// 将内容取出，进行和运算
        long squareSum = 0;// 将内容取出，进行平方和运算
        maxIndex=0;
        for (int i = 0; i < size; i++) {
            int v = Math.abs(audiodata[i]);
            sum+=v;
            squareSum+=v*v;
            if ( v > max) {
                max = v;// |audiodata|
                maxIndex=i;
            }
        }
        maxAudio= max;
        avgAudio = (int)(sum / size);
        double mean = squareSum / (double) size;
        dbAudio = 10 * Math.log10(mean);// 平方和除以数据总长度，得到音量大小。
    }

    static public int maxIndex=0;//最大值下标
    static public int maxAudio=0;//最大值
    static public int avgAudio=0;//绝对响度
    static public double dbAudio=0;//声音分贝
    static public int getQuietLevel = 0;

    /*
     * @shortArr  传入的值将被修改
     */
    public static boolean work(short[] shortArr){
        maxVolAndavgVol(shortArr,shortArr.length);
        emergeArr(shortArr,avgAudio);
        getQuietLevel = testAudio(shortArr);
        return getQuietLevel > QUIET_LEVEL;
    }


    //DEV.androidPrint("DB:"+dbAudio);
    //DEV.androidPrint("MAX:"+maxAudio);
    //DEV.androidPrint("AVG:"+avgAudio);


    //清理小型数据，便于统计声音状态
    public static void emergeArr(short[] shortArr,int avg){
        for(int i=0;i<shortArr.length;i++){
            shortArr[i] = (short) (Math.abs(shortArr[i])-avg);
            if(shortArr[i]<0){
                shortArr[i]=0;
            }
        }
    }

    public static int testAudio(short[] shortArr){

        int count=0;
        int maxZero=0;
        for(short s:shortArr){
            if(s == 0){
                count++;
            }else{
                //新局面，新计算
                if(maxZero<count){
                    maxZero=count;
                }
                count=0;
            }
        }
        DEV.androidPrint("有声音可能性:"+maxZero);
        return maxZero;
    }
}
