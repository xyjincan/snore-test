package demo.watchers.cc.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.sql.Timestamp;

import demo.watchers.cc.myapplication.audioservice.SnoreAudio;
import demo.watchers.cc.myapplication.audioservice.utils.FileTools;
import demo.watchers.cc.myapplication.audioservice.utils.L;
import demo.watchers.cc.myapplication.db.SnoreLog;

public class AudioRecordThread implements Runnable {

    private static final int SAMPLE_RATE = 4000;//Hz，采样频率,
    //音质越高。给出的实例是44100、22050、11025但不限于这几个参数。例如要采集低质量的音频就可以使用4000、8000等低采样率

    private File mSampleFile;
    private int bufferSize = 0;
    private AudioRecord audioRecord;
    private boolean mIsRecording = true;

    public AudioRecordThread() {
        Log.i("AudioRecordThread init", "true");
    }

    public void stop() {
        mIsRecording = false;
    }

    @Override
    public void run() {

        //存储准备
        String baseFileName = FileTools.getBaseName();//基本文件名，无后缀名
        SnoreAudio snoreAudio = new SnoreAudio(baseFileName);

        //获取本机缓存大小
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        short[] audiodata = new short[bufferSize / 2];
        int readSize = 0;
        Log.i(L.TAG + bufferSize, "true");
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                                        AudioFormat.CHANNEL_IN_MONO,//为了方便，这里只录制单声道
                                        AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        audioRecord.startRecording();
        Timestamp beginTime = new Timestamp(System.currentTimeMillis());
        Timestamp finishTime;

        SnoreLog.createFileLog(snoreAudio.getRawFileName(),snoreAudio.getFormatFileName(),beginTime);//初始日志

        while ((mIsRecording) && (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)) {
            readSize = audioRecord.read(audiodata, 0, audiodata.length);
            if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {//如果录音读取没有失败
                finishTime = new Timestamp(System.currentTimeMillis());
                short[] shortArr = new short[readSize];
                System.arraycopy(audiodata,0,shortArr,0,readSize);
                snoreAudio.onDeal(shortArr, beginTime, finishTime);//录音数据处理
                beginTime = finishTime;
            }
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }
}
