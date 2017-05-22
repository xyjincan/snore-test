package cc.watchers.snoreview.audioservice.utils;


import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FileTools {

	private static final String AUDIO_RECORDER_FOLDER = FINALV.AUDIO_RECORDER_FOLDER;    //默认录音文件的存储位置

	public static void deteleFile(String filename){
		File file = new File(getDataFilePath(),filename);
		if(file.exists()){
			file.delete();
		}
	}

	public static String getDataFilePath() {

		String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);
		if(!file.exists()){
			file.mkdir();
		}
		return file.getAbsolutePath()+"/";
	}

	public static String getBaseName(){
		//生成一个变化的基本文件名
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_");//设置日期格式
		String utctime = String.valueOf(System.currentTimeMillis());
		return df.format(new Date())+utctime.substring(utctime.length()-8,utctime.length()-3 );
	}

	/**
	 * 读取文件并按行输出
	 * 
	 * @param filePath
	 * @param spec
	 *            允许解析的最大行数， spec==null时，解析所有行
	 * @return
	 * @author
	 * @since 2016-3-1
	 */
	public static String[] read(final String filePath, final Integer spec) {
		File file = new File(filePath);
		// 当文件不存在或者不可读时
		if ((!isFileExists(file)) || (!file.canRead())) {
			System.out.println("file [" + filePath + "] is not exist or cannot read!!!");
			return new String[0];
		}

		List<String> lines = new LinkedList<String>();
		BufferedReader br = null;
		FileReader fb = null;
		try {
			fb = new FileReader(file);
			br = new BufferedReader(fb);

			String str = null;
			int index = 0;
			while (((spec == null) || index++ < spec) && (str = br.readLine()) != null) {
				lines.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeQuietly(br);
			closeQuietly(fb);
		}

		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * 写文件
	 * 
	 * @param filePath
	 *            输出文件路径
	 * @param contents
	 *            要写入的内容
	 * @param append
	 *            是否追加
	 * @return
	 * @author s00274007
	 * @since 2016-3-1
	 */
	public static int write(final String filePath, final String[] contents, final boolean append) {
		File file = new File(filePath);
		if (contents == null) {
			System.out.println("file [" + filePath + "] invalid!!!");
			return 0;
		}

		// 当文件存在但不可写时
		if (isFileExists(file) && (!file.canRead())) {
			return 0;
		}

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if (!isFileExists(file)) {
				file.createNewFile();
			}

			fw = new FileWriter(file, append);
			bw = new BufferedWriter(fw);
			for (String content : contents) {
				if (content == null) {
					continue;
				}
				bw.write(content);
				bw.newLine();
			}
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} finally {
			closeQuietly(bw);
			closeQuietly(fw);
		}

		return 1;
	}

	public static int write(final String filePath, final short[] contents,int readSize, final boolean append) {
		File file = new File(filePath);
		if (contents == null) {
			System.out.println("file [" + filePath + "] invalid!!!");
			return 0;
		}

		// 当文件存在但不可写时
		if (isFileExists(file) && (!file.canRead())) {
			return 0;
		}

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if (!isFileExists(file)) {
				//Log on DB new File

				file.createNewFile();
			}

			fw = new FileWriter(file, append);
			bw = new BufferedWriter(fw);

			for(int i=0;i<readSize;i++){
				bw.write(String.valueOf(contents[i])+",");
			}
            bw.newLine();
			bw.flush();
			//log on db update
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} finally {
			closeQuietly(bw);
			closeQuietly(fw);
		}

		return 1;
	}

    public static int write(final String filePath, final String content, final boolean append) {

        File file = new File(filePath);
        if (content == null || content.length()==0) {
            System.out.println("file [" + filePath + "] invalid!!!");
            return 0;
        }
        // 当文件存在但不可写时
        if (isFileExists(file) && (!file.canRead())) {
            return 0;
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if (!isFileExists(file)) {
                file.createNewFile();
            }
            fw = new FileWriter(file, append);
            bw = new BufferedWriter(fw);
            bw.write(content);
			bw.flush();
        } catch (IOException e) {
            return 0;
        } finally {
            closeQuietly(bw);
            closeQuietly(fw);
        }
        return 1;
    }
	
	private static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
		}
	}

	private static boolean isFileExists(final File file) {
		if (file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}

}
