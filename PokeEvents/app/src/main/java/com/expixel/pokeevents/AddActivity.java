package com.expixel.pokeevents;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by cellbody on 2016/9/21.
 */

public class AddActivity extends Activity {
    protected static final String TAG = "PokeEvents";
    TextView tvDate;

    TextView tvTime;
    DatePickerDialog datePickDialog;
    RangeTimePickerDialog rangeTimePickerDialog;
    private DatabaseReference dbRef;

    private FirebaseAuth auth;

    GregorianCalendar calendarNaw;
    GregorianCalendar calendar;

    String eventType;
    String date;
    String time;
    Long dateTime;
    EditText etPlace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ac_add);

        dbRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        tvDate = (TextView) findViewById(R.id.date);
        tvTime = (TextView) findViewById(R.id.time);
        etPlace = (EditText) findViewById(R.id.location);

        RadioGroup radgroup = (RadioGroup) findViewById(R.id.eventTypeRdGrp);
        radgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.btnBattle:
                        eventType = "1";
                        break;
                    case R.id.btnCapture:
                        eventType = "2";
                        break;
                }
            }
        });


        calendarNaw = new GregorianCalendar();
        calendar = new GregorianCalendar();


        date = calendar.get(Calendar.YEAR)
                + "/" + (calendar.get(Calendar.MONTH) + 1)  //  from 0
                + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        tvDate.setText(date);

        time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        tvTime.setText(time);

        datePickDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                tvDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                date = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                calendar.set(year, monthOfYear, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickDialog.getDatePicker().setMinDate(calendarNaw.getTimeInMillis());

        rangeTimePickerDialog = new RangeTimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hoursOfDay, int minute) {
                tvTime.setText(hoursOfDay + ":" + minute);
                time = hoursOfDay + ":" + minute;
                calendar.set(Calendar.HOUR_OF_DAY, hoursOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        rangeTimePickerDialog.setMin(calendarNaw.get(Calendar.HOUR_OF_DAY), calendarNaw.get(Calendar.MINUTE));




    }


    public void onDateSettingClick(View view) {
        Log.i(TAG, "AddActivity: onDateSettingClick: ");
        datePickDialog.show();
    }

    public void onTimeSettingClick(View view) {
        rangeTimePickerDialog.show();
    }

    public void onAddClick(View view) {
        String place = etPlace.getText().toString();
        if (eventType==null) {
            Toast.makeText(this, "Battle or Capture ?", Toast.LENGTH_SHORT).show();
        }
//        else if (calendar.getTimeInMillis() < calendarNaw.getTimeInMillis()){
//            Toast.makeText(this, "時間不能")
//        }
        else if(place.equals("")) {
            Toast.makeText(this, "Where ?",Toast.LENGTH_SHORT).show();
        }else {
            String imgUrl = auth.getCurrentUser().getPhotoUrl().toString();
            Event event = new Event(imgUrl,"",date+" "+time,calendar.getTimeInMillis(), eventType, place, 1);
            String key = dbRef.child("events").push().getKey();
            dbRef.child("events").child(key).setValue(event);
            Toast.makeText(this, "Go !", Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }
}
