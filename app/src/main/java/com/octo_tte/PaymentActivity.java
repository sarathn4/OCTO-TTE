package com.octo_tte;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.analogics.thermalAPI.Bluetooth_Printer_2inch_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.database.ChallansDatabase;
import com.database.Mydatabase;

import java.util.Calendar;

public class PaymentActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    String address = "";

    WebView webView_payment_gateway;
    String url = "";
    String postData = "";

    String userid = "";
    String tteid = "";
    String station_id = "";
    String invoice_no = "";
    String payment_date = "";
    String passenger_name = "";
    String phone = "";
    String penalty_type = "";
    String description = "";
    String challan_amount = "";
    String email = "";

    Mydatabase mydatabase;
    ChallansDatabase challansDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        registerReceiver(ActionFoundReceiver, new IntentFilter(
                BluetoothDevice.ACTION_FOUND));

        webView_payment_gateway = (WebView) findViewById(R.id.webView_payment_gateway);
        url = "http://goeticket.com/ticketingsystem/challan";

        mydatabase = new Mydatabase(this);
        challansDatabase = new ChallansDatabase(this);

        userid = getIntent().getStringExtra("userid");
        tteid = getIntent().getStringExtra("tteid");
        station_id = getIntent().getStringExtra("station_id");
        invoice_no = getIntent().getStringExtra("invoice_no");
        payment_date = getIntent().getStringExtra("payment_date");
        passenger_name = getIntent().getStringExtra("passenger_name");
        phone = getIntent().getStringExtra("phone");
        penalty_type = getIntent().getStringExtra("penalty_type");
        description = getIntent().getStringExtra("description");
        challan_amount = getIntent().getStringExtra("challan_amount");
        email = getIntent().getStringExtra("email");

        postData = "userid="+userid+"&tteid="+tteid+"&station_id="+station_id+"&invoice_no="+invoice_no+
                "&payment_date="+payment_date+"&passenger_name="+passenger_name+"&phone="+phone+
                "&penalty_type="+penalty_type+"&description="+description+"&challan_amount=+"+challan_amount+
                "&email="+email;

        WebSettings webSettings = webView_payment_gateway.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        CheckBlueToothState();
        bluetoothAdapter.startDiscovery();

        webView_payment_gateway.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Log.e("URL", url);
                    }

                    @Override
                    public void onLoadResource(WebView view, String url) {
                        super.onLoadResource(view, url);
                        Log.e("Loading URL", url);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        Log.e("Finished URL", url);
                        if(url.startsWith("http://goeticket.com/ticketingsystem/challan/showsuccess?status=success")){

                            challansDatabase.insertChallan(invoice_no, userid,
                                    tteid, "1", "" + Calendar.getInstance().getTime(),
                                    passenger_name, phone, penalty_type, "Fine to be paid by the passenger",
                                    challan_amount + "", "Card");
                            printChallan();
                            Toast.makeText(getApplicationContext(),"Challan generated",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),Home.class);
                            startActivity(intent);
                        }
                    }
                }
        );

        webView_payment_gateway.postUrl(url, postData.getBytes());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView_payment_gateway.canGoBack()) {
            webView_payment_gateway.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("device name",device.getName()+"");
                if((device.getName()+"").equals("ANTHERMAL"))
                    address = device.getAddress()+"";
//                btArrayAdapter.add(device.getAddress() + "\n"
//                        + device.getName());
//                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };
    private void CheckBlueToothState() {
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is currently in device discovery process.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is enabled",Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth isn't enabled",Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBlueToothState();
        }
    }

    public String getString(String title,String value){
        String result = "";
        int title_length = title.length();
        int value_length = value.length();
        StringBuilder stringBuilder = new StringBuilder("");
        int space_length = 38-(title_length+value_length);
        stringBuilder.append(title);
        for(int i= 0;i<space_length;i++){
            stringBuilder.append(" ");
        }
        stringBuilder.append(value);
        result = stringBuilder.toString();
        return result;
    }

    public void printChallan(){
        Log.e("Address",address);
        if(address.isEmpty()) {
            bluetoothAdapter.startDiscovery();
            Toast.makeText(getApplicationContext(), "Device is not found", Toast.LENGTH_SHORT).show();
        }
        else{
            Bluetooth_Printer_2inch_ThermalAPI preparePrintData= new Bluetooth_Printer_2inch_ThermalAPI();

            String ch_header = preparePrintData.font_Courier_38("\n\t\t\tIndian Railways\n\n");
            String ch_subheader = preparePrintData.font_Courier_38("\n\t\t\t\tIndia\n\n");
            String ch_date = preparePrintData.font_Courier_38(getString("Date:",Calendar.getInstance().getTime()+"") + "\n");
//                    String date = preparePrintData.font_Courier_38("Date:\t" + Calendar.getInstance().getTime() + "\n\n");
            String ch_tteid = preparePrintData.font_Courier_38(getString("TTE id:","10" )+ "\n");
            String ch_invoice_no = preparePrintData.font_Courier_38(getString("Invoice no:",invoice_no) + "\n");
            String ch_dotted_lines = preparePrintData.font_Courier_38("--------------------------------------" + "\n");
            String ch_challan_receipt_title = preparePrintData.font_Courier_38("\t\t\tChallan Receipt" + "\n");

            String ch_name = preparePrintData.font_Courier_38(getString("Name:",passenger_name) + "\n");
            String ch_mobile_no = preparePrintData.font_Courier_38(getString("Mobile no:",phone) + "\n");
            String ch_station_name = preparePrintData.font_Courier_38(getString("Station:",mydatabase.getStationName(station_id)) + "\n");
            String ch_penalty = preparePrintData.font_Courier_38(getString("Penalty:",penalty_type) + "\n");
            String ch_amount = preparePrintData.font_Courier_38(getString("Amount:","Rs."+challan_amount+".00") + "\n");

            String ch_payment_mode = preparePrintData.font_Courier_38(getString("Payment mode:","Card") + "\n");

            String tte_sign = preparePrintData.font_Courier_38(getString("Sign:","________________")+"\n");
            String printdata = (ch_header+ch_subheader+ch_date+ch_tteid+ch_invoice_no+ch_dotted_lines+ch_challan_receipt_title+ch_name
                    +ch_mobile_no+ch_station_name+ch_penalty+ch_amount+ch_dotted_lines+ch_payment_mode+tte_sign+"\n\n\n").toUpperCase();

            AnalogicsThermalPrinter printer=new AnalogicsThermalPrinter();

            printer.Call_PrintertoPrint(address,printdata);
        }
    }
}
