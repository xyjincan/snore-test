package cc.watchers.snoreview.audioservice;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.sql.Timestamp;

import cc.watchers.snoreview.audioservice.utils.DEV;
import cc.watchers.snoreview.audioservice.utils.FINALV;
import cc.watchers.snoreview.audioservice.utils.FileTools;
import cc.watchers.snoreview.audioservice.utils.ShortConnect;
import cc.watchers.snoreview.db.SnoreLog;

import static cc.watchers.snoreview.audioservice.utils.ShortConnect.snoreAudio;

public class AudioRecordThread implements Runnable {

    private static final int SAMPLE_RATE = FINALV.SAMPLE_RATE;//Hz，采样频率,
    //音质越高。给出的实例是44100、22050、11025但不限于这几个参数。例如要采集低质量的音频就可以使用4000、8000等低采样率

    private File mSampleFile;
    private int bufferSize = 0;
    private AudioRecord audioRecord;
    public static boolean mIsRecording = true;

    public AudioRecordThread() {
        mIsRecording=true;
        Log.i("AudioRecordThread init", "mIsRecording"+mIsRecording);
    }

    public void stop() {
        mIsRecording = false;
        Log.i("AudioRecordThread stop", "mIsRecording"+mIsRecording);
    }

    public static int getDeviceAudioMinBufferSize(){
        return AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }
    public static int getStandDeviceAudioMinBufferSize(){
        return AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

    @Override
    public void run() {

        //存储准备
        String baseFileName = FileTools.getBaseName();//基本文件名，无后缀名
        ShortConnect.initShortConnect(baseFileName);

        //获取本机缓存大小
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        short[] audiodata = new short[bufferSize];
        int readSize = 0;
        Log.i(DEV.TAG,"设备:"+ bufferSize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                                        AudioFormat.CHANNEL_IN_MONO,//为了方便，这里只录制单声道
                                        AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        audioRecord.startRecording();
        Timestamp beginTime = new Timestamp(System.currentTimeMillis());
        SnoreLog.createFileLog(snoreAudio.getRawFileName(),snoreAudio.getFormatFileName(),beginTime);//初始日志

        while ((mIsRecording) && (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)) {
            readSize = audioRecord.read(audiodata, 0, audiodata.length);//读取新数据，并覆盖直接旧值
            if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {//如果录音读取没有失败
                ShortConnect.onDeal(audiodata,readSize);
            }
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }
}
