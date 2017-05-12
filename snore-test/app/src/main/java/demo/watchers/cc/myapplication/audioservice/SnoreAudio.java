package demo.watchers.cc.myapplication.audioservice;

import android.util.Log;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import demo.watchers.cc.myapplication.audioservice.utils.FileTools;
import demo.watchers.cc.myapplication.audioservice.utils.L;
import demo.watchers.cc.myapplication.audioservice.utils.TimeTools;
import demo.watchers.cc.myapplication.db.SnoreLog;

public class SnoreAudio {

    private static int LOGLIMIT = 42;//记录鼾声分贝级别
    public static boolean ROWWRITE = true;

    private ExecutorService singleThreadExecutor = null;
    private String rawFileName;
    private String formatFileName;
    private String fileParentPath;

    public SnoreAudio(String baseFileName) {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        rawFileName = baseFileName + ".dat";//原始数据
        formatFileName = baseFileName + ".txt";//分析记录
        fileParentPath = FileTools.getDataFilePath();
    }

    public void onDeal(short[] shortArr, Timestamp beginTime, Timestamp finishTime) {
        SnoreHandler snoreHander = new SnoreHandler(shortArr, beginTime, finishTime);
        singleThreadExecutor.execute(snoreHander);
    }

    public double getEnvironmentDecibel(short[] data, int length) {
        long v = 0;
        // 将 buffer 内容取出，进行平方和运算
        for (int i = 0; i < length; i++) {
            v += data[i] * data[i];
        }
        // 平方和除以数据总长度，得到音量大小。
        double mean = v / (double) length;
        return 10 * Math.log10(mean);
    }

    private int maxIndex=0;
    private int maxVol(short[] audiodata, int size) {
        int max = 0;
        maxIndex=0;
        for (int i = 0; i < size; i++) {
            if (audiodata[i] > max) {
                max = audiodata[i];//audiodata
                maxIndex=i;
            }
        }
        return max;
    }

    private int avgVlo(short[] audiodata, int size) {

        long sum = 0;
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (audiodata[i] >= 0) {
                count++;
                sum += audiodata[i];
            }
        }
        return (int) sum / count;
    }

    class SnoreHandler implements Runnable {
        private short[] shortArr;
        private Timestamp beginTime, finishTime;

        public SnoreHandler(short[] shortArr, Timestamp beginTime, Timestamp finishTime) {
            this.shortArr = shortArr;
            this.beginTime = beginTime;
            this.finishTime = finishTime;
        }

        @Override
        public void run() {

            Log.i(L.TAG, "vol inf:" + "begin:" + beginTime + " finish:" + finishTime);

            double environmentDecibel = getEnvironmentDecibel(shortArr, shortArr.length);
            int maxVol = maxVol(shortArr, shortArr.length);
            int avgVol = avgVlo(shortArr, shortArr.length);

            Log.i(L.TAG, "EnvironmentDecibel:" + environmentDecibel);
            Log.i(L.TAG, "max vol:" + maxVol);
            Log.i(L.TAG, "avg vol:" + avgVol);

            if(environmentDecibel > LOGLIMIT) {//纪录疑是鼾声
                StringBuilder loginf = new StringBuilder();
                loginf.append((int)environmentDecibel);
                loginf.append(",");
                loginf.append(maxVol);
                loginf.append(",");
                loginf.append(avgVol);
                loginf.append(",");
                loginf.append(TimeTools.occerTime(beginTime,finishTime,maxIndex,shortArr.length));
                loginf.append("\n");
                SnoreLog.updateLogFile(formatFileName,finishTime);
                FileTools.write(fileParentPath+formatFileName, loginf.toString(), true);
            }

            if(ROWWRITE){
                SnoreLog.updateRowFile(rawFileName,finishTime);
                FileTools.write( fileParentPath+rawFileName, shortArr, shortArr.length, true);
            }

        }

    }


    public String getRawFileName() {
        return rawFileName;
    }

    public void setRawFileName(String rawFileName) {
        this.rawFileName = rawFileName;
    }

    public String getFormatFileName() {
        return formatFileName;
    }

    public void setFormatFileName(String formatFileName) {
        this.formatFileName = formatFileName;
    }
}
