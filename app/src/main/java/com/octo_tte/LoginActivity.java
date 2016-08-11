package com.octo_tte;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.VolleyRequestData;
import com.database.SecretCodeDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginRequest";
    Button signin, admin;
//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntentValidatedTickets;
    private PendingIntent alarmIntentSecretData;

    PopupWindow popupWindow;
    View view;
    EditText editText_email, editText_password;
    SharedPreferences sharedPreferences;
    SecretCodeDatabase secretCodeDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        secretCodeDatabase = new SecretCodeDatabase(this);
        sharedPreferences = getSharedPreferences("tte_data", MODE_PRIVATE);

        editText_email = (EditText) findViewById(R.id.login_editText_email);
        editText_password = (EditText) findViewById(R.id.login_editText_password);

        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        signin = (Button) findViewById(R.id.login_button_login);
        admin = (Button) findViewById(R.id.login_button_mis);

        editText_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    return signin.performClick();
                }
                return false;
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(editText_email.getText().toString().trim().equals("")) && !(editText_password.getText().toString().trim().equals(""))) {
                    if (Patterns.EMAIL_ADDRESS.matcher(editText_email.getText().toString().trim()).matches())
//                        new Login().execute();
                        login();
                    else
                        Toast.makeText(getApplicationContext(), "Invalid email. Please try again", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Enter email id and password", Toast.LENGTH_SHORT).show();

            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
            }
        });
    }

