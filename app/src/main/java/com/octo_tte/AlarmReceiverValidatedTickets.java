package com.octo_tte;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.Challan;
import com.data.PHPSerialization;
import com.data.ValidatedTicket;
import com.data.VolleyRequestData;
import com.database.ChallansDatabase;
import com.database.ValidatedTicketsDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AlarmReceiverValidatedTickets extends BroadcastReceiver {

    String TAG = "SyncData";
    ValidatedTicketsDatabase validatedTicketsDatabase;
    ArrayList<ValidatedTicket> validatedTickets;
    JSONArray postValidatedTicketsData = null;

    ChallansDatabase challansDatabase;
    ArrayList<Challan> challans;
    JSONArray postChallanData = null;

//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmCheck", "Running");
        validatedTicketsDatabase = new ValidatedTicketsDatabase(context);
        validatedTickets = validatedTicketsDatabase.getAllData();
        Log.e("ValidatedTicketslength", validatedTickets.size() + "");

        challansDatabase = new ChallansDatabase(context);
        challans = challansDatabase.getAllData();
        sharedPreferences = context.getSharedPreferences("tte_data", Context.MODE_PRIVATE);
        Log.e("ChallansLength", challans.size() + "");

        if (!validatedTickets.isEmpty()) {
            Toast.makeText(context, "Tickets:I'm running", Toast.LENGTH_SHORT).show();
            postValidatedTicketsData = new JSONArray();
            for (ValidatedTicket validatedTicket : validatedTickets)
                postValidatedTicketsData.put(validatedTicket.getJsonObject());
//            new SyncTickets().execute(postValidatedTicketsData.toString());
            syncTickets(postValidatedTicketsData.toString());
        }

        if (!challans.isEmpty()) {
            Toast.makeText(context, "Challans:I'm running", Toast.LENGTH_SHORT).show();
            postChallanData = new JSONArray();

            for (Challan challan : challans)
                postChallanData.put(challan.getJSONObject());

//            new SyncChallans().execute(postChallanData.toString());
            syncChallans(postChallanData.toString());
        }
    }

