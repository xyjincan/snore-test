package cc.watchers.snoreview.audioservice.utils;

import cc.watchers.snoreview.audioservice.utils.fft.Complex;
import cc.watchers.snoreview.audioservice.utils.fft.FFT;

public class FFTDriver {

    public static final int FFT_N = 16384;        // 傅里叶计算最小长度??? 2^14
    public static final int SAMPLE_RATE = 4000; //HZ 录音采样频率

    public static final double MINLOGRANGE = 5.0;

    public static boolean FFTView(short[] data) {

        Complex[] x = new Complex[data.length];
        for (int i = 0; i < data.length; i++) {
            x[i] = new Complex(data[i], 0);
        }
        //FFT 快速傅里叶变换
        Complex[] y = FFT.fft(x);//主要是想知道这儿处理后，结果的含义是啥。。。
        outPrintFFT(y);//
        return true;
    }

    public static double ek = 0;

    public static void outPrintFFT(Complex[] cs) {

        DEV.androidPrint("FFT变换结果");
        DEV.androidPrint("直流分量："  + cs[0].getMod()/FFT_N);

        double e1=0,e2=0;
        int i = FFT_N*250/SAMPLE_RATE;
        int frequent = (i) * SAMPLE_RATE / FFT_N;
        while (frequent<400){
            double trange = cs[i].getMod()/(FFT_N/2);
            e1 = trange*trange;
            frequent = (i++) * SAMPLE_RATE / FFT_N;
        };

        i = FFT_N*600/SAMPLE_RATE;
        frequent = (i) * SAMPLE_RATE / FFT_N;
        while (frequent<750){
            double trange = cs[i].getMod()/(FFT_N/2);
            e2 = trange*trange;
            frequent = (i++) * SAMPLE_RATE / FFT_N;
        };
        ek=e1/e2;
        DEV.androidPrint("能量比Ek:"+ (e1/e2) );

/*        for(int i=1;i<FFT_N/2;i++){
            frequent = (i) * SAMPLE_RATE / FFT_N;
            double trange = cs[i].getMod()/(FFT_N/2);
            if(trange>MINLOGRANGE) {
                DEV.androidPrint("频率：" + (i) * SAMPLE_RATE / FFT_N + "幅度：" + trange);//相位
            }
        }*/


    }


    /*
    *
    * E1 250-400Hz
    *
    * E2 600-750Hz
    *
    * 0.55 (OSAHS较大)
    * Ek=E1/E2;(保留)
    *
    *
    *
    *
    *
    * */


}
