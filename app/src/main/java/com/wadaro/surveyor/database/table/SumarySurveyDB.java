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

public class SumarySurveyDB extends MasterDB {


    public static final String TAG          = "SumarySurveyDB";
    public static final String TABLE_NAME   = "SUMARY_SURVEY";


    public static final String ID           = "id";
    public static final String DATA         = "data";

    public String id      = "";
    public String data  = "";


    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID    + " varchar(30) NULL," +
                " " + DATA + " TEXT NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected SumarySurveyDB build(Cursor res) {
        SumarySurveyDB visitDB = new SumarySurveyDB();
        visitDB.id = res.getString(res.getColumnIndex(ID));
        visitDB.data = res.getString(res.getColumnIndex(DATA));
        return visitDB;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getString(res.getColumnIndex(ID));
        this.data = res.getString(res.getColumnIndex(DATA));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(DATA, data);
        return contentValues;
    }


    @Override
    public boolean insert(Context context) {
        delete(context, ID+"='"+id+"'");
        return super.insert(context);
    }

    public ArrayList<SumarySurveyDB> getAllData(Context context){
        ArrayList<SumarySurveyDB> data = new ArrayList<>();

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

    public void getData(Context context, String id){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" WHERE "+ ID+"='"+id+"'", null);
        try {
            while (res.moveToNext()){
                buildSingle(res);
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
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
        return max+1;
    }

}
