package com.wadaro.surveyor.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wadaro.surveyor.database.DatabaseManager;
import com.wadaro.surveyor.database.MasterDB;

import java.util.ArrayList;
import java.util.Objects;

public class DtlSurveyDB extends MasterDB {


    public static final String TAG          = "TempDB";
    public static final String TABLE_NAME   = "DETAIL_SURVEY";


    public static final String ID           = "id";
    public static final String DATA         = "data";
    public static final String IMAGES       = "images";

    public String id        = "";
    public String data      = "";
    public String images    = "";


    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID    + " varchar(30) NULL," +
                " " + DATA + " TEXT NULL," +
                " " + IMAGES + " TEXT NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected DtlSurveyDB build(Cursor res) {
        DtlSurveyDB visitDB = new DtlSurveyDB();
        visitDB.id      = res.getString(res.getColumnIndex(ID));
        visitDB.data    = res.getString(res.getColumnIndex(DATA));
        visitDB.images  = res.getString(res.getColumnIndex(IMAGES));
        return visitDB;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id     = res.getString(res.getColumnIndex(ID));
        this.data   = res.getString(res.getColumnIndex(DATA));
        this.images = res.getString(res.getColumnIndex(IMAGES));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(DATA, data);
        contentValues.put(IMAGES, images);
        return contentValues;
    }


    @Override
    public boolean insert(Context context) {
        delete(context, ID+"='"+id+"'");
        return super.insert(context);
    }

    public ArrayList<DtlSurveyDB> getAllData(Context context){
        ArrayList<DtlSurveyDB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME , null);
        try {
            while (res.moveToNext()){
                data.add(build(res));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        return  data;
    }

    public int getNextID(Context context){
        int max = 0;
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select MAX("+ID+") as IDMAX  from " + TABLE_NAME , null);
        try {
            while (res.moveToNext()){
                max    = res.getInt(res.getColumnIndex("IDMAX"));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        return max;
    }

}
