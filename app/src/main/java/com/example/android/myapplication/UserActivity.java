package com.example.android.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserActivity extends AppCompatActivity {
    FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

  //  final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

  //  protected void onCreate(Bundle savedInstanceState) {
      //  super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_signup);
    //    setContentView(R.layout.activity_main);

//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

       // db = FirebaseFirestore.getInstance();
       // String[] UserInfo = {"1_NameEmp,1_SurnameEmp,1,7etWE35m0KAxhtt4RkUV,0",
       //         "1_NameResp,1_SurnameResp,1,7etWE35m0KAxhtt4RkUV,1",
       //         "1_NameAdmin,1_SurnameAdmin,0"};
      //  User.AddUserToDb(db, UserInfo);
 //   }


}
