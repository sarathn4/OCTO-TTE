package com.octo_tte;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adapter.ActiveTicketListAdapter;
import com.data.ActiveTicket;

import java.util.ArrayList;

public class ListOfTickets extends AppCompatActivity {

    ListView listView;
    ArrayList<ActiveTicket> activeTickets;

    ActiveTicketListAdapter activeTicketListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_tickets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listView_tickets_list);
        activeTickets = new ArrayList<>();
        activeTickets = getIntent().getParcelableArrayListExtra("activeTickets");

        activeTicketListAdapter = new ActiveTicketListAdapter(getApplicationContext(),activeTickets);
        listView.setAdapter(activeTicketListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListOfTickets.this,ActiveTicketView.class);
                intent.putExtra("activeTicket",activeTickets.get(i));
                startActivity(intent);
            }
        });
    }
}
