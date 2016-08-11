package com.octo_tte;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class TicketView extends Activity {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_view);


    }

}
