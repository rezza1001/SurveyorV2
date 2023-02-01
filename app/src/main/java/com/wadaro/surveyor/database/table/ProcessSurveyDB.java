package com.wadaro.surveyor.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wadaro.surveyor.database.DatabaseManager;
import com.wadaro.surveyor.database.MasterDB;

import java.util.Objects;

public class ProcessSurveyDB extends MasterDB {


    public static final String TAG          = "ProcessSurveyDB";
    public static final String TABLE_NAME   = "PROCESS_SURVEY";


    public static final String SALES_ID     = "sales_id";
    public static final String CONSUMEN_ID  = "consumen_id";
    public static final String ORDER_ID     = "order_id";
    public static final String DATA         = "data";

    public String salesID       = "";
    public String consumenID    = "";
    public String orderID       = "";
    public String data          = "";


    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + SALES_ID + " varchar(30) NULL," +
                " " + CONSUMEN_ID + " varchar(100) NULL," +
                " " + ORDER_ID + " varchar(100) NULL," +
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
    protected ProcessSurveyDB build(Cursor res) {
        ProcessSurveyDB visitDB = new ProcessSurveyDB();
        visitDB.salesID = res.getString(res.getColumnIndex(SALES_ID));
        visitDB.consumenID = res.getString(res.getColumnIndex(CONSUMEN_ID));
        visitDB.orderID = res.getString(res.getColumnIndex(ORDER_ID));
        visitDB.data = res.getString(res.getColumnIndex(DATA));
        return visitDB;
    }

    @Override
    protected void buildSingle(Cursor res) {
        salesID = res.getString(res.getColumnIndex(SALES_ID));
        consumenID = res.getString(res.getColumnIndex(CONSUMEN_ID));
        orderID = res.getString(res.getColumnIndex(ORDER_ID));
        data = res.getString(res.getColumnIndex(DATA));
    }

    public ContentValues createContentValues(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(SALES_ID, salesID);
        contentValues.put(CONSUMEN_ID, consumenID);
        contentValues.put(ORDER_ID, orderID);
        contentValues.put(DATA, data);
        return contentValues;
    }


    @Override
    public boolean insert(Context context) {
        delete(context, SALES_ID +"='"+ salesID +"' AND "+ ORDER_ID +"='"+ orderID +"' AND "+ CONSUMEN_ID+"='"+consumenID+"'");
        return super.insert(context);
    }


    public void getData(Context context, String id){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" WHERE "+ SALES_ID +"='"+id+"'", null);
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



}
