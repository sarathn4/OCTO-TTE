package com.octo_tte;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.database.ValidatedTicketsDatabase;
import com.google.zxing.BarcodeFormat;
import com.jwetherell.quick_response_code.encoder.QRCodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ValidatedTicketView extends AppCompatActivity {

    public String TAG = "ValidateTicketOnline";
//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;
    String code_data = "";
    String validity = "";
    int smallerDimension;
    ValidatedTicketsDatabase validatedTicketsDatabase;
    ImageView imageView_qrcode, imageView_valid_invalid;
    TextView textView_valid_invalid;
    TextView textView_validated_count;
    String status = "";
    String passenger_name = "";
    String ticketid = "";
    String passenger_id = "";
    String station_id = "";
    String tte_id = "";
    String validated_on = "";
    String dob = "";
    boolean check = false;
    SharedPreferences sharedPreferences, sharedPreferences_mode;
    Button button_ok;

    PopupWindow popupWindow;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validated_ticket_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        validatedTicketsDatabase = new ValidatedTicketsDatabase(this);
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);

        sharedPreferences = getSharedPreferences("tte_data", MODE_PRIVATE);
        tte_id = sharedPreferences.getString("id", "2");
        station_id = sharedPreferences.getString("station_id", "2");
        Calendar calendar = Calendar.getInstance();
        validated_on = "" + calendar.get(Calendar.YEAR) + "-" +
                getNumber(calendar.get(Calendar.MONTH) + 1) + "-" + getNumber(calendar.get(Calendar.DATE));
        sharedPreferences_mode = getSharedPreferences("online", MODE_PRIVATE);
        check = sharedPreferences_mode.getBoolean("mode", false);
        Log.e("Mode Check", check + "");
        textView_validated_count = (TextView) findViewById(R.id.textView_validated_count);
        imageView_qrcode = (ImageView) findViewById(R.id.imageView_qrcode);
        imageView_valid_invalid = (ImageView) findViewById(R.id.imageView_valid_invalid);
        textView_valid_invalid = (TextView) findViewById(R.id.textView_valid_invalid);
        button_ok = (Button) findViewById(R.id.button_ok);

        try {
            code_data = getIntent().getStringExtra("code");
            validity = getIntent().getStringExtra("validity");

            int index = code_data.lastIndexOf(" ");
            if (index != -1) {
                int first = code_data.indexOf(" ");
                int second = code_data.indexOf(" ", first + 1);
                int third = code_data.indexOf(" ", second + 1);
                int fourth = code_data.lastIndexOf(" ");
                if (second != -1) {
                    ticketid = code_data.substring(first + 1, second);
                    passenger_id = code_data.substring(second + 1, third - 1);
                }
                if (third != -1) {
                    passenger_name = code_data.substring(third + 1, fourth);
                }
                if (fourth != -1) {
                    dob = code_data.substring(fourth + 1, code_data.length());
                }
            }
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            int dimension = width < height ? width : height;
            smallerDimension = dimension * 7 / 8;
            QRCodeEncoder qrCodeEncoder;
            qrCodeEncoder = new QRCodeEncoder(code_data, null, "TEXT_TYPE", BarcodeFormat.QR_CODE.toString(), smallerDimension);
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            imageView_qrcode.setImageBitmap(bitmap);

            if (validity.equals("valid")) {
                imageView_valid_invalid.setImageDrawable(getResources().getDrawable(R.drawable.icon_valid));
                textView_valid_invalid.setText("VALID " + decryptData(passenger_name) + " " + dob);
            } else if (validity.equals("invalid")) {
                imageView_valid_invalid.setImageDrawable(getResources().getDrawable(R.drawable.icon_invalid));
                textView_valid_invalid.setText("INVALID");
            }

            button_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validity.equals("valid")) {
                        validatedTicketsDatabase.insertTicket(ticketid, passenger_id, station_id,
                                tte_id, validated_on, validity);

                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                    } else if (validity.equals("invalid")) {
                        Intent intent = new Intent(getApplicationContext(), GenerateChallan.class);
                        startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (check) {
            textView_validated_count.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                public void run() {
//                    new BuyTicket().execute();
                    validateTicketOnline();
                }
            }, 500);
        } else {
            textView_validated_count.setVisibility(View.GONE);
        }
    }

