package com.example.android.myapplication;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Team extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    public TextView textView_name, textView_titre;
    Handler handler = new Handler();
    private ArrayList<String> listviewTitle = new ArrayList<>();
    private ArrayList<Integer> listviewImage  = new ArrayList<>();
    private ArrayList<String> listviewShortDescription = new ArrayList<>();

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
        Intent intent = new Intent(Team.this, UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        //get firebase auic_launcherth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
                PorterDuff.Mode.MULTIPLY);

        progressBar.setVisibility(View.VISIBLE);

        CollectionReference cr = db.collection("/Users/");
        Task<QuerySnapshot> task_count = cr.whereEqualTo("Type", 1).get();
        SystemClock.sleep(2000);
        int k = 0;
        for(QueryDocumentSnapshot doc : task_count.getResult()) {
            k += 1;
            String mail = doc.getString("Mail");
            DocumentReference dr = cr.document(mail);
            Task<DocumentSnapshot> task = dr.get();
            SystemClock.sleep(2000);
            DocumentSnapshot document = task.getResult();
            listviewTitle.add(document.getString("Name"));
            listviewShortDescription.add(document.getString("Titre"));
            String user_photo = document.getString("Photo");
            String photo = "profileimage" + user_photo;
            int id_photo =  getResources().getIdentifier(photo,"drawable", "com.example.android.myapplication");
            listviewImage.add(id_photo);
            progressBar.setVisibility(View.GONE);

        }

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
                    startActivity(new Intent(Team.this, LoginActivity.class));
                    finish();
                }
            }
        };

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < listviewTitle.size(); i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listviewTitle.get(i));
            hm.put("listview_discription", listviewShortDescription.get(i));
            hm.put("listview_image", Integer.toString(listviewImage.get(i)));
            aList.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
        ListView androidListView = (ListView) findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);


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

}
