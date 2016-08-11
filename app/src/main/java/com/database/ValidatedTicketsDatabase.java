package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.data.ValidatedTicket;

import java.util.ArrayList;

public class ValidatedTicketsDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyValidatedTickets.db";
    public static final String TICKETS_TABLE_NAME = "stations";
    public static final String TICKETS_COLUMN_STATION_ID = "station_id";
    public static final String TICKETS_COLUMN_PASSENGER_ID = "passenger_id";
    public static final String TICKETS_COLUMN_TICKET_ID = "ticketid";
    public static final String TICKETS_COLUMN_TTE_ID = "tte_id";
    public static final String TICKETS_COLUMN_VALIDATED_ON = "validated_on";
    public static final String TICKETS_COLUMN_VALIDATED_STATUS = "validated_status";

    public ValidatedTicketsDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table tickets " +
                        "(ticketid text, passenger_id text,station_id text," +
                        "tte_id text,validated_on text,validated_status text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tickets");
        onCreate(db);
    }

    public boolean insertTicket(String ticketid, String passenger_id, String station_id, String tte_id,
                                String validated_on, String validated_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ticketid", ticketid);
        contentValues.put("passenger_id", passenger_id);
        contentValues.put("station_id", station_id);
        contentValues.put("tte_id", tte_id);
        contentValues.put("validated_on", validated_on);
        contentValues.put("validated_status", validated_status);
        db.insert("tickets", null, contentValues);
        return true;
    }

    public ArrayList<ValidatedTicket> getAllData() {
        ArrayList<ValidatedTicket> validatedTickets = new ArrayList<ValidatedTicket>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery("select * from tickets", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            ValidatedTicket validatedTicket = new ValidatedTicket();
            validatedTicket.setTicketid(res.getString(res.getColumnIndex(TICKETS_COLUMN_TICKET_ID)));
            validatedTicket.setPassenger_id(res.getString(res.getColumnIndex(TICKETS_COLUMN_PASSENGER_ID)));
            validatedTicket.setTte_id(res.getString(res.getColumnIndex(TICKETS_COLUMN_TTE_ID)));
            validatedTicket.setStation_id(res.getString(res.getColumnIndex(TICKETS_COLUMN_STATION_ID)));
            validatedTicket.setValidated_on(res.getString(res.getColumnIndex(TICKETS_COLUMN_VALIDATED_ON)));
            validatedTicket.setValidated_status(res.getString(res.getColumnIndex(TICKETS_COLUMN_VALIDATED_STATUS)));
            validatedTickets.add(validatedTicket);
            res.moveToNext();
        }
        return validatedTickets;
    }

    public void deleteTicket(String ticketid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from tickets where ticketid = '" + ticketid + "'");
        db.close();
    }

    public void deleteAllTickets(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from tickets");
        db.close();
    }

    public void deleteTicketsList(ArrayList<ValidatedTicket> validatedTickets){
        for(ValidatedTicket validatedTicket : validatedTickets){
            deleteTicket(validatedTicket.getTicketid());
        }
    }
}
//station_id' => 2,
// 'tte_id' => 2,
// 'passenger_id' => 10,
// 'ticketid' => '1',
// 'validated_on' => '2015-10-10',
// 'validated_status' => 'Valid'