package com.octo_tte;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.analogics.thermalAPI.Bluetooth_Printer_2inch_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.database.ChallansDatabase;
import com.database.Mydatabase;

import java.util.Calendar;

public class ChalanPreview extends AppCompatActivity {


    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    String address = "";

    Mydatabase mydatabase;
    ChallansDatabase challansDatabase;
    Button button_confirm;
    String name = "";
    String mobile = "";
    String station = "";
    String penalty_type = "";
    int amount = 0;
    String payment_type = "";
    String invoice_no = "";
    int invoice_count;
    SharedPreferences sharedPreferences, sharedPreferences_invoice_no;
    TextView challan_textView_name, challan_textView_mobile, challan_textView_station,
            challan_textView_date, challan_textView_tteid, challan_textView_invoice_no,
            challan_textView_paymentmode, challan_textView_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chalan_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        registerReceiver(ActionFoundReceiver, new IntentFilter(
                BluetoothDevice.ACTION_FOUND));

        mydatabase = new Mydatabase(this);
        challansDatabase = new ChallansDatabase(this);
        sharedPreferences = getSharedPreferences("tte_data", MODE_PRIVATE);

        invoice_count = getIntent().getIntExtra("invoice_count", 1);
        name = getIntent().getStringExtra("name");
        mobile = getIntent().getStringExtra("mobile");
        station = getIntent().getStringExtra("station");
        penalty_type = getIntent().getStringExtra("penalty_type");
        amount = getIntent().getIntExtra("amount", 0);
        payment_type = getIntent().getStringExtra("payment_type");
        invoice_no = getIntent().getStringExtra("invoice_no");

        challan_textView_name = (TextView) findViewById(R.id.challan_textView_name);
        challan_textView_mobile = (TextView) findViewById(R.id.challan_textView_mobile);
        challan_textView_station = (TextView) findViewById(R.id.challan_textView_station);
        challan_textView_date = (TextView) findViewById(R.id.challan_textView_date);
        challan_textView_tteid = (TextView) findViewById(R.id.challan_textView_tteid);
        challan_textView_invoice_no = (TextView) findViewById(R.id.challan_textView_invoice_no);
        challan_textView_paymentmode = (TextView) findViewById(R.id.challan_textView_paymentmode);
        challan_textView_amount = (TextView) findViewById(R.id.challan_textView_amount);

        button_confirm = (Button) findViewById(R.id.button_confirm);

        challan_textView_name.setText(name);
        challan_textView_amount.setText("Rs." + amount + ".00");
        challan_textView_date.setText(Calendar.getInstance().getTime().getTime() + "");
        challan_textView_mobile.setText(mobile);
        challan_textView_paymentmode.setText(payment_type);
        challan_textView_tteid.setText(sharedPreferences.getString("id", "0"));
        challan_textView_station.setText(station);
        challan_textView_invoice_no.setText(invoice_no);
        CheckBlueToothState();
        bluetoothAdapter.startDiscovery();
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferences_invoice_no = getSharedPreferences("invoice_count", MODE_PRIVATE);

                Calendar calendar = Calendar.getInstance();
                Log.e("InvoiceCountIntent", "" + invoice_count);
                SharedPreferences.Editor editor = sharedPreferences_invoice_no.edit();
                editor.putInt("" + calendar.getTime().getDate(), invoice_count);
                editor.commit();

