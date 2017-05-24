package cc.watchers.snoreview.audioservice.utils;

/**
 *定义项目中各种常量
 */

public class FINALV {

    public static final String AUDIO_RECORDER_FOLDER = "2017snore-test";    //默认录音文件的存储位置

    //public static final int SAMPLE_RATE = 4000; //HZ 录音采样频率
    public static final int SAMPLE_RATE = 44100; //HZ 录音采样频率

    public static final double FREQUENCY = 500; //Hz，标准频率（这里分析的是500Hz）
    public static final double RESOLUTION = 10; //Hz，误差



    public static final int FFT_N = 16384;        // 傅里叶计算最小长度???
    //傅里叶计算长度要求length=2^n
    public static final int[] FFT_NS = {0,2,4,16,32,64,128,256,512,1024,2048,4096,8192,16384};

    //计算各个频率的幅度，计算幅度比，能量
    //波的能量与振幅A的平方成正比（只有振幅可以表现其能量大小）

    //定义鼾声频率范围：


    public static boolean ROWWRITE = false; //应用分析数据后是否保持相关原始数据，比较占空间

    public static int LOGLIMIT = 42;//记录鼾声分贝级别

    //public static final int QUIET_LEVEL  = 100;//定义“平静”声音的等级 100 means: 25ms(声音大于均值)  ,4000Hz 1000ms
    public static final int QUIET_LEVEL  = 1000;//定义“平静”声音的等级 100 means: 25ms(声音大于均值)  ,4000Hz 1000ms



}
