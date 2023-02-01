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

public class SurveyDeatailDB extends MasterDB {

    public static final String TAG          = "SurveyDeatailDB";
    public static final String TABLE_NAME   = "SurveyDeatailDB";

    public static final String ID       = "id";
    public static final String REFF_ID  = "reffId";
    public static final String DATA     = "data";
    public static final String IMAGES   = "images";

    public int id     = 0;
    public String data   = "";
    public String images   = "";
    public String reffId   = "";

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID    + " INTEGER DEFAULT 0," +
                " " + DATA    + " text NULL," +
                " " + IMAGES    + " text NULL," +
                " " + REFF_ID    + " varchar(50) NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected SurveyDeatailDB build(Cursor res) {
        SurveyDeatailDB boking = new SurveyDeatailDB();
        boking.id    = res.getInt(res.getColumnIndex(ID));
        boking.data    = res.getString(res.getColumnIndex(DATA));
        boking.images    = res.getString(res.getColumnIndex(IMAGES));
        boking.reffId    = res.getString(res.getColumnIndex(REFF_ID));
        return boking;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id    = res.getInt(res.getColumnIndex(ID));
        this.data    = res.getString(res.getColumnIndex(DATA));
        this.reffId    = res.getString(res.getColumnIndex(REFF_ID));
        this.images    = res.getString(res.getColumnIndex(IMAGES));
        Log.d(TAG,"reffId : "+reffId);
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(DATA, data);
        contentValues.put(IMAGES, images);
        contentValues.put(REFF_ID, reffId);
        return contentValues;
    }

    @Override
    public boolean insert(Context context) {
        return super.insert(context);
    }

    public ArrayList<SurveyDeatailDB> getData(Context context){
        ArrayList<SurveyDeatailDB> data = new ArrayList<>();

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
    public void getData(Context context, String reffId){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME +" WHERE "+REFF_ID+"='"+reffId+"'";
        Log.d(TAG,query);
        Cursor res = db.rawQuery(query, null);
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

    public ArrayList<SurveyDeatailDB> getAllBySalesId(Context context, String salesId){
        ArrayList<SurveyDeatailDB> allData = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME +" WHERE "+REFF_ID+" LIKE '%"+salesId+"%'";
        Log.d(TAG,query);
        Cursor res = db.rawQuery(query, null);
        try {
            while (res.moveToNext()){
                allData.add(build(res));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        return allData;
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
