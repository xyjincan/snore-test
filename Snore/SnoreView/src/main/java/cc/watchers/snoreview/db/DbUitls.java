package cc.watchers.snoreview.db;

import android.database.sqlite.SQLiteDatabase;

import cc.watchers.snoreview.audioservice.utils.FileTools;


public class DbUitls {

    public static SQLiteDatabase localLogDB() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(FileTools.getDataFilePath() + "data.db", null);
        db.execSQL("create table if not exists snore(id integer primary key autoincrement,rawfile text,logfile text not null,create_time INTEGER,last_update INTEGER,status INTEGER not null)");
        return db;
    }

}

/*
        //d.execSQL("delete from stutb");
        //d.execSQL("insert into stutb(name,sex,age)values('张三','女',33)");


        /*
        //SQLiteDatabase db = DbUitls.localLogDB();

        Cursor cursor = db.rawQuery("select * from stutb", null);
        if(cursor != null){
            String []  columns = cursor.getColumnNames();
            while(cursor.moveToNext()){
                for(String column: columns){
                    Log.i(L.TAG,cursor.getString(cursor.getColumnIndex(column)));
                }
            }
            cursor.close();
        }
        db.close();


*/
