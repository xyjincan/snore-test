package cc.watchers.snoreview.audioservice;

import android.util.Log;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.watchers.snoreview.audioservice.utils.DEV;


public class ShortConnect {

    public static SnoreAudio snoreAudio;

    static public void initShortConnect(String baseFileName) {
        snoreAudio = new SnoreAudio(baseFileName);
    }

    private static ExecutorService singleThreadExecutor = null;
    static {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    static final int BUF_SIZE = 5000000;
    public static final int FFT_N = 16384;        // 傅里叶计算最小长度???
    static short[] recordset = new short[BUF_SIZE];//当作循环缓存
    static int tail=0;
    static int front=0;//前面不断加数据

    public static void onDeal(short[] newdata, int size) {
        ShortHandler sh = new ShortHandler(newdata,size);
        singleThreadExecutor.execute(sh);
    }


    public static void onWork(short newCp[]) {
        snoreAudio.onDeal(newCp,newCp.length);//录音数据处理
    }

    static class ShortHandler implements Runnable {

        private short[] audiodata;
        private int size;
        ShortHandler(short[] audiodata, int size){
            this.audiodata=audiodata;
            this.size=size;
        }

        @Override
        public void run() {
            //System.arraycopy(来源, 0, 去处, 0, size);
            System.arraycopy(audiodata, 0, recordset, front, size);
            front+=size;//前面不断增加数据
            while(front-tail >= FFT_N){
                Timestamp beginTime = new Timestamp(System.currentTimeMillis());
                //Log.i(DEV.TAG,"my new data1:"+ " begin"+tail +" finish" + front + " t:"+beginTime);
                short newCp[] = new short[FFT_N];
                System.arraycopy(recordset, tail, newCp,0,FFT_N);
                onWork(newCp);
                tail+=FFT_N;//后面不断取数据
                //Log.i(DEV.TAG,"my new data go2:"+ " begin"+tail +" finish" + front + " t:"+beginTime);
                if(BUF_SIZE-tail < FFT_N*2 ){
                    beginTime = new Timestamp(System.currentTimeMillis());
                    Log.i(DEV.TAG,beginTime +"缓存循环使用回头");
                    //回头前移剩余数据(tail至front)
                    int remain = front-tail;
                    System.arraycopy(recordset, tail, recordset,0,remain);
                    tail=0;front=remain;
                }
            }

        }
    }

}
