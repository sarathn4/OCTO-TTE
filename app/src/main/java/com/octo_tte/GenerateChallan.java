package com.octo_tte;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.database.Mydatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class GenerateChallan extends AppCompatActivity {

    int price = 100;
    ArrayList<String> stations_list;
    String penalty_types[];
    Mydatabase mydatabase;
    String payment_type = "Cash";
    Pattern pattern_name;

    SharedPreferences sharedPreferences_invoice,sharedPreferences;
    EditText editText_name,editText_mobile_no;
    Spinner spinner_station,spinner_penalty_type;
    Button button_price,button_ok;
    RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_chalan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pattern_name = Pattern.compile("^[a-zA-Z]+$");
        sharedPreferences_invoice = getSharedPreferences("invoice_count", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("tte_data",MODE_PRIVATE);
        mydatabase = new Mydatabase(this);
        stations_list = mydatabase.getStationsList();
        penalty_types = getResources().getStringArray(R.array.penalty_type);

        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_mobile_no = (EditText) findViewById(R.id.editText_mobile_no);
        spinner_station = (Spinner) findViewById(R.id.spinner_station);
        spinner_penalty_type = (Spinner) findViewById(R.id.spinner_penalty_type);

        button_price = (Button) findViewById(R.id.button_price);
        button_ok = (Button) findViewById(R.id.button_generate);

        editText_mobile_no.setImeOptions(EditorInfo.IME_ACTION_NONE);
        spinner_station.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stations_list));
        spinner_penalty_type.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, penalty_types));

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup_pm);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radiobutton_cash:
                        payment_type = "Cash";
                        break;
                    case R.id.radiobutton_card:
                        payment_type = "Card";
                        break;
                }
            }
        });
        button_price.setText("Rs." + price + ".00");
        editText_mobile_no.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    return button_ok.performClick();
                }
                return false;
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editText_name.getText().toString().trim().equals("")) {
                    editText_name.requestFocus();
                    Toast.makeText(getApplicationContext(), "Enter name", Toast.LENGTH_SHORT).show();
                }
                else if(!(pattern_name.matcher(editText_name.getText().toString().trim()).matches())){
                    editText_name.requestFocus();
                    Toast.makeText(getApplicationContext(),"Invalid name. Please try again",Toast.LENGTH_SHORT).show();
                }
                else if(editText_mobile_no.getText().toString().trim().equals("")){
                    editText_mobile_no.requestFocus();
                    Toast.makeText(getApplicationContext(),"Please enter Mobile Number",Toast.LENGTH_SHORT).show();
                }
                else if(editText_mobile_no.getText().toString().length()!=10){
                    editText_mobile_no.requestFocus();
                    Toast.makeText(getApplicationContext(),"Mobile no should be 10 digits. Please try again",Toast.LENGTH_SHORT).show();
                }
                else if(!(Patterns.PHONE.matcher(editText_mobile_no.getText().toString().trim()).matches())){
                    editText_mobile_no.requestFocus();
                    Toast.makeText(getApplicationContext(),"Invalid Mobile number.Please try again",Toast.LENGTH_SHORT).show();
                }
                else {
                    Calendar calendar = Calendar.getInstance();
                    int invoice_count = sharedPreferences_invoice.getInt("" + calendar.getTime().getDate(), 0);
                    invoice_count++;
                    Log.e("Count", invoice_count + "");
                    String tteid = sharedPreferences.getString("id", "0");
                    Intent intent = new Intent(GenerateChallan.this, ChalanPreview.class);
                    intent.putExtra("name", editText_name.getText().toString());
                    intent.putExtra("mobile", editText_mobile_no.getText().toString());
                    intent.putExtra("station", stations_list.get(spinner_station.getSelectedItemPosition()));
                    intent.putExtra("penalty_type", penalty_types[spinner_penalty_type.getSelectedItemPosition()]);
                    intent.putExtra("amount", price);
                    intent.putExtra("invoice_count", invoice_count);
                    intent.putExtra("invoice_no", calendar.getTime().getDate() + "" + calendar.getTime().getMonth() + "/" + tteid +
                            "/" + (invoice_count));
                    intent.putExtra("payment_type", payment_type);
                    startActivity(intent);
                }
            }
        });
    }
}