//    class SyncTickets extends AsyncTask<String,Void,Void>{
//        JSONObject jsonObject;
//        String message = "";
//
//        @Override
//        protected Void doInBackground(String... param) {
//            try {
//                String postValidatedTicketsData = param[0];
//                Log.e("postValidatedTickets",postValidatedTicketsData);
//                Log.e("Check", "Started");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "validatedTickets"));
//                String id = sharedPreferences.getString("user_id", "2");
//                params.add(new BasicNameValuePair("userid", id));
//                params.add(new BasicNameValuePair("postValidatedTicketsData",postValidatedTicketsData));
//
//                httpClient = new DefaultHttpClient();
//                httpPost = new HttpPost("http://goeticket.com/ticketingsystem/passengers.json");
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//
//                httpResponse = httpClient.execute(httpPost);
//                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//                Log.e("Response", result);
//
//                jsonObject = new JSONObject(result);
//                message = jsonObject.getString("message");
//                if(message.equals("Success"))
//                    validatedTicketsDatabase.deleteTicketsList(validatedTickets);
//                Log.e("Check", message);
//            } catch (Exception e) {
//                Log.e("Check", "Exception");
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }

    public void syncTickets(final String postValidatedTicketsData) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                Log.e("Tickets", "Gonna Delete");
                validatedTicketsDatabase.deleteAllTickets();
                Log.e("Tickets", "Deleted:" + validatedTicketsDatabase.getAllData().size());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
                    Log.e("Tickets", "Gonna Delete");
                    validatedTicketsDatabase.deleteAllTickets();
                    Log.e("Tickets", "Deleted:" + validatedTicketsDatabase.getAllData().size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return generateTicketsParams();
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

//    class SyncChallans extends AsyncTask<String,Void,Void> {
//        JSONObject jsonObject;
//        String message = "";
//
//        @Override
//        protected Void doInBackground(String... param) {
//            try {
//                String postChallanData = param[0];
//                Log.e("postChallanData", postChallanData);
//                Log.e("Check", "Started");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "updateOfflineChallan"));
//                String id = sharedPreferences.getString("user_id", "2");
//                params.add(new BasicNameValuePair("userid", id));
//                params.add(new BasicNameValuePair("postChallanData", postChallanData));
//
//                httpClient = new DefaultHttpClient();
//                httpPost = new HttpPost("http://goeticket.com/ticketingsystem/passengers.json");
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//
//                httpResponse = httpClient.execute(httpPost);
//                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//                Log.e("Response", result);
//
//                jsonObject = new JSONObject(result);
//                message = jsonObject.getString("message");
//                if (message.equals("Success"))
//                    challansDatabase.deleteTicketsList(challans);
//                Log.e("Check", message);
//            } catch (Exception e) {
//                Log.e("Check", "Exception");
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }

    public void syncChallans(final String postChallanData) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                Log.e("Challans", "Gonna Delete");
                challansDatabase.deleteAllChallans();
                Log.e("Challans", "Deleted:" + challansDatabase.getAllData().size());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
                    Log.e("Challans", "Gonna Delete");
                    challansDatabase.deleteAllChallans();
                    Log.e("Challans", "Deleted:" + challansDatabase.getAllData().size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("actiontype", "updateOfflineChallan");
//                String id = sharedPreferences.getString("user_id", "2");
//                params.put("userid", id);
//                params.put("postChallanData", postChallanData);
//                return params;
                return generateChallanParams();
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

    public Map<String, String> generateChallanParams() {
        Map<String, String> params = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        params.put("actiontype", "updateOfflineChallan");
        String id = sharedPreferences.getString("user_id", "2");
        params.put("userid", id);

        challans = challansDatabase.getAllData();

        Log.e("Challans", "Size:" + challans.size());
        ArrayList arrayList = new ArrayList();
        for (int count = 0; count < challans.size(); count++) {
            HashMap<String, String> challan = new HashMap<>();

            challan.put("invoice_no", challans.get(count).getInvoice_no());
            challan.put("tte_id", challans.get(count).getTte_id());
            challan.put("station_id", challans.get(count).getStation_id());
            challan.put("payment_date", challans.get(count).getPayment_date());
            challan.put("passenger_name", challans.get(count).getPassenger_name());
            challan.put("phone", challans.get(count).getPhone());
            challan.put("penalty_type", challans.get(count).getPenalty_type());
            challan.put("description", challans.get(count).getDescription());
            challan.put("challan_amount", challans.get(count).getChallan_amount());
            challan.put("payment_mode", challans.get(count).getPayment_mode());
            challan.put("passenger_id", challans.get(count).getPassenger_id());

            arrayList.add(challan);
        }

        String beaconInfo = PHPSerialization.serialize(arrayList);

        params.put("postChallanData", beaconInfo);
        Log.e("ChallanParams", "" + params);
        return params;
    }

    public Map<String, String> generateTicketsParams() {
        Map<String, String> params = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        params.put("actiontype", "validatedTickets");
        String id = sharedPreferences.getString("user_id", "2");
        params.put("userid", id);

        validatedTickets = validatedTicketsDatabase.getAllData();

        Log.e("Tickets", "Size:" + validatedTickets.size());
        ArrayList arrayList = new ArrayList();
        for (int count = 0; count < validatedTickets.size(); count++) {
            HashMap<String, String> ticket = new HashMap<>();

            String ticketid = validatedTickets.get(count).getTicketid();
            String passenger_id = validatedTickets.get(count).getPassenger_id();
            String station_id = validatedTickets.get(count).getStation_id();
            String tte_id = validatedTickets.get(count).getTte_id();
            String validated_on = validatedTickets.get(count).getValidated_on();
            String validated_status = validatedTickets.get(count).getValidated_status();

            ticket.put("ticketid", ticketid);
            ticket.put("passenger_id", passenger_id);
            ticket.put("station_id", station_id);
            ticket.put("tte_id", tte_id);
            ticket.put("validated_on", validated_on);
            ticket.put("validated_status", validated_status);

            arrayList.add(ticket);
        }

        String beaconInfo = PHPSerialization.serialize(arrayList);

        params.put("postValidatedTicketsData", beaconInfo);
        Log.e("ChallanParams", "" + params);
        return params;
    }
}