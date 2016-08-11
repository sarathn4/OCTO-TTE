package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.data.SecretCode;

import java.util.ArrayList;

public class SecretCodeDatabase extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "SecretCodeDBName.db";
    public static final String SECRETCODE_TABLE_NAME = "secretcode";
    public static final String SECRETCODE_COLUMN_HOUR = "codeofhour";
    public static final String SECRETCODE_COLUMN_COLOR_CODE = "colorcode";
    public static final String SECRETCODE_COLUMN_SECRET_CODE = "secretcode";

    public SecretCodeDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table secretcode " +
                        "(codeofhour text primary key, colorcode text, secretcode text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS secretcode");
        onCreate(db);
    }

    public boolean insertSecretCode(String codeofhour, String colorcode, String secretcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("codeofhour", codeofhour);
        contentValues.put("colorcode", colorcode);
        contentValues.put("secretcode", secretcode);

        db.insert("secretcode", null, contentValues);
        return true;
    }

    public ArrayList<SecretCode> getData(){
        ArrayList<SecretCode> secretCodesList = new ArrayList<SecretCode>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from secretcode", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            SecretCode secretCode = new SecretCode();
            secretCode.setCodeofhour(res.getString(res.getColumnIndex(SECRETCODE_COLUMN_HOUR)));
            secretCode.setColorcode(res.getString(res.getColumnIndex(SECRETCODE_COLUMN_COLOR_CODE)));
            secretCode.setSecretcode(res.getString(res.getColumnIndex(SECRETCODE_COLUMN_SECRET_CODE)));
            secretCodesList.add(secretCode);
            res.moveToNext();
        }
        return secretCodesList;
    }

    public ArrayList<Float> getHours(){
        ArrayList<Float> hours = new ArrayList<Float>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from secretcode", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            hours.add(Float.parseFloat(res.getString(res.getColumnIndex(SECRETCODE_COLUMN_HOUR))));
            res.moveToNext();
        }
        return hours;
    }
    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from secretcode");
        db.close();
    }

    public void deleteSecretCode(String hour){
//        int hour_of_hour,mins_of_hour;
//        hour_of_hour = Integer.parseInt(hour.subSequence(0,1).toString());
//        mins_of_hour = Integer.parseInt(hour.subSequence(4,5).toString());
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from secretcode where codeofhour = '" + hour + "'");
        db.close();
    }

    public boolean hasSecretCode(String hour){
        boolean found = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from secretcode", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String current_hour = res.getString(res.getColumnIndex(SECRETCODE_COLUMN_HOUR));
            if(current_hour.equals(hour))
                found = true;
            res.moveToNext();
        }
        return found;
    }
}
//{"message":"Success",
// "msgcontent":{
// "requestParam":{
// "actiontype":"getSecretData","user_id":"10","forTime":"13.50"},
// "responseInfo":{"codeofhour":"13.30","colorcode":"#BCF0C0","secretcode":"u7Js98"}}}
