package com.octo_tte;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.VolleyRequestData;
import com.database.SecretCodeDatabase;
import com.jwetherell.quick_response_code.CaptureActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    public String TAG = "GetSecretData";
    ImageView validate, chalan;
//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;
    SecretCodeDatabase secretCodeDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences_onlinemode;
    Button logout, change_password;
    Switch switch_mode;
    boolean mode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        secretCodeDatabase = new SecretCodeDatabase(this);
        sharedPreferences_onlinemode = getSharedPreferences("online", MODE_PRIVATE);
        validate = (ImageView) findViewById(R.id.imageView_validate);
        chalan = (ImageView) findViewById(R.id.imageView_generate);
        logout = (Button) findViewById(R.id.button_logout);
        switch_mode = (Switch) findViewById(R.id.switch_mode);
        change_password = (Button) findViewById(R.id.button_change_password);

        mode = sharedPreferences_onlinemode.getBoolean("mode", false);
        switch_mode.setChecked(mode);
        if (mode)
            switch_mode.setText("Online mode");
        else
            switch_mode.setText("Offline mode");

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(intent);
            }
        });
        switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("CheckMode", isChecked + "");
                if (isChecked)
                    switch_mode.setText("Online mode");
                else
                    switch_mode.setText("Offline mode");
                SharedPreferences.Editor editor = sharedPreferences_onlinemode.edit();
                editor.putBoolean("mode", isChecked);
                editor.commit();
                Log.e("AgainCheck", sharedPreferences_onlinemode.getBoolean("mode", false) + "");
            }
        });
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Float> hours = secretCodeDatabase.getHours();
                float hour_one = hours.get(0);
                float hour_two;
                if (hours.size() > 1) {
                    hour_two = hours.get(1);
                } else {
                    hour_two = hours.get(0);
                }
                Calendar calendar = Calendar.getInstance();
                float time = Float.parseFloat(calendar.getTime().getHours() + "." + calendar.getTime().getMinutes());

                if ((time >= hour_one) && (time <= (hour_two + 0.30)) || (time >= hour_two) && (time <= (hour_one + 0.30))) {
                    Intent intent = new Intent(Home.this, CaptureActivity.class);
                    startActivity(intent);
                } else {
                    String current_time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
//                    new GetSecretCode().execute(current_time);
                    getSecretCode(current_time);
                    calendar.add(Calendar.MINUTE, -31);
                    current_time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
//                    new GetSecretCode().execute(current_time);
                    getSecretCode(current_time);
                    Intent intent = new Intent(Home.this, CaptureActivity.class);
                    startActivity(intent);
                }
            }
        });

        chalan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, GenerateChallan.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("tte_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                SharedPreferences.Editor editor1 = sharedPreferences_onlinemode.edit();
                editor1.clear();
                editor1.commit();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        showExitAlert();
        //super.onBackPressed();
    }

    public void showExitAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Are you sure to exit?");

        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

//    class GetSecretCode extends AsyncTask<String, Void, Void> {
//
//        JSONObject jsonObject;
//        String message = "";
//        String color_code = "";
//        String secret_code = "";
//        String color_hour = "";
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(String... param) {
//            try {
//                String time = param[0];
////                Calendar calendar = Calendar.getInstance();
////                String time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "getSecretData"));
//                params.add(new BasicNameValuePair("user_id", "3"));
//                params.add(new BasicNameValuePair("forTime", time));
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
//            try {
//                JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                JSONObject responseInfo = msgcontent.getJSONObject("responseInfo");
//                color_code = responseInfo.getString("colorcode");
//                secret_code = responseInfo.getString("secretcode");
//                color_hour = responseInfo.getString("codeofhour");
//
//                if (!(secretCodeDatabase.insertSecretCode(color_hour, color_code, secret_code))) {
//                    secretCodeDatabase.deleteSecretCode(color_hour);
//                    secretCodeDatabase.insertSecretCode(color_hour, color_code, secret_code);
//                }
//                ;
//                //popupWindow.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(Home.this, SearchTickets.class));
//                startCatgeory();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getSecretCode(final String time) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
                    JSONObject responseInfo = msgcontent.getJSONObject("responseInfo");

                    JSONObject secretCodes = responseInfo.getJSONObject("secretcodes");
                    String color_code = secretCodes.getString("colorcode");
                    String secret_code = secretCodes.getString("secretcode");
                    String color_hour = secretCodes.getString("codeofhour");

                    if (secretCodeDatabase.hasSecretCode(color_hour))
                        secretCodeDatabase.deleteSecretCode(color_hour);
                    secretCodeDatabase.insertSecretCode(color_hour, color_code, secret_code);
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "getSecretData");
                params.put("user_id", "3");
                params.put("forTime", time);
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

}