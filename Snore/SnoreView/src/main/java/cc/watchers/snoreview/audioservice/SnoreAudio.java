package cc.watchers.snoreview.audioservice;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.watchers.snoreview.audioservice.utils.FFTDriver;
import cc.watchers.snoreview.audioservice.utils.FINALV;
import cc.watchers.snoreview.audioservice.utils.FileTools;
import cc.watchers.snoreview.audioservice.utils.SoundCheck;
import cc.watchers.snoreview.db.SnoreLog;


public class SnoreAudio {

    private static int LOGLIMIT = FINALV.LOGLIMIT;//记录鼾声分贝级别
    public static boolean ROWWRITE = FINALV.ROWWRITE;

    private ExecutorService singleThreadExecutor = null;
    //private ExecutorService fftsingleThreadExecutor = null;//单独为傅里叶变换开线程计算（nlogn时间复杂度）

    static public String rawFileName;
    static public String formatFileName;
    static public String fileParentPath;
    //private static final int SAMPLE_ATE = 44100;//Hz，采样频率
    //private static final double FREQUENCY = V.FREQUENCY; //Hz，标准频率（这里分析的是500Hz）
    //private static final double RESOLUTION = V.RESOLUTION; //Hz，误差

    public SnoreAudio(String baseFileName) {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        rawFileName = baseFileName + ".dat";//原始数据
        formatFileName = baseFileName + ".txt";//分析记录
        fileParentPath = FileTools.getDataFilePath();
    }

    public void onDeal(short[] newdata, int size) {
        //Log.i(DEV.TAG, "onDeal" + "newdata:" + newdata.length);
        if (AudioRecordThread.mIsRecording == false) {
            return;//当用户点击取消后，不再追加写入数据（录音延迟五秒左右）
        }
        SnoreHandler snoreHander = new SnoreHandler(newdata, size);
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

    private int maxIndex = 0;

    private int maxVol(short[] audiodata, int size) {
        int max = 0;
        maxIndex = 0;
        for (int i = 0; i < size; i++) {
            if (audiodata[i] > max) {
                max = audiodata[i];//audiodata
                maxIndex = i;
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

        private short[] audiodata;
        private int size;


        public SnoreHandler(short[] audiodata, int size) {
            this.audiodata = audiodata;
            this.size = size;
        }

        @Override
        public void run() {

/*            long a,b;
            a=beginTime.getTime();
            b=finishTime.getTime();*/
            //Log.i(DEV.TAG, "SnoreAudio inf:" + "begin:" + beginTime + " finish:" + finishTime + "datasize:" + size +"timelengths:"+(b-a)+"ms");
            short[] tempdatas = new short[size];
            System.arraycopy(audiodata, 0, tempdatas, 0, size);
            short[] tempdataf = new short[size];
            System.arraycopy(audiodata, 0, tempdataf, 0, size);
            if (ROWWRITE) {
                SnoreLog.updateRowFile(rawFileName, new Timestamp(System.currentTimeMillis()));
                FileTools.write(fileParentPath + rawFileName, tempdataf, tempdataf.length, true);
            }
            //LOG:dbAudio,maxAudio,avgAudio,EK,amongTime(beginTime-finishTime)
            if (SoundCheck.work(tempdatas)) {
                FFTDriver.FFTView(tempdataf);
                StringBuilder loginf = new StringBuilder();
                loginf.append((int) SoundCheck.dbAudio);
                loginf.append(",");
                loginf.append(SoundCheck.maxAudio);
                loginf.append(",");
                loginf.append(SoundCheck.avgAudio);
                loginf.append(",");
                loginf.append(FFTDriver.ek);
                loginf.append(",");
                loginf.append( new Timestamp(System.currentTimeMillis()) );
                loginf.append("\n");
                SnoreLog.updateLogFile(formatFileName, new Timestamp(System.currentTimeMillis()));
                FileTools.write(fileParentPath + formatFileName, loginf.toString(), true);
            }



/*
            //FFT分析得到频率
            double frequence = FFT.GetFrequency(tempdata);
            Log.i(DEV.TAG+"FFT","fre:"+frequence);
            //RESOLUTION = 10; //Hz，误差
            if(Math.abs(frequence - FREQUENCY)<RESOLUTION){
                //测试通过
                Log.i(DEV.TAG+"FFT pass","fre:"+Math.abs(frequence - FREQUENCY));
            }else{
                //测试失败
                Log.i(DEV.TAG+"FFT fail","fre:"+Math.abs(frequence - FREQUENCY));
            }
            */


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
