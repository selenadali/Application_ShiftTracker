package com.example.android.myapplication;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;


import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekViewLoader;
import com.alamkanak.weekview.WeekView.EventClickListener;
import com.alamkanak.weekview.WeekView.EventLongPressListener ;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DayOfWeek;

import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PlanningActivity extends AppCompatActivity {
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
    private EditText dp;

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
        Intent intent = new Intent(PlanningActivity.this, UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
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
                    startActivity(new Intent(PlanningActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);
        Date currentTime = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.DAY_OF_WEEK, 7);
        mWeekView.goToDate(cal);

        dp = (EditText) findViewById(R.id.dp);
        /*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        String currentDateandTime = sdf.format(new Date());
        dp.setText(currentDateandTime);*/
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
                Date date_edittext = null;
                try {
                    SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
                    date_edittext = dateFormat.parse(txt);
                    cal.setTime(date_edittext);

                } catch (ParseException e) {
                    e.printStackTrace();
                    Date currentTime = Calendar.getInstance().getTime();
                    cal.setTime(currentTime);
                    cal.add(Calendar.DAY_OF_WEEK, 7);


                }
                mWeekView.goToDate(cal);
            }
        });
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(PlanningActivity.this, new DatePickerDialog.OnDateSetListener() {
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



        // Set long press listener for events.
        mWeekView.setEventLongPressListener(mEventLongPressListener);

        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {

            }
        });

        CollectionReference cr = db.collection("/Users/" + user.getEmail().toString() + "/Planning/" );
        Task<QuerySnapshot> task_count = cr.whereEqualTo("b", true).get();
        SystemClock.sleep(2000);
        int i = 0;
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
        events = new ArrayList<WeekViewEvent>();
        WeekViewEvent[] event = new WeekViewEvent[i];
        for(int j = 0; j<i;j++){
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(user_startTime.get(j));
            Calendar endTime = (Calendar) startTime.clone();
            endTime.setTime(user_endTime.get(j));
            SimpleDateFormat f= new SimpleDateFormat("HH:mm");
            String name = String.valueOf(f.format(user_startTime.get(j))+ "-" + f.format(user_endTime.get(j)));
            event[j] = new WeekViewEvent(j+1, name, startTime, endTime);
            events.add(event[j]);
        }
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


    public void onClickMonPlanning(View view) {
        Intent intent = new Intent(PlanningActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickMonEquipe(View view) {
        Intent intent = new Intent(PlanningActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void onClickMesHeures(View view) {
        Intent intent = new Intent(PlanningActivity.this, HoursActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickRuptures(View view) {
        Intent intent = new Intent(PlanningActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public static boolean isListContainsId(List<WeekViewEvent> events, long id) {
        for (int i=0; i <events.size(); i++ ) {
            for (WeekViewEvent event : events) {
                if (event.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }
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