                if (payment_type.equals("Card")) {
                    Intent intent = new Intent(ChalanPreview.this, PaymentActivity.class);
                    intent.putExtra("userid", sharedPreferences.getString("userid", "0"));
                    intent.putExtra("tteid", sharedPreferences.getString("id", "0"));
                    intent.putExtra("station_id", mydatabase.getData(station));
                    intent.putExtra("invoice_no", invoice_no);
                    intent.putExtra("payment_date", "" + Calendar.getInstance().getTime());
                    intent.putExtra("passenger_name", name);
                    intent.putExtra("phone", mobile);
                    intent.putExtra("penalty_type", penalty_type);
                    intent.putExtra("description", "Fine to be paid by the passenger");
                    intent.putExtra("challan_amount", 1 + "");
                    intent.putExtra("email", sharedPreferences.getString("email_id", "abc@abc.com"));
                    startActivity(intent);
                } else if (payment_type.equals("Cash")) {

                    CheckBlueToothState();
                    bluetoothAdapter.startDiscovery();

                    challansDatabase.insertChallan(invoice_no, sharedPreferences.getString("userid", "0"),
                            sharedPreferences.getString("id", "0"), "1", "" + calendar.get(Calendar.YEAR) + "-" +
                                    getNumber(calendar.get(Calendar.MONTH) + 1) + "-" + getNumber(calendar.get(Calendar.DATE)),
                            name, mobile, penalty_type, "Fine to be paid by the passenger",
                            amount + "", payment_type);
                    printChallan();

                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                }
            }
        });
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("device name", device.getName() + "");
                if ((device.getName() + "").equals("ANTHERMAL"))
                    address = device.getAddress() + "";
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
                    Toast.makeText(getApplicationContext(), "Bluetooth is currently in device discovery process.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth isn't enabled", Toast.LENGTH_SHORT).show();
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

    public String getString(String title, String value) {
        String result = "";
        int title_length = title.length();
        int value_length = value.length();
        StringBuilder stringBuilder = new StringBuilder("");
        int space_length = 38 - (title_length + value_length);
        stringBuilder.append(title);
        for (int i = 0; i < space_length; i++) {
            stringBuilder.append(" ");
        }
        stringBuilder.append(value);
        result = stringBuilder.toString();
        return result;
    }

    public void printChallan() {
        Log.e("Address", address);
        if (address.isEmpty()) {
            bluetoothAdapter.startDiscovery();
            Toast.makeText(getApplicationContext(), "Device is not found", Toast.LENGTH_SHORT).show();
        } else {
            Bluetooth_Printer_2inch_ThermalAPI preparePrintData = new Bluetooth_Printer_2inch_ThermalAPI();

            String ch_header = preparePrintData.font_Courier_38("\n\t\t\tIndian Railways\n\n");
            String ch_subheader = preparePrintData.font_Courier_38("\n\t\t\t\tIndia\n\n");
            String ch_date = preparePrintData.font_Courier_38(getString("Date:", Calendar.getInstance().getTime() + "") + "\n");
//                    String date = preparePrintData.font_Courier_38("Date:\t" + Calendar.getInstance().getTime() + "\n\n");
            String ch_tteid = preparePrintData.font_Courier_38(getString("TTE id:", "10") + "\n");
            String ch_invoice_no = preparePrintData.font_Courier_38(getString("Invoice no:", invoice_no) + "\n");
            String ch_dotted_lines = preparePrintData.font_Courier_38("--------------------------------------" + "\n");
            String ch_challan_receipt_title = preparePrintData.font_Courier_38("\t\t\tChallan Receipt" + "\n");

            String ch_name = preparePrintData.font_Courier_38(getString("Name:", name) + "\n");
            String ch_mobile_no = preparePrintData.font_Courier_38(getString("Mobile no:", mobile) + "\n");
            String ch_station_name = preparePrintData.font_Courier_38(getString("Station:", station) + "\n");
            String ch_penalty = preparePrintData.font_Courier_38(getString("Penalty:", penalty_type) + "\n");
            String ch_amount = preparePrintData.font_Courier_38(getString("Amount:", "Rs." + amount + ".00") + "\n");

            String ch_payment_mode = preparePrintData.font_Courier_38(getString("Payment mode:", "Cash") + "\n");
            String tte_sign = preparePrintData.font_Courier_38(getString("Sign:", "________________") + "\n");
            String printdata = (ch_header + ch_subheader + ch_date + ch_tteid + ch_invoice_no + ch_dotted_lines + ch_challan_receipt_title + ch_name
                    + ch_mobile_no + ch_station_name + ch_penalty + ch_amount + ch_dotted_lines + ch_payment_mode + tte_sign + "\n\n\n").toUpperCase();

            AnalogicsThermalPrinter printer = new AnalogicsThermalPrinter();

            printer.Call_PrintertoPrint(address, printdata);
            Toast.makeText(getApplicationContext(), "Challan Generated", Toast.LENGTH_SHORT).show();
        }
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

//"userid="+userid+"&tteid="+tteid+"&station_id="+station_id+"&invoice_no="+invoice_no+
//        "&payment_date="+payment_date+"&passenger_name="+passenger_name+"&phone="+phone+
//        "&penalty_type="+penalty_type+"&description="+description+"&challan_amount=+"+challan_amount+
//        "&email="+email;