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

public class GoodsDB extends MasterDB {


    public static final String TAG          = "GoodsDB";
    public static final String TABLE_NAME   = "GOODS";


    public static final String TMP_ID           = "tmpid";
    public static final String GOODS            = "goods";
    public static final String GOODS_NAME       = "goods_name";
    public static final String QTY              = "qty";
    public static final String PRICE              = "price";

    public String tmpId         = "";
    public String goods         = "";
    public String goodsName     = "";
    public int qty           = 0;
    public long price           = 0;

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + TMP_ID    + " varchar(30) NULL," +
                " " + GOODS    + " varchar(20) NULL," +
                " " + GOODS_NAME    + " varchar(200) NULL," +
                " " + PRICE    + " varchar(10) NULL," +
                " " + QTY    + " varchar(10) NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected GoodsDB build(Cursor res) {
        GoodsDB visitDB = new GoodsDB();
        visitDB.tmpId = res.getString(res.getColumnIndex(TMP_ID));
        visitDB.goods = res.getString(res.getColumnIndex(GOODS));
        visitDB.goodsName = res.getString(res.getColumnIndex(GOODS_NAME));
        visitDB.qty = res.getInt(res.getColumnIndex(QTY));
        visitDB.price = res.getLong(res.getColumnIndex(PRICE));
        return visitDB;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.tmpId = res.getString(res.getColumnIndex(TMP_ID));
        this.goods = res.getString(res.getColumnIndex(GOODS));
        this.goodsName = res.getString(res.getColumnIndex(GOODS_NAME));
        this.qty = res.getInt(res.getColumnIndex(QTY));
        this.price = res.getLong(res.getColumnIndex(PRICE));
    }

    public ContentValues createContentValues(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TMP_ID, tmpId);
        contentValues.put(GOODS, goods);
        contentValues.put(GOODS_NAME, goodsName);
        contentValues.put(QTY, qty);
        contentValues.put(PRICE, price);
        return contentValues;
    }


    @Override
    public boolean insert(Context context) {
        delete(context, TMP_ID+"='"+tmpId+"'");
        return super.insert(context);
    }

    public void getData(Context context, String tmpID){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" WHERE "+ TMP_ID+"='"+tmpID+"'", null);
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

    public ArrayList<GoodsDB> getData(Context context){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +"  ORDER BY "+ TMP_ID , null);
        ArrayList<GoodsDB> all_notif = new ArrayList<>();
        try {
            while (res.moveToNext()){
                all_notif.add(build(res));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        return all_notif;
    }

}
