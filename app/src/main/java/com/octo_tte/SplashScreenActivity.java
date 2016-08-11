package com.octo_tte;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.VolleyRequestData;
import com.database.Mydatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends Activity {

    public static final String TAG = "LoadStations";
    Mydatabase mydatabase;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPreferences = getSharedPreferences("tte_data", MODE_PRIVATE);
        mydatabase = new Mydatabase(this);

//        new DataLoader().execute();

        loadStations();
    }

//    class DataLoader extends AsyncTask<Void, Void, Void> {
//
//        HttpClient httpClient;
//        HttpPost httpPost;
//        HttpResponse httpResponse;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... param) {
//            try {
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "getStations"));
//
//                httpClient = new DefaultHttpClient();
//                httpPost = new HttpPost("http://goeticket.com/ticketingsystem/passengers.json");
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//
//                httpResponse = httpClient.execute(httpPost);
//
//                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//                Log.e("Result", result);
//
//                JSONObject jsonObject = new JSONObject(result);
//                JSONObject msgContent = jsonObject.getJSONObject("msgcontent");
//                JSONArray responseInfo = msgContent.getJSONArray("responseInfo");
//
//                for (int i = 0; i < responseInfo.length(); i++) {
//                    JSONObject station_item = responseInfo.getJSONObject(i);
//                    JSONObject stations = station_item.getJSONObject("stations");
//                    String division_id = stations.getString("division_id");
//                    if (division_id.equals("1")) {
//                        String station_id = stations.getString("id");
//                        String station_name = stations.getString("station_name");
//                        String station_code = stations.getString("station_code");
//
//                        if (!(mydatabase.insertStation(station_id, station_name, station_code))) {
//                            mydatabase.deleteStation(station_name);
//                            mydatabase.insertStation(station_id, station_name, station_code);
//                        }
//
//                        Log.e("Station Name", station_name);
//                    }
//                }
//
//            } catch (Exception e) {
//                Log.e("Check", "Exception");
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Log.e("NameCheck", sharedPreferences.getString("user_name", "User"));
//            if (sharedPreferences.getString("user_name", "User").equals("User")) {
//                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
//                startActivity(intent);
//            } else {
//                Intent intent = new Intent(getApplicationContext(), Home.class);
//                startActivity(intent);
//            }
//        }
//    }

    public void loadStations() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    parseStationData(response);
                    Log.e("Check", message);
                } catch (Exception e) {
                    e.printStackTrace();
                    notifyUser("Something went wrong. Please try again.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
                notifyUser("Something went wrong. Please try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "getStations");
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

    public void parseStationData(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);
        JSONObject msgContent = jsonObject.getJSONObject("msgcontent");
        JSONArray responseInfo = msgContent.getJSONArray("responseInfo");

        for (int i = 0; i < responseInfo.length(); i++) {
            JSONObject station_item = responseInfo.getJSONObject(i);
            JSONObject stations = station_item.getJSONObject("stations");
            String division_id = stations.getString("division_id");
            if (division_id.equals("1")) {
                String station_id = stations.getString("id");
                String station_name = stations.getString("station_name");
                String station_code = stations.getString("station_code");

                if (!(mydatabase.insertStation(station_id, station_name, station_code))) {
                    mydatabase.deleteStation(station_name);
                    mydatabase.insertStation(station_id, station_name, station_code);
                }

                Log.e("Station Name", station_name);
            }
        }
        Log.e("NameCheck", sharedPreferences.getString("user_name", "User"));
        if (sharedPreferences.getString("user_name", "User").equals("User")) {
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
        }
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }
}