//    class BuyTicket extends AsyncTask<Void, Void, Void> {
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
//                params.add(new BasicNameValuePair("actiontype", "validateTicketOnline"));
//                params.add(new BasicNameValuePair("tte_id", tte_id));
//                params.add(new BasicNameValuePair("station_id", station_id));
//                params.add(new BasicNameValuePair("passenger_id", passenger_id));
//                params.add(new BasicNameValuePair("ticketid", ticketid));
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
//            if (message.equals("Success")) {
//                try {
//                    JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                    JSONObject passengetTicket = msgcontent.getJSONObject("responseInfo");
//                    status = passengetTicket.getString("ticketValid");
//                    passenger_name = passengetTicket.getString("passengerName");
//
//                    if (passengetTicket.has("validated_count")) {
//                        textView_validated_count.setText("No of Times Validated:" +
//                                passengetTicket.getString("validated_count"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    status = "Invalid";
//                    popupWindow.dismiss();
//                }
//            } else
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//
//
//            if (status.equals("Valid")) {
//                imageView_valid_invalid.setImageDrawable(getResources().getDrawable(R.drawable.icon_valid));
//                validity = "valid";
//            } else {
//                imageView_valid_invalid.setImageDrawable(getResources().getDrawable(R.drawable.icon_invalid));
//                validity = "invalid";
//            }
//            if (passenger_name.isEmpty() || passenger_name.equals("") || passenger_name.equals("null")) {
//                textView_valid_invalid.setText(status);
//            } else {
//                textView_valid_invalid.setText(status + " " + passenger_name);
//            }
//            popupWindow.dismiss();
//        }
//    }

    public void validateTicketOnline() {
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
                    Log.e("Check", message);
                    parseData(response);
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
                params.put("actiontype", "validateTicketOnline");
                params.put("tte_id", tte_id);
                params.put("station_id", station_id);
                params.put("passenger_id", passenger_id);
                params.put("ticketid", ticketid);
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

    public void parseData(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        String message = jsonObject.getString("message");
        if (message.equals("Success")) {
            try {
                JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
                JSONObject passengetTicket = msgcontent.getJSONObject("responseInfo");
                status = passengetTicket.getString("ticketValid");
                passenger_name = passengetTicket.getString("passengerName");

                if (passengetTicket.has("validated_count")) {
                    textView_validated_count.setText("No of Times Validated:" +
                            passengetTicket.getString("validated_count"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                status = "Invalid";
                popupWindow.dismiss();
            }
        } else
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


        if (status.equals("Valid")) {
            imageView_valid_invalid.setImageDrawable(getResources().getDrawable(R.drawable.icon_valid));
            validity = "valid";
        } else {
            imageView_valid_invalid.setImageDrawable(getResources().getDrawable(R.drawable.icon_invalid));
            validity = "invalid";
        }
        if (passenger_name.isEmpty() || passenger_name.equals("") || passenger_name.equals("null")) {
            textView_valid_invalid.setText(status);
        } else {
            textView_valid_invalid.setText(status + " " + passenger_name);
        }
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    public String decryptData(String data) {
        Log.e("Check", "" + data);
        String decryptedData = "";
        for (int i = 0; i < (data.length() - 1); i = i + 2) {
            Log.e("Number", "" + data.substring(i, i + 2));
            Log.e("Letter", "" + getLetter(data.substring(i, i + 2)));
            decryptedData += getLetter(data.substring(i, i + 2));
            Log.e("DecryptedData", "" + decryptedData);
        }
        return decryptedData;
    }

    public String getLetter(String numbers) {
        String letter = "";
        switch (numbers) {
            case "01":
                letter = "A";
                break;
            case "02":
                letter = "B";
                break;
            case "03":
                letter = "C";
                break;
            case "04":
                letter = "D";
                break;
            case "05":
                letter = "E";
                break;
            case "06":
                letter = "F";
                break;
            case "07":
                letter = "G";
                break;
            case "08":
                letter = "H";
                break;
            case "09":
                letter = "I";
                break;
            case "10":
                letter = "J";
                break;
            case "11":
                letter = "K";
                break;
            case "12":
                letter = "L";
                break;
            case "13":
                letter = "M";
                break;
            case "14":
                letter = "N";
                break;
            case "15":
                letter = "O";
                break;
            case "16":
                letter = "P";
                break;
            case "17":
                letter = "Q";
                break;
            case "18":
                letter = "R";
                break;
            case "19":
                letter = "S";
                break;
            case "20":
                letter = "T";
                break;
            case "21":
                letter = "U";
                break;
            case "22":
                letter = "V";
                break;
            case "23":
                letter = "W";
                break;
            case "24":
                letter = "X";
                break;
            case "25":
                letter = "Y";
                break;
            case "26":
                letter = "Z";
                break;
            default:
                letter = " ";
        }
        return letter;
    }

    public String getNumber(int number) {
        String no = "";
        if (number / 10 == 0) {
            no = "0" + number;
        } else {
            no = "" + number;
        }
        return no;
    }
}


// {"message":"Success",
// "msgcontent":{
// "requestParam":{"userid":"52","actiontype":"getTicketInfo","ticketnumber":"CSMUMADH1046PF"},
// "responseInfo":{"id":"226","passengerid":"30","from_station":"16","to_station":"16","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"ticket_code":"SPMF1C","ticket_amount":"1","purchased_on":"2015-09-02 11:09:25","status_date":"0000-00-00 00:00:00","txnid":"3b002343d585b5c3f95a","inactivatedDate":"0000-00-00 00:00:00","status":"Inactive"}}}

//'actiontype' = 'validateTicketOnline',
//'station_id' = 2,
//'tte_id' = 15,
//'passenger_id' = 16,
//'ticketid' = '223'

// {"message":"Success",
// "msgcontent":{"requestParam":
// {"actiontype":"validateTicketOnline","tte_id":"58","station_id":"1","passenger_id":"33","ticketid":"257"},
// "responseInfo":{"passengerName":"Sarath","ticketValid":"Valid"}}}

//{"message":"Success",
// "msgcontent":{"requestParam":
// {"actiontype":"validateTicketOnline","tte_id":"58","station_id":"1","passenger_id":"0","ticketid":"0"},
// "responseInfo":"Invalid"}}
