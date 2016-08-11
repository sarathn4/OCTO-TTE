package com.octo_tte;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.ActiveTicket;
import com.data.VolleyRequestData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchTickets extends AppCompatActivity {

    EditText editText_email;
    Button button_search;

    public static final String TAG = "SearchTickets";
    ArrayList<ActiveTicket> activeTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tickets);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activeTickets = new ArrayList<>();
        editText_email = (EditText) findViewById(R.id.search_editText_email);
        button_search = (Button) findViewById(R.id.search_button_search);

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText_email.getText().toString().trim().isEmpty() ||
                        editText_email.getText().toString().trim().equals("")) {
                    notifyUser("Please enter Email ID");
                } else if (!(Patterns.EMAIL_ADDRESS.matcher(editText_email.getText().toString().trim()).matches())) {
                    notifyUser("Please enter Valid Email ID");
                } else {
                    searchTickets();
                }
            }

        });
    }

    public void searchTickets() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);

                try {
                    parseData(response);
                    if (activeTickets.isEmpty()) {
                        notifyUser("No Tickets Found");
                    } else {
                        Intent intent = new Intent(SearchTickets.this, ListOfTickets.class);
                        intent.putParcelableArrayListExtra("activeTickets", activeTickets);
                        startActivity(intent);
                    }
                    Log.e(TAG, "got");
                    pDialog.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.hide();
                    notifyUser("Something went wrong. Please try again.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
                pDialog.hide();
                notifyUser("Something went wrong. Please try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "searchPassengerActivateTickets");
                params.put("passengerinfo", editText_email.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    public void parseData(String response) throws JSONException {
        activeTickets.clear();
        JSONObject jsonObject = new JSONObject(response);
        JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
        if (msgcontent.has("responseInfo")) {
            JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
            for (int i = 0; i < responseInfo.length(); i++) {
                JSONObject activatedTicket = responseInfo.getJSONObject(i);
                String ticket_id = activatedTicket.getString("id");
                String ticket_no = activatedTicket.getString("ticket_number");
                String ticket_code = activatedTicket.getString("ticket_code");
                String from_station = activatedTicket.getString("from_station");
                String to_station = activatedTicket.getString("to_station");
                String ticket_type = activatedTicket.getString("ticket_type");
                String no_of_tickets = activatedTicket.getString("no_of_tickets");
                String ticket_category = activatedTicket.getString("ticket_category");
                String ticket_period = activatedTicket.getString("ticket_period");
                String ticket_amount = activatedTicket.getString("ticket_amount");
                String purchased_date = activatedTicket.getString("purchased_on");
                String activated_date = activatedTicket.getString("activated_on");
                String activated_station = activatedTicket.getString("activated_station_code");
                String valid_date = activatedTicket.getString("valid_till");
                String proof_document = activatedTicket.getString("proof_document");
                String photo = activatedTicket.getString("upload_photo");
                String validated_count = activatedTicket.getString("validated_count");
                String imei_device = activatedTicket.getString("imei_device");

                ActiveTicket activeTicket = new ActiveTicket();
                activeTicket.setTicket_type(ticket_type);
                activeTicket.setTicket_period(ticket_period);
                activeTicket.setPurchased_date(purchased_date);
                activeTicket.setActivated_date(activated_date);
                activeTicket.setActivated_station(activated_station);
                activeTicket.setFrom_station(from_station);
                activeTicket.setNo_of_tickets(to_station);
                activeTicket.setTicket_amount(ticket_amount);
                activeTicket.setTicket_category(ticket_category);
                activeTicket.setTo_station(to_station);
                activeTicket.setValid_date(valid_date);
                activeTicket.setTicket_id(ticket_id);
                activeTicket.setTicket_no(ticket_no);
                activeTicket.setTicket_code(ticket_code);
                activeTicket.setProof_document(proof_document);
                activeTicket.setPhoto(photo);
                activeTicket.setValidated_count(validated_count);
                activeTicket.setImei_device(imei_device);
                activeTickets.add(activeTicket);
            }
        }
    }

}