package com.example.android.myapplication;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnAddUser, btnChangeEmail, btnAddPlanning, btnChangePassword, btnSendResetEmail, btnRemoveUser,
            addUser, changeEmail, addPlanning, changePassword, sendEmail, remove, signOut;
    private RadioButton rb_Resp, rb_Titre_Hote,rb_Titre_Accueil ,rb_Titre_Barman, rb_Titre_Cusine , btn_titre;
    private RadioButton rb_range1, rb_range2 , btn_salaire;
    private RadioButton rb_18h, rb_24h, rb_35h , btn_contrat;
    private RadioGroup rg_selectType_titre;
    private RadioGroup rg_selectType_salaire;
    private RadioGroup rg_selectType_contrat;
    private EditText oldEmail, newEmail,userName, userMail, password, newPassword, adresse, tel, dp;
    private DatePicker dp_date_commence;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String typeEmpResp = "";
    private String userTitre = "";
    private String userContrat = "";
    private String userSalaire = "";
    private String userDatecommence = "";
    private TextView header, date_commence;
    private EditText timePicker1, timePicker2;
    private List<String> user_mails;
    private ImageView mImageView;
    private Button takePicture;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private Uri mCapturedImageURI;

    private Uri file ;
    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);



       //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {@Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }
        };
        String mail = user.getEmail();
        if(! mail.equals("admin@test.fr")){
            Intent intent = new Intent(MainActivity.this, UserMainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
        btnAddUser = (Button) findViewById(R.id.add_user_button);
        btnChangeEmail = (Button) findViewById(R.id.change_email_button);
        btnAddPlanning = (Button) findViewById(R.id.add_planning_button);
        btnChangePassword = (Button) findViewById(R.id.change_password_button);
        btnSendResetEmail = (Button) findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button) findViewById(R.id.remove_user_button);
        addUser = (Button) findViewById(R.id.addUser);
        changeEmail = (Button) findViewById(R.id.changeEmail);
        addPlanning = (Button) findViewById(R.id.addPlanning);
        final Spinner ddlUsers = findViewById(R.id.ddl_users);
        changePassword = (Button) findViewById(R.id.changePass);
        sendEmail = (Button) findViewById(R.id.send);
        remove = (Button) findViewById(R.id.remove);
        signOut = (Button) findViewById(R.id.sign_out);
        header = (TextView) findViewById(R.id.header);
        date_commence = (TextView) findViewById(R.id.date_commence);
        oldEmail = (EditText) findViewById(R.id.old_email);
        newEmail = (EditText) findViewById(R.id.new_email);
        userName = (EditText) findViewById(R.id.user_name);
        userMail = (EditText) findViewById(R.id.user_mail);
        adresse = (EditText) findViewById(R.id.user_adresse);
        tel = (EditText) findViewById(R.id.user_tel);
        dp_date_commence = (DatePicker) findViewById(R.id.dp_date_commence);
        rb_Resp = (RadioButton) findViewById(R.id.rb_resp);
        rb_Titre_Hote = (RadioButton) findViewById(R.id.rb_Titre_Hote);
        rb_Titre_Accueil = (RadioButton) findViewById(R.id.rb_Accueil);
        rb_Titre_Barman = (RadioButton) findViewById(R.id.rb_Barman);
        rb_Titre_Cusine = (RadioButton) findViewById(R.id.rb_Cusine);
        rb_18h = (RadioButton) findViewById(R.id.rb_18h);
        rb_24h = (RadioButton) findViewById(R.id.rb_24h);
        rb_35h = (RadioButton) findViewById(R.id.rb_35h);
        rb_range1 = (RadioButton) findViewById(R.id.rb_range1);
        rb_range2 = (RadioButton) findViewById(R.id.rb_range2);
        rg_selectType_titre = (RadioGroup) findViewById(R.id.selectTypeTitre);
        rg_selectType_salaire = (RadioGroup) findViewById(R.id.selectTypeSalaire);
        rg_selectType_contrat = (RadioGroup) findViewById(R.id.selectTypeContrat);
        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.newPassword);
        dp = (EditText) findViewById(R.id.dp);
        timePicker1 = (EditText) findViewById(R.id.timePicker1);
        timePicker2 = (EditText) findViewById(R.id.timePicker2);
        mImageView = (ImageView) findViewById(R.id.user_profile_photo);
        takePicture = (Button) findViewById(R.id.button_image);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePicture.setEnabled(true);
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
        }
        else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePicture.setEnabled(true);
              //  ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }


        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        adresse.setVisibility(View.GONE);
        tel.setVisibility(View.GONE);
        dp_date_commence.setVisibility(View.GONE);
        rb_Resp.setVisibility(View.GONE);
        rb_Titre_Hote.setVisibility(View.GONE);
        rb_Titre_Accueil.setVisibility(View.GONE);
        rb_Titre_Barman.setVisibility(View.GONE);
        rb_Titre_Cusine.setVisibility(View.GONE);
        rg_selectType_contrat.setVisibility(View.GONE);
        rg_selectType_salaire.setVisibility(View.GONE);
        userName.setVisibility(View.GONE);
        userMail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        ddlUsers.setVisibility(View.GONE);
        addUser.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        addPlanning.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);
        header.setVisibility(View.GONE);
        date_commence.setVisibility(View.GONE);
        dp_date_commence.setVisibility(View.GONE);
        adresse.setVisibility(View.GONE);
        tel.setVisibility(View.GONE);
        timePicker1.setVisibility(View.GONE);
        timePicker2.setVisibility(View.GONE);
        dp.setVisibility(View.GONE);
        mImageView.setVisibility(View.GONE);
        takePicture.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                userName.setVisibility(View.VISIBLE);
                userMail.setVisibility(View.VISIBLE);
                adresse.setVisibility(View.VISIBLE);
                tel.setVisibility(View.VISIBLE);
                dp_date_commence.setVisibility(View.VISIBLE);
                rb_range1.setVisibility(View.VISIBLE);
                rb_range2.setVisibility(View.VISIBLE);
                rb_18h.setVisibility(View.VISIBLE);
                rb_24h.setVisibility(View.VISIBLE);
                rb_35h.setVisibility(View.VISIBLE);
                rb_Resp.setVisibility(View.VISIBLE);
                rg_selectType_titre.setVisibility(View.VISIBLE);
                rg_selectType_contrat.setVisibility(View.VISIBLE);
                rg_selectType_salaire.setVisibility(View.VISIBLE);
                rb_Titre_Hote.setVisibility(View.VISIBLE);
                rb_Titre_Accueil.setVisibility(View.VISIBLE);
                rb_Titre_Barman.setVisibility(View.VISIBLE);
                rb_Titre_Cusine.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                ddlUsers.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                addPlanning.setVisibility(View.GONE);
                addUser.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                date_commence.setVisibility(View.VISIBLE);
                adresse.setVisibility(View.VISIBLE);
                tel.setVisibility(View.VISIBLE);
                timePicker1.setVisibility(View.GONE);
                timePicker2.setVisibility(View.GONE);
                dp.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
                takePicture.setVisibility(View.VISIBLE);
            }
        });

            //String[] UserInfo = {"1_NameEmp,1_SurnameEmp,1,7etWE35m0KAxhtt4RkUV,0",
            //                     "1_NameResp,1_SurnameResp,1,7etWE35m0KAxhtt4RkUV,1",
            //                     "1_NameAdmin,1_SurnameAdmin,
            //                      0,
            //                     "Hotesse de Table"};


        addUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                int selectedTitre = rg_selectType_titre.getCheckedRadioButtonId();
                int selectedTitreSalaire = rg_selectType_salaire.getCheckedRadioButtonId();
                int selectedTitreContrat = rg_selectType_contrat.getCheckedRadioButtonId();
                btn_titre = (RadioButton) findViewById(selectedTitre);
                btn_contrat = (RadioButton) findViewById(selectedTitreContrat);
                btn_salaire = (RadioButton) findViewById(selectedTitreSalaire);
                if (user != null && !userName.getText().toString().trim().equals("") && !userMail.getText().toString().trim().equals("") ) {
                    auth.createUserWithEmailAndPassword(userMail.getText().toString(),"password")
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "New User is added!", Toast.LENGTH_LONG).show();


                                        if(rb_Resp.isChecked()){
                                            typeEmpResp = "1";
                                            userTitre = "Responsable";
                                        }
                                        else {
                                            typeEmpResp = "0";
                                        }

                                        if(rb_Titre_Hote.isChecked()){
                                            userTitre = "Hôte/hôtesse de table";
                                        }
                                        else if(rb_Titre_Accueil.isChecked()){
                                            userTitre = "Accueil";
                                        }
                                        else if(rb_Titre_Barman.isChecked()){
                                            userTitre = "Barman";
                                        }
                                        else if(rb_Titre_Cusine.isChecked()){
                                            userTitre = "Cusine";
                                        }
                                        if(rb_range1.isChecked()){
                                            userSalaire = "Range1";
                                        }
                                        else{
                                            userSalaire = "Range2";
                                        }

                                        if(rb_18h.isChecked()){

                                            userContrat = "18h";
                                        }
                                        else if(rb_24h.isChecked()){
                                            userContrat = "24h";
                                        }
                                        else{
                                            userContrat = "35h";
                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        int month  = dp_date_commence.getMonth();
                                        int year  = dp_date_commence.getYear();
                                        int day  = dp_date_commence.getDayOfMonth();
                                        calendar.set(year, month, day);
                                        Date date = calendar.getTime();
                                        userDatecommence = date.toString();

                                        String[] UserInfo = {userName.getText().toString() + "," + userMail.getText().toString()+ "," + "1," + typeEmpResp + "," + userTitre + "," + adresse.getText() + "," + tel.getText() + "," + userSalaire + "," + userContrat + "," + userDatecommence + "," + mCapturedImageURI.toString()}; //Emp
                                        User.AddUserToDb(db, UserInfo);
                                        //signOut();
                                        progressBar.setVisibility(View.GONE);
                                        addUser.setVisibility(View.GONE);
                                        userName.setVisibility(View.GONE);
                                        rg_selectType_titre.setVisibility(View.GONE);
                                        rg_selectType_contrat.setVisibility(View.GONE);
                                        rg_selectType_salaire.setVisibility(View.GONE);
                                        userMail.setVisibility(View.GONE);
                                        header.setVisibility(View.GONE);
                                        date_commence.setVisibility(View.GONE);
                                        dp_date_commence.setVisibility(View.GONE);
                                        adresse.setVisibility(View.GONE);
                                        tel.setVisibility(View.GONE);
                                        ddlUsers.setVisibility(View.GONE);
                                        timePicker1.setVisibility(View.GONE);
                                        timePicker2.setVisibility(View.GONE);
                                        dp.setVisibility(View.GONE);
                                        takePicture.setVisibility(View.GONE);
                                        mImageView.setVisibility(View.GONE);

                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to add new user!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });



                } else if (userName.getText().toString().trim().equals("")) {
                    userName.setError("Enter user name");
                    progressBar.setVisibility(View.GONE);
                }else if (userMail.getText().toString().trim().equals("")) {
                    userMail.setError("Enter user mail");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

            btnAddPlanning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oldEmail.setVisibility(View.GONE);
                    addUser.setVisibility(View.GONE);
                    newEmail.setVisibility(View.GONE);
                    userName.setVisibility(View.GONE);
                    rg_selectType_titre.setVisibility(View.GONE);
                    userMail.setVisibility(View.GONE);
                    password.setVisibility(View.GONE);
                    newPassword.setVisibility(View.GONE);
                    changeEmail.setVisibility(View.GONE);
                    ddlUsers.setVisibility(View.VISIBLE);
                    addPlanning.setVisibility(View.VISIBLE);
                    changePassword.setVisibility(View.GONE);
                    sendEmail.setVisibility(View.GONE);
                    remove.setVisibility(View.GONE);
                    adresse.setVisibility(View.GONE);
                    tel.setVisibility(View.GONE);
                    dp_date_commence.setVisibility(View.GONE);
                    rb_range1.setVisibility(View.GONE);
                    rb_range2.setVisibility(View.GONE);
                    rb_18h.setVisibility(View.GONE);
                    rb_24h.setVisibility(View.GONE);
                    rb_35h.setVisibility(View.GONE);
                    rb_Resp.setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                    date_commence.setVisibility(View.GONE);
                    rg_selectType_contrat.setVisibility(View.GONE);
                    rg_selectType_salaire.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.GONE);
                    takePicture.setVisibility(View.GONE);


                   user_mails = new ArrayList<String>();

                    db.collection("/Users/").whereEqualTo("Type", 1).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            int k = 0;
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                k += 1;
                                String mail = doc.getString("Mail");
                                CollectionReference cr = db.collection("/Users/");
                                DocumentReference dr = cr.document(mail);
                                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        int k = 0;
                                        DocumentSnapshot document = task.getResult();
                                        user_mails.add(document.getString("Mail"));
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, user_mails);
                                        ddlUsers.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);

                                    }
                                });
                            }
                        }
                    });
                }
            });

            ddlUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String item = parent.getItemAtPosition(position).toString();
                    timePicker1.setVisibility(View.VISIBLE);
                    timePicker2.setVisibility(View.VISIBLE);

                    timePicker1.setText("00:00");
                    timePicker2.setText("00:00");

                    dp.setVisibility(View.VISIBLE);

                    dp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {

                            final Calendar calendar = Calendar.getInstance();
                            int yy = calendar.get(Calendar.YEAR);
                            int mm = calendar.get(Calendar.MONTH);
                            int dd = calendar.get(Calendar.DAY_OF_MONTH);
                            DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                    timePicker1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    timePicker1.setText("" + selectedHour + ":" + selectedMinute + "");
                                }
                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Select Time");
                            mTimePicker.show();
                        }
                    });

                    timePicker2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    timePicker2.setText("" + selectedHour + ":" + selectedMinute + "");
                                }
                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Select Time");
                            mTimePicker.show();
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            addPlanning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    int id_user = ddlUsers.getSelectedItemPosition();
                    String user_mail = user_mails.get(id_user);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date startDate = null;
                    try {
                        startDate = dateFormat.parse(dp.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String startTime = String.valueOf(timePicker1.getText());
                    String endTime = String.valueOf(timePicker2.getText());

                    if (user_mail != null && dp.getText() != null && startTime != null && endTime != null) {
                        //...
                        final int[] k = {0};
                        Task<QuerySnapshot> task = db.collection("/Users/" + user_mail.toString() + "/Planning/").get();
                        SystemClock.sleep(2000);
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Boolean b = doc.getBoolean("b");
                            if(b){
                                k[0] += 1;
                            }
                        }


                        SimpleDateFormat format_timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        String[] start_time = startTime.split(":");
                        String[] end_time = endTime.split(":");
                        int t = (Integer.parseInt(start_time[0]) * 60) + (Integer.parseInt(start_time[1]));
                        int t_end = (Integer.parseInt(end_time[0]) * 60) + (Integer.parseInt(end_time[1]));
                        Calendar calendar = Calendar.getInstance();
                        Calendar calendar_end = Calendar.getInstance();
                        calendar.setTime(startDate);
                        calendar_end.setTime(startDate);
                        calendar.add(Calendar.MINUTE, t);
                        calendar_end.add(Calendar.MINUTE, t_end);
                        Date dt_start = calendar.getTime();
                        Date dt_end = calendar_end.getTime();
                        String start = format_timestamp.format(dt_start);
                        String end = format_timestamp.format(dt_end);
                        Date dd = null;
                        Date dd_end = null;
                        try {
                            dd = format_timestamp.parse(start);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            dd_end = format_timestamp.parse(end);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String path = "/Users/" + user_mail.toString() + "/Planning/";

                        Map<String, Object> new_planning = new HashMap<>();
                        new_planning.put("b", true);
                        new_planning.put("startTime", new Date());
                        new_planning.put("endTime", new Date());

                        db.collection(path).document(String.valueOf(k[0]+1)).set(new_planning);

                        db.collection(path).document(String.valueOf(k[0]+1)).update("startTime", dd);
                        db.collection(path).document(String.valueOf(k[0]+1)).update("endTime", dd_end);
                        db.collection(path).document(String.valueOf(k[0]+1)).update("b", true);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Planning a ajouté!", Toast.LENGTH_LONG).show();
                        dp.setVisibility(View.GONE);
                        ddlUsers.setVisibility(View.GONE);
                        timePicker1.setVisibility(View.GONE);
                        timePicker2.setVisibility(View.GONE);
                        addPlanning.setVisibility(View.GONE);
                        takePicture.setVisibility(View.GONE);
                        mImageView.setVisibility(View.GONE);

                    }
                }
            });


        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                addUser.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                rg_selectType_titre.setVisibility(View.GONE);
                userMail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                addPlanning.setVisibility(View.GONE);
                ddlUsers.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
                adresse.setVisibility(View.GONE);
                tel.setVisibility(View.GONE);
                dp_date_commence.setVisibility(View.GONE);
                rb_range1.setVisibility(View.GONE);
                rb_range2.setVisibility(View.GONE);
                rb_18h.setVisibility(View.GONE);
                rb_24h.setVisibility(View.GONE);
                rb_35h.setVisibility(View.GONE);
                rb_Resp.setVisibility(View.GONE);
                header.setVisibility(View.GONE);
                date_commence.setVisibility(View.GONE);
                rg_selectType_contrat.setVisibility(View.GONE);
                rg_selectType_salaire.setVisibility(View.GONE);
                timePicker1.setVisibility(View.GONE);
                timePicker2.setVisibility(View.GONE);
                dp.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
                takePicture.setVisibility(View.GONE);

            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (user != null && !newEmail.getText().toString().trim().equals("")) {
                        user.updateEmail(newEmail.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    } else if (newEmail.getText().toString().trim().equals("")) {
                        newEmail.setError("Enter email");
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });



        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                userName.setVisibility(View.GONE);
                rg_selectType_titre.setVisibility(View.GONE);
                userMail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                addPlanning.setVisibility(View.GONE);
                ddlUsers.setVisibility(View.GONE);
                addUser.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
                adresse.setVisibility(View.GONE);
                tel.setVisibility(View.GONE);
                dp_date_commence.setVisibility(View.GONE);
                rb_range1.setVisibility(View.GONE);
                rb_range2.setVisibility(View.GONE);
                rb_18h.setVisibility(View.GONE);
                rb_24h.setVisibility(View.GONE);
                rb_35h.setVisibility(View.GONE);
                rb_Resp.setVisibility(View.GONE);
                rg_selectType_contrat.setVisibility(View.GONE);
                rg_selectType_salaire.setVisibility(View.GONE);
                header.setVisibility(View.GONE);
                date_commence.setVisibility(View.GONE);
                timePicker1.setVisibility(View.GONE);
                timePicker2.setVisibility(View.GONE);
                dp.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
                takePicture.setVisibility(View.GONE);

            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                userName.setVisibility(View.GONE);
                rg_selectType_titre.setVisibility(View.GONE);
                userMail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                addPlanning.setVisibility(View.GONE);
                ddlUsers.setVisibility(View.GONE);
                addUser.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
                adresse.setVisibility(View.GONE);
                tel.setVisibility(View.GONE);
                dp_date_commence.setVisibility(View.GONE);
                header.setVisibility(View.GONE);
                date_commence.setVisibility(View.GONE);
                rb_range1.setVisibility(View.GONE);
                rb_range2.setVisibility(View.GONE);
                rb_18h.setVisibility(View.GONE);
                rb_24h.setVisibility(View.GONE);
                rb_35h.setVisibility(View.GONE);
                rb_Resp.setVisibility(View.GONE);
                rg_selectType_contrat.setVisibility(View.GONE);
                rg_selectType_salaire.setVisibility(View.GONE);
                timePicker1.setVisibility(View.GONE);
                timePicker2.setVisibility(View.GONE);
                dp.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
                takePicture.setVisibility(View.GONE);

            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, SignupActivity.class));
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==  REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                mImageView.setImageURI(file);
                mImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
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

    public void onclicktakePicture(View view) {
        mImageView.setVisibility(View.VISIBLE);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.android.fileprovider",
                     photoFile);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Image File name");
                mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                startActivityForResult(intentPicture,REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePicture.setEnabled(true);
            }
        }
    }

}