//    class Login extends AsyncTask<Void, Void, Void> {
//
//        JSONObject jsonObject;
//        String message = "";
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 40);
//        }
//
//        @Override
//        protected Void doInBackground(Void... param) {
//            try {
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "logintte"));
//                params.add(new BasicNameValuePair("username", editText_email.getText().toString()));
//                params.add(new BasicNameValuePair("password", editText_password.getText().toString()));
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
//                Log.e("Check", message);
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
//            Log.e("Check", "postEx Started");
//            if (message.equals("Error")) {
//                popupWindow.dismiss();
//                Toast.makeText(getApplicationContext(), "Invalid Credentials.Please try again.", Toast.LENGTH_SHORT).show();
//            } else if (message.equals("TTE Logged in")) {
//                try {
//                    Log.e("Check", "passenger details extraction");
//                    JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                    JSONObject responseInfo = msgcontent.getJSONObject("responseInfo");
//                    JSONObject userdata = null;
//                    if (responseInfo.has("0"))
//                        userdata = responseInfo.getJSONObject("0");
//                    else
//                        userdata = responseInfo.getJSONObject("1");
//
//                    JSONObject users = userdata.getJSONObject("ttes");
//                    String user_name = users.getString("fullname");
//                    String user_id = users.getString("userid");
//                    String email_id = users.getString("email_id");
//                    String mobile = users.getString("mobile");
////                    String address = users.getString("address");
////                    String city_id = users.getString("city_id");
////                    String city_name = users.getString("city_name");
//                    String id = users.getString("id");
//
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("user_name", user_name);
//                    editor.putString("email_id", email_id);
//                    editor.putString("station_id", "1");
//                    Log.e("CheckID", id);
//                    editor.putString("user_id", user_id);
//                    editor.putString("mobile_no", mobile);
//                    editor.putString("id", id);
//                    editor.commit();
//
//                    popupWindow.dismiss();
//
//                    Calendar calendar = Calendar.getInstance();
//                    String time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
//                    new GetSecretCode().execute(time);
//                    calendar.add(Calendar.MINUTE, -31);
//                    time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
//                    new GetSecretCode().execute(time);
//                    setAlarm();
//                    Intent intent = new Intent(LoginActivity.this, Home.class);
//                    startActivity(intent);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                popupWindow.dismiss();
//                Toast.makeText(getApplicationContext(), message + ".Please try again later.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

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
//                if (secretCodeDatabase.hasSecretCode(color_hour))
//                    secretCodeDatabase.deleteSecretCode(color_hour);
//                secretCodeDatabase.insertSecretCode(color_hour, color_code, secret_code);
////
////                if(!(secretCodeDatabase.insertSecretCode(color_hour,color_code,secret_code))){
////                    secretCodeDatabase.deleteSecretCode(color_hour);
////                    secretCodeDatabase.insertSecretCode(color_hour,color_code,secret_code);
////                }
//                //popupWindow.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void setAlarm() {
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiverValidatedTickets.class);
        alarmIntentValidatedTickets = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                60 * 1000, 60 * 1000, alarmIntentValidatedTickets);

        Intent secretdatain_intent = new Intent(getApplicationContext(), AlarmReceiverSecretData.class);
        alarmIntentSecretData = PendingIntent.getBroadcast(getApplicationContext(), 0, secretdatain_intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HOUR, alarmIntentSecretData);
    }

    @Override
    public void onBackPressed() {
        showExitAlert();
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

    public void login() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    parseLoginData(response);
                    Log.e("Check", message);
                    pDialog.hide();
//                    notifyUser(message+"");
                } catch (Exception e) {
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
                params.put("actiontype", "logintte");
                params.put("username", editText_email.getText().toString());
                params.put("password", editText_password.getText().toString());
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

    public void parseLoginData(String response) throws JSONException {
        Log.e("Check", "postEx Started");
        JSONObject jsonObject = new JSONObject(response);
        String message = jsonObject.getString("message");
        if (message.equals("Error")) {
            popupWindow.dismiss();
            Toast.makeText(getApplicationContext(), "Invalid Credentials.Please try again.", Toast.LENGTH_SHORT).show();
        } else if (message.equals("TTE Logged in")) {
            try {
                Log.e("Check", "passenger details extraction");
                JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
                JSONObject responseInfo = msgcontent.getJSONObject("responseInfo");
                JSONObject userdata = null;
                if (responseInfo.has("0"))
                    userdata = responseInfo.getJSONObject("0");
                else
                    userdata = responseInfo.getJSONObject("1");

                JSONObject users = userdata.getJSONObject("ttes");
                String user_name = users.getString("fullname");
                String user_id = users.getString("userid");
                String email_id = users.getString("email_id");
                String mobile = users.getString("mobile");
//                    String address = users.getString("address");
//                    String city_id = users.getString("city_id");
//                    String city_name = users.getString("city_name");
                String id = users.getString("id");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_name", user_name);
                editor.putString("email_id", email_id);
                editor.putString("station_id", "1");
                Log.e("CheckID", id);
                editor.putString("user_id", user_id);
                editor.putString("mobile_no", mobile);
                editor.putString("id", id);
                editor.commit();

                popupWindow.dismiss();

                Calendar calendar = Calendar.getInstance();
                String time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
//                new GetSecretCode().execute(time);
                getSecretCode(time);
                calendar.add(Calendar.MINUTE, -31);
                time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
//                new GetSecretCode().execute(time);
                getSecretCode(time);
                setAlarm();
                Intent intent = new Intent(LoginActivity.this, Home.class);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            popupWindow.dismiss();
            Toast.makeText(getApplicationContext(), message + ".Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getSecretCode(final String time) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
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
//
//                if(!(secretCodeDatabase.insertSecretCode(color_hour,color_code,secret_code))){
//                    secretCodeDatabase.deleteSecretCode(color_hour);
//                    secretCodeDatabase.insertSecretCode(color_hour,color_code,secret_code);
//                }
                    //popupWindow.dismiss();
                    pDialog.hide();
//                    notifyUser(message+"");
                } catch (Exception e) {
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

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }
}

//{"message":"TTE Logged in",
// "msgcontent":
// {"requestParam":
// {"actiontype":"logintte","username":"suresh@abc.com","password":"df94ffd25eab310eb58b50c4d75b6166539457d6","tteid":null},
// "responseInfo":{"users":{"username":"suresh@abc.com","id":"51"},
// "0":{"ttes":{"id":"17","userid":"51","fullname":"Suresh Kumar","email_id":"suresh@abc.com","mobile":"9876543210","emp_code":"sr1234","zone_id":"0","division_id":"0","verified":"1"}}}}}
