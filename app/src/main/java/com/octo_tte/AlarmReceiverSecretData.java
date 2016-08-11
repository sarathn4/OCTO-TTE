package com.octo_tte;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.VolleyRequestData;
import com.database.SecretCodeDatabase;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AlarmReceiverSecretData extends BroadcastReceiver {

    String TAG = "GetSecretData";
    //    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;
    SecretCodeDatabase secretCodeDatabase;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Calendar calendar = Calendar.getInstance();
        String time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
        secretCodeDatabase = new SecretCodeDatabase(context);
//        new GetSecretCode().execute(time);
        getSecretCode(time);
        calendar.add(Calendar.MINUTE, -31);
        time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
//        new GetSecretCode().execute(time);
        getSecretCode(time);
    }

//    class GetSecretCode extends AsyncTask<String, Void, Void> {
//        JSONObject jsonObject;
//        String color_code ="";
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
//                if(secretCodeDatabase.hasSecretCode(color_hour))
//                    secretCodeDatabase.deleteSecretCode(color_hour);
//                secretCodeDatabase.insertSecretCode(color_hour,color_code,secret_code);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
//
//                if(!(secretCodeDatabase.insertSecretCode(color_hour,color_code,secret_code))){
//                    secretCodeDatabase.deleteSecretCode(color_hour);
//                    secretCodeDatabase.insertSecretCode(color_hour,color_code,secret_code);
//                }
                    //popupWindow.dismiss();
//                    notifyUser(message+"");
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
