package demo.watchers.cc.myapplication.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jc on 2017/5/11.
 */

public class SnoreLog {
    private static SQLiteDatabase db;
    static {
        db = DbUitls.localLogDB();
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

    public static void deleteFileLog(String logfilepath){
        //update snore set status=-1 where logfile = ?



        db.execSQL("delete from snore where logfile = ?", new Object[]{logfilepath});
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

}
