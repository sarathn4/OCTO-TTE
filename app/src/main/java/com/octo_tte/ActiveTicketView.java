package com.octo_tte;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.data.ActiveTicket;
import com.database.Mydatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ActiveTicketView extends AppCompatActivity {

    TextView textView_ticketno, textView_date, textView_user, textView_source, textView_destination,
            textView_no_of_tickets, textView_purchaseddate, textView_validdate, textView_period, textView_category;
    ImageView imageView_qrcode, imageView_route;
    LinearLayout layout_ticket_type;
    RelativeLayout ticket_layout_days, ticket_layout_hours, ticket_layout_mins, ticket_layout_secs,
            ticket_layout_millisecs;
    TextView ticket_textView_day, ticket_textView_hour, ticket_textView_min, ticket_textView_sec,
            ticket_textView_millisec;
    Button button_send_to_mail;
    ImageView imageView_document, imageView_profile;
    Spinner spinner;
    ActiveTicket activeTicket;

    Mydatabase mydatabase;
    PopupWindow popupWindow_image;
    View view, view_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_ticket_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activeTicket = new ActiveTicket();
        activeTicket = getIntent().getParcelableExtra("activeTicket");

        mydatabase = new Mydatabase(this);
        imageView_profile = (ImageView) findViewById(R.id.image_profile);
        imageView_document = (ImageView) findViewById(R.id.imageView_document);
        spinner = (Spinner) findViewById(R.id.spinner_doc);
        textView_ticketno = (TextView) findViewById(R.id.ticketView_ticketno);
        textView_date = (TextView) findViewById(R.id.ticketView_date);
        textView_user = (TextView) findViewById(R.id.ticketView_name);
        textView_source = (TextView) findViewById(R.id.ticketView_source);
        textView_destination = (TextView) findViewById(R.id.ticketView_destination);
        textView_purchaseddate = (TextView) findViewById(R.id.ticketView_purchased_date);
        textView_validdate = (TextView) findViewById(R.id.ticketView_valid_date);
        textView_no_of_tickets = (TextView) findViewById(R.id.ticketView_no_of_tickets);
        textView_period = (TextView) findViewById(R.id.ticketView_period);
        textView_category = (TextView) findViewById(R.id.ticketView_category);
        imageView_qrcode = (ImageView) findViewById(R.id.ticketView_qrcode);
        imageView_route = (ImageView) findViewById(R.id.ticketView_route);
        layout_ticket_type = (LinearLayout) findViewById(R.id.layout_ticket_type);

        ticket_layout_days = (RelativeLayout) findViewById(R.id.ticket_layout_days);
        ticket_layout_hours = (RelativeLayout) findViewById(R.id.ticket_layout_hours);
        ticket_layout_mins = (RelativeLayout) findViewById(R.id.ticket_layout_mins);
        ticket_layout_secs = (RelativeLayout) findViewById(R.id.ticket_layout_secs);
        ticket_layout_millisecs = (RelativeLayout) findViewById(R.id.ticket_layout_millisecs);

        ticket_textView_day = (TextView) findViewById(R.id.ticket_textView_day);
        ticket_textView_hour = (TextView) findViewById(R.id.ticket_textView_hour);
        ticket_textView_min = (TextView) findViewById(R.id.ticket_textView_min);
        ticket_textView_sec = (TextView) findViewById(R.id.ticket_textView_sec);
        ticket_textView_millisec = (TextView) findViewById(R.id.ticket_textView_millisec);

        button_send_to_mail = (Button) findViewById(R.id.button_send_mail);
        button_send_to_mail.setText("Document: " + activeTicket.getProof_document());
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("" + activeTicket.getProof_document());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_title_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_list_item);

        spinner.setAdapter(arrayAdapter);

        if (!activeTicket.getPhoto().equals("")) {
            byte[] imageData = Base64.decode(activeTicket.getPhoto(), Base64.DEFAULT);
            final Bitmap bitmap_profile = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            imageView_profile.setImageBitmap(bitmap_profile);
            imageView_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bitmap_profile != null) {

                        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view_image = inflater.inflate(R.layout.popup_touch_image, null);
                        TouchImageView touchImageView;
                        touchImageView = (TouchImageView) view_image.findViewById(R.id.touch_imageview);
                        popupWindow_image = new PopupWindow(view_image, ViewGroup.LayoutParams.FILL_PARENT,
                                ViewGroup.LayoutParams.FILL_PARENT, true);
                        popupWindow_image.setFocusable(false);
                        popupWindow_image.setBackgroundDrawable(new BitmapDrawable());
                        popupWindow_image.setOutsideTouchable(true);
                        touchImageView.setImageBitmap(bitmap_profile);
                        popupWindow_image.showAtLocation(view_image, Gravity.CENTER, 0, 40);
                    }
                }
            });
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String dateInString = "7-Jun-2013";
        long millisecs = 0;
        Calendar calendar = Calendar.getInstance();
        Date current_date = calendar.getTime();
        try {
            Date valid_date = formatter.parse(activeTicket.getValid_date());
            millisecs = valid_date.getTime() - current_date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        MyCountDownTimer myCountDownTimer = new MyCountDownTimer(millisecs, 100);

        textView_ticketno.setText(activeTicket.getTicket_no().toString());
        textView_purchaseddate.setText(getSplitDateAndTime(activeTicket.getActivated_date()));

        textView_date.setText(getSplitDateAndTime(activeTicket.getPurchased_date()));
        textView_validdate.setText(getSplitDateAndTime(activeTicket.getValid_date()));
        textView_no_of_tickets.setText(activeTicket.getNo_of_tickets() + " Tickets");
        textView_source.setText(mydatabase.getStationName(activeTicket.getFrom_station()));
        textView_user.setText("User");
        if (activeTicket.getTicket_type().equals("Platform")) {
            LinearLayout destinationlinearLayout = (LinearLayout) findViewById(R.id.ticketView_destination_layout);
            destinationlinearLayout.setVisibility(View.INVISIBLE);
            imageView_route.setVisibility(View.INVISIBLE);
            textView_category.setVisibility(View.GONE);
            textView_period.setVisibility(View.GONE);
        } else
            textView_destination.setText(mydatabase.getStationName(activeTicket.getTo_station()));

        if (activeTicket.getTicket_type().equals("Platform"))
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_pf));
        else if (activeTicket.getTicket_type().equals("Unreserved Ticket")) {
            textView_category.setVisibility(View.VISIBLE);
            textView_period.setVisibility(View.GONE);
            textView_category.setText(activeTicket.getTicket_category());
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_uts));
        } else if (activeTicket.getTicket_type().equals("MonthlyPass")) {
            textView_category.setVisibility(View.VISIBLE);
            textView_period.setVisibility(View.VISIBLE);
            textView_category.setText(activeTicket.getTicket_category());
            textView_period.setText(activeTicket.getTicket_period());
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_st));
        }
        myCountDownTimer.start();
    }

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

            long days = 0;
            long hours = 0;
            long minutes = 0;
            long secs = 0;
            long millisecs = 0;

            days = TimeUnit.MILLISECONDS.toDays(millisInFuture);
            hours = TimeUnit.MILLISECONDS.toHours(millisInFuture)
                    - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS
                    .toDays(millisInFuture));
            minutes = TimeUnit.MILLISECONDS.toMinutes(millisInFuture)
                    - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                    .toHours(millisInFuture));
            secs = TimeUnit.MILLISECONDS.toSeconds(millisInFuture)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(millisInFuture));
            millisecs = (TimeUnit.MILLISECONDS.toMillis(millisInFuture)
                    - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS
                    .toSeconds(millisInFuture))) % 10;

            if (days < 0)
                ticket_layout_days.setVisibility(View.GONE);
            else {
                ticket_layout_days.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                ticket_layout_days.setVisibility(View.VISIBLE);
                ticket_layout_days.setPadding(10, 0, 10, 0);
                ticket_textView_day.setText(days + "");
            }
            if (hours < 0)
                ticket_layout_hours.setVisibility(View.GONE);
            else {
                if (days < 0)
                    ticket_layout_hours.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_hours.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_hours.setVisibility(View.VISIBLE);
                ticket_layout_hours.setPadding(10, 0, 10, 0);
                ticket_textView_hour.setText(hours + "");
            }
            if (minutes < 0)
                ticket_layout_mins.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0)
                    ticket_layout_mins.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_mins.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_mins.setVisibility(View.VISIBLE);
                ticket_layout_mins.setPadding(10, 0, 10, 0);
                ticket_textView_min.setText(minutes + "");
            }
            if (secs < 0)
                ticket_layout_secs.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0 && minutes < 0)
                    ticket_layout_secs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_secs.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_secs.setVisibility(View.VISIBLE);
                ticket_layout_secs.setPadding(10, 0, 10, 0);
                ticket_textView_sec.setText(secs + "");
            }
            if (millisecs < 0)
                ticket_layout_millisecs.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0 && minutes < 0 && secs < 0)
                    ticket_layout_millisecs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_millisecs.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_millisecs.setVisibility(View.VISIBLE);
                ticket_layout_millisecs.setPadding(10, 0, 10, 0);
                ticket_textView_millisec.setText(millisecs + "");
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;

            long days = 0;
            long hours = 0;
            long minutes = 0;
            long secs = 0;
            long millisecs = 0;

            days = TimeUnit.MILLISECONDS.toDays(millis);
            hours = TimeUnit.MILLISECONDS.toHours(millis)
                    - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS
                    .toDays(millis));
            minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
                    - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                    .toHours(millis));
            secs = TimeUnit.MILLISECONDS.toSeconds(millis)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(millis));
            millisecs = (TimeUnit.MILLISECONDS.toMillis(millis)
                    - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS
                    .toSeconds(millis))) % 10;


            if (days < 0)
                ticket_layout_days.setVisibility(View.GONE);
            else {
                ticket_layout_days.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                ticket_layout_days.setVisibility(View.VISIBLE);
                ticket_textView_day.setText(days + "");
            }
            if (hours < 0)
                ticket_layout_hours.setVisibility(View.GONE);
            else {
                if (days < 0)
                    ticket_layout_hours.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_hours.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_hours.setVisibility(View.VISIBLE);
                ticket_textView_hour.setText(hours + "");
            }
            if (minutes < 0)
                ticket_layout_mins.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0)
                    ticket_layout_mins.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_mins.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_mins.setVisibility(View.VISIBLE);
                ticket_textView_min.setText(minutes + "");
            }
            if (secs < 0)
                ticket_layout_secs.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0 && minutes < 0)
                    ticket_layout_secs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_secs.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_secs.setVisibility(View.VISIBLE);
                ticket_textView_sec.setText(secs + "");
            }
            if (millisecs < 0)
                ticket_layout_millisecs.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0 && minutes < 0 && secs < 0)
                    ticket_layout_millisecs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_millisecs.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_millisecs.setVisibility(View.VISIBLE);
                ticket_textView_millisec.setText(millisecs + "");
            }
            if (days == 0 && hours == 0 && minutes == 0 && secs == 0) {
                ticket_layout_days.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_millisecs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            } else if (days == 0 && hours == 0 && minutes == 0) {
                ticket_layout_days.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_secs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            } else if (days == 0 && hours == 0) {
                ticket_layout_days.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_mins.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            } else if (days == 0) {
                ticket_layout_days.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_hours.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            } else {
                ticket_layout_days.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            }
        }

        @Override
        public void onFinish() {

        }
    }

    public String getSplitDateAndTime(String date_time) {
        String splitDateAndTime = "";
        int index = date_time.indexOf(" ");
        String date = date_time.substring(0, index);
        String time = date_time.substring(index + 1, date_time.length());

        splitDateAndTime = changeDateFormat(date.trim()) + "\n" + changeTimeFormat(time.trim());
        return splitDateAndTime;
    }

    public String changeDateFormat(String old_date) {
        String formatted_date = "";
        DateFormat writeFormat = new SimpleDateFormat("dd MMM yyyy");
        DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = readFormat.parse(old_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            formatted_date = writeFormat.format(date);
        }
        return formatted_date;
    }

    public String changeTimeFormat(String old_time) {
        String formatted_time = "";
        DateFormat writeFormat = new SimpleDateFormat("hh:mm:ss aa");
        DateFormat readFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = readFormat.parse(old_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            formatted_time = writeFormat.format(date);
        }
        return formatted_time.toUpperCase();
    }

    @Override
    public void onBackPressed() {
        if (popupWindow_image != null) {
            if (popupWindow_image.isShowing()) {
                Log.e("PopupCheck", "Not null");
                popupWindow_image.dismiss();
                popupWindow_image = null;
            } else
                super.onBackPressed();
        } else
            super.onBackPressed();
    }
}