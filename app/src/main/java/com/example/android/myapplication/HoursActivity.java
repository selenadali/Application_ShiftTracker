package com.example.android.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekView.EventClickListener;
import com.alamkanak.weekview.WeekView.EventLongPressListener;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HoursActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    //public ScrollView scroll;
    //Handler handler = new Handler();
    private WeekView mWeekView;
    private MonthLoader.MonthChangeListener mMonthChangeListener;
    private EventClickListener mEventClickListener;
    private EventLongPressListener mEventLongPressListener;
    private List<WeekViewEvent> events;
    private List<Date> user_startTime = new ArrayList<>();
    private List<Date> user_endTime = new ArrayList<>();
    private EditText dp, dp2;
    private Boolean isdp2;
    private Date date_edittext = null;
    private Date date_edittext2 = null;
    private SimpleDateFormat dateFormat;
    private String txt;
    private String txt2;
    private Date currentTime;
    private TextView txtTotal;
    private int i;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void onDeconnection(MenuItem mi) {
        auth.signOut();
    }
    public void onProfileAction(MenuItem mi) {
        Intent intent = new Intent(HoursActivity.this, UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hours);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        progressBar.getIndeterminateDrawable().setColorFilter(0xffff0000,
                android.graphics.PorterDuff.Mode.MULTIPLY);
        //scroll = (ScrollView) findViewById(R.id.scroll);

        progressBar.setVisibility(View.VISIBLE);
        //scroll.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle(getString(R.string.app_name)); // set the top title
        String title = actionBar.getTitle().toString(); // get the title
        //actionBar.hide(); // or even hide the actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(HoursActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);
        currentTime = Calendar.getInstance().getTime();
        Calendar calx = Calendar.getInstance();
        calx.add(Calendar.YEAR, -1);
        date_edittext = calx.getTime();
        date_edittext2 = currentTime;
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.DAY_OF_WEEK, 7);
        mWeekView.goToDate(cal);

        dp = (EditText) findViewById(R.id.dp);
        dp2 = (EditText) findViewById(R.id.dp2);
        dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        /*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        String currentDateandTime = sdf.format(new Date());
        dp.setText(currentDateandTime);*/
        isdp2 =false;
        dp.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Calendar cal = Calendar.getInstance();
                String txt = dp.getText().toString();
                date_edittext = null;
                try {

                    date_edittext = dateFormat.parse(txt);
                    cal.setTime(date_edittext);

                } catch (ParseException e) {
                    e.printStackTrace();
                    Date currentTime = Calendar.getInstance().getTime();
                    cal.setTime(currentTime);
                    cal.add(Calendar.DAY_OF_WEEK, 7);


                }
                isdp2 = false;
                mWeekView.goToDate(cal);
            }
        });
        dp2.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dateFormat= new SimpleDateFormat("dd/MM/yyyy");
                Calendar cal = Calendar.getInstance();
                txt = dp.getText().toString();
                txt2 = dp2.getText().toString();

                try {
                    date_edittext = dateFormat.parse(txt);
                    date_edittext2 = dateFormat.parse(txt2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //cal.setTime(date_edittext2);
                //long difference = Math.abs(date_edittext.getTime() - date_edittext2.getTime());
                //long differenceDates = difference / (60 * 60 * 1000);
                call(i);

            }
        });

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(HoursActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1)
                                + "/" + String.valueOf(year);
                        dp.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();

            }
        });

        dp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(HoursActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1)
                                + "/" + String.valueOf(year);
                        dp2.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();

            }
        });


        // Set long press listener for events.
        mWeekView.setEventLongPressListener(mEventLongPressListener);

        mWeekView.setOnEventClickListener(new EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {

            }
        });

        CollectionReference cr = db.collection("/Users/" + user.getEmail().toString() + "/Planning/" );
        Task<QuerySnapshot> task_count = cr.whereGreaterThanOrEqualTo("startTime", date_edittext  ).get();
        SystemClock.sleep(2000);
        i = 0;
        for(QueryDocumentSnapshot doc : task_count.getResult()){
            i += 1;
            DocumentReference dr = cr.document(String.valueOf(i));
            Task<DocumentSnapshot> task = dr.get();
            SystemClock.sleep(2000);
            progressBar.setVisibility(View.GONE);
            DocumentSnapshot document = task.getResult();

            user_startTime.add(document.getDate("startTime"));
            user_endTime.add(document.getDate("endTime"));
        }
        call(i);
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {

            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
                for (WeekViewEvent event : events) {
                    if (eventMatches(event, newYear, newMonth)) {
                        matchedEvents.add(event);
                    }
                }
                return matchedEvents;
            }
        });

    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public void call(int i){
        events = new ArrayList<WeekViewEvent>();
        float total_heures = (float) 0;
        WeekViewEvent[] event = new WeekViewEvent[i];
        for(int j = 0; j<i;j++) {
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(user_startTime.get(j));
            Calendar endTime = (Calendar) startTime.clone();
            endTime.setTime(user_endTime.get(j));
            float difference = Math.abs(user_startTime.get(j).getTime() - user_endTime.get(j).getTime());
            float differenceDates = difference / (60 * 1000);
            SimpleDateFormat f = new SimpleDateFormat("HH:mm");
            String name = String.valueOf(f.format(user_startTime.get(j)) + "-" + f.format(user_endTime.get(j)) + " " + String.valueOf((float) (differenceDates / 60.0)) + "heures");
            event[j] = new WeekViewEvent(j + 1, name, startTime, endTime);
            if (user_startTime.get(j).getTime() < date_edittext2.getTime()) {
                difference = Math.abs(user_startTime.get(j).getTime() - user_endTime.get(j).getTime());
                differenceDates = difference / (float) (60 * 1000);
                total_heures += differenceDates;
            }


            String[] dif = String.valueOf((float) total_heures / 60.0).split("\\.");
            if(dif.length > 0){
                Double dif_min = 60.0 * Double.parseDouble("0." + dif[1].toString());
                String[] dif_m = String.valueOf(dif_min).split("\\.");

            if (Double.parseDouble(dif_m[0]) < 60) {
                txtTotal.setText(dif[0] + " heures " + dif_m[0].toString() + " minutes");
            } else {
                int dif_h = Integer.parseInt(dif_m[0])/60;
                int d_m = Integer.parseInt(dif_m[0]) - dif_h;
                txtTotal.setText(String.valueOf(dif_h) + " heures " + String.valueOf(d_m) + " minutes");
            }

            }


            events.add(event[j]);
        }
    }

    public void onClickMonPlanning(View view) {
        Intent intent = new Intent(HoursActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickMonEquipe(View view) {
        Intent intent = new Intent(HoursActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void onClickMesHeures(View view) {
        Intent intent = new Intent(HoursActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickRuptures(View view) {
        Intent intent = new Intent(HoursActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    /*public static boolean isListContainsId(List<WeekViewEvent> events, long id) {
        for (int i=0; i <events.size(); i++ ) {
            for (WeekViewEvent event : events) {
                if (event.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }*/
    /**
     * Checks if an event falls into a specific year and month.
     * @param event The event to check for.
     * @param year The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month-1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

}
