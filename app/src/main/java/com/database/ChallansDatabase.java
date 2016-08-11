package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.data.Challan;
import java.util.ArrayList;

public class ChallansDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyChallans.db";
    public static final String CHALLANS_TABLE_NAME = "challans";
    public static final String CHALLANS_COLUMN_STATION_ID = "station_id";
    public static final String CHALLANS_COLUMN_PASSENGER_ID = "passenger_id";
    public static final String CHALLANS_COLUMN_INVOICE_NO = "invoice_no";
    public static final String CHALLANS_COLUMN_TTE_ID = "tteid";
    public static final String CHALLANS_COLUMN_PASSENGER_NAME = "passenger_name";
    public static final String CHALLANS_COLUMN_PENALTY_TYPE = "penalty_type";
    public static final String CHALLANS_COLUMN_PAYMENT_DATE = "payment_date";
    public static final String CHALLANS_COLUMN_PAYMENT_MODE = "payment_mode";
    public static final String CHALLANS_COLUMN_CHALLAN_AMOUNT = "challan_amount";
    public static final String CHALLANS_COLUMN_DESCRIPTION = "description";
    public static final String CHALLANS_COLUMN_MOBILE = "mobile";

    public ChallansDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table challans " +
                        "(invoice_no text,passenger_id text,station_id text,passenger_name text," +
                        "tteid text,mobile text,penalty_type text,payment_date text," +
                        "payment_mode text,challan_amount text,description text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS challans");
        onCreate(db);
    }

    public boolean insertChallan(String invoice_no, String passenger_id, String tte_id, String station_id,
                                 String payment_date, String passenger_name, String phone,
                                 String penalty_type, String description, String challan_amount,
                                  String payment_mode){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("invoice_no", invoice_no);
        contentValues.put("passenger_id", passenger_id);
        contentValues.put("tteid", tte_id);
        contentValues.put("station_id", station_id);
        contentValues.put("payment_date", payment_date);
        contentValues.put("passenger_name", passenger_name);
        contentValues.put("mobile", phone);
        contentValues.put("penalty_type", penalty_type);
        contentValues.put("description", description);
        contentValues.put("challan_amount", challan_amount);
        contentValues.put("payment_mode", payment_mode);

        db.insert("challans", null, contentValues);
        return true;
    }

    public ArrayList<Challan> getAllData(){
        ArrayList<Challan> challans = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery("select * from challans", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {

            Challan challan = new Challan();
            challan.setStation_id(res.getString(res.getColumnIndex(CHALLANS_COLUMN_STATION_ID)));
            challan.setPassenger_id(res.getString(res.getColumnIndex(CHALLANS_COLUMN_PASSENGER_ID)));
            challan.setInvoice_no(res.getString(res.getColumnIndex(CHALLANS_COLUMN_INVOICE_NO)));
            challan.setTte_id(res.getString(res.getColumnIndex(CHALLANS_COLUMN_TTE_ID)));
            challan.setPassenger_name(res.getString(res.getColumnIndex(CHALLANS_COLUMN_PASSENGER_NAME)));
            challan.setPhone(res.getString(res.getColumnIndex(CHALLANS_COLUMN_MOBILE)));
            challan.setPenalty_type(res.getString(res.getColumnIndex(CHALLANS_COLUMN_PENALTY_TYPE)));
            challan.setPayment_date(res.getString(res.getColumnIndex(CHALLANS_COLUMN_PAYMENT_DATE)));
            challan.setPayment_mode(res.getString(res.getColumnIndex(CHALLANS_COLUMN_PAYMENT_MODE)));
            challan.setChallan_amount(res.getString(res.getColumnIndex(CHALLANS_COLUMN_CHALLAN_AMOUNT)));
            challan.setDescription(res.getString(res.getColumnIndex(CHALLANS_COLUMN_DESCRIPTION)));

            challans.add(challan);
            res.moveToNext();
        }
        return challans;
    }


    public void deleteChallan(String invoice_no) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from challans where invoice_no = '" + invoice_no + "'");
        db.close();
    }

    public void deleteAllChallans(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from challans");
        db.close();
    }

    public void deleteTicketsList(ArrayList<Challan> challans){
        for(Challan challan : challans){
            deleteChallan(challan.getInvoice_no());
        }
    }
}
