package cc.watchers.snoreview.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cc.watchers.snoreview.audioservice.utils.FileTools;
import cc.watchers.snoreview.db.model.SnoreHistory;

/**
 * Created by jc on 2017/5/11.
 */

public class SnoreLog {
    private static SQLiteDatabase db;
    static {
        db = DbUitls.localLogDB();
    }

    /**
     * （未实现）查询记录个数
     */
    public static int countFileLog(){
        Cursor cursor = db.rawQuery("select count(*)" +
                " from snore where status=1", null);
        return 0;
    }


    public static void createFileLog(String rawfilepath,String logfilepath, Timestamp createTime){
        db.execSQL("insert into snore(rawfile,logfile,create_time,status) values(?,?,?,1)", new Object[]{rawfilepath, logfilepath,createTime});
    }

    public static void updateLogFile(String logfilepath, Timestamp updateTime){
        db.execSQL("update snore set last_update=? where logfile = ?", new Object[]{updateTime, logfilepath});
    }

    public static void updateRowFile(String rawfile, Timestamp updateTime){
        db.execSQL("update snore set last_update=? where rawfile = ?", new Object[]{updateTime, rawfile});
    }

    public static void deleteFileLog(SnoreHistory snoreHistory){
        //update snore set status=-1 where logfile = ?
        FileTools.deteleFile(snoreHistory.getLogFile());
        FileTools.deteleFile(snoreHistory.getRowFile());
        db.execSQL("delete from snore where id = ?", new Object[]{snoreHistory.getId()});
    }

    public static List<SnoreHistory> getHistory(){

        List<SnoreHistory> list = new ArrayList<SnoreHistory>();

        Cursor cursor = db.rawQuery("select id,rawfile,logfile,create_time,last_update" +
                " from snore where status=1 order by id desc", null);
        if(cursor == null){
            return list;
        }
        while (cursor.moveToNext()) {
            SnoreHistory obj = new SnoreHistory();
            obj.setId(cursor.getString(cursor
                    .getColumnIndex("id")));
            obj.setRowFile(cursor.getString(cursor
                    .getColumnIndex("rawfile")));
            obj.setLogFile(cursor.getString(cursor
                    .getColumnIndex("logfile")));
            obj.setCreateTime(cursor.getString(cursor
                    .getColumnIndex("create_time")));
            obj.setLastUpdate(cursor.getString(cursor
                    .getColumnIndex("last_update")));
            list.add(obj);
        }
        return list;
    }


    public static List<SnoreHistory> getSnoreHistoryById(String id){

        List<SnoreHistory> list = new ArrayList<SnoreHistory>();
        Cursor cursor = db.rawQuery("select id,rawfile,logfile,create_time,last_update" +
                " from snore where status=1 and id=? order by id desc",new String[]{id});
        if(cursor == null){
            return list;
        }
        while (cursor.moveToNext()) {
            SnoreHistory obj = new SnoreHistory();
            obj.setId(cursor.getString(cursor
                    .getColumnIndex("id")));
            obj.setRowFile(cursor.getString(cursor
                    .getColumnIndex("rawfile")));
            obj.setLogFile(cursor.getString(cursor
                    .getColumnIndex("logfile")));
            obj.setCreateTime(cursor.getString(cursor
                    .getColumnIndex("create_time")));
            obj.setLastUpdate(cursor.getString(cursor
                    .getColumnIndex("last_update")));
            list.add(obj);
        }
        return list;
    }

}
