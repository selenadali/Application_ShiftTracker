package com.example.android.myapplication;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static java.security.AccessController.getContext;

public class UserProfileActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    public TextView textView_name, textView_titre;
    public ScrollView scroll;
    Handler handler = new Handler();
    private ImageView user_profile_photo;

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
        Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        user_profile_photo = (ImageView) findViewById(R.id.user_profil_photo);
        user_profile_photo.setClipToOutline(true);
        progressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
                android.graphics.PorterDuff.Mode.MULTIPLY);
        scroll = (ScrollView) findViewById(R.id.scroll);

        progressBar.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.GONE);
        user_profile_photo.setVisibility(View.GONE);

        db.collection("/Users/").document(user.getEmail().toString()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        String name = document.getString("Name");
                        String titre = document.getString("Titre");
                        String user_photo = document.getString("photo");
                        textView_name = (TextView) findViewById(R.id.user_profile_name);
                        textView_titre = (TextView) findViewById(R.id.user_profile_titre);
                        textView_name.setText(name);
                        textView_titre.setText(titre);
                        progressBar.setVisibility(View.GONE);
                        scroll.setVisibility(View.VISIBLE);
                        String s = UserProfileActivity.this.getPackageName();
//                        int photo_id = getResources().getIdentifier(photo, "drawable", "com.example.android.myapplication");
                        user_profile_photo.setImageURI(Uri.parse(user_photo));
                        user_profile_photo.setVisibility(View.VISIBLE);

                    }
                });


        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle(getString(R.string.app_name)); // set the top title
        String title = actionBar.getTitle().toString(); // get the title
        //actionBar.hide(); // or even hide the actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        authListener = new FirebaseAuth.AuthStateListener() {@Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
                finish();
            }
        }
        };


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
        Intent intent = new Intent(UserProfileActivity.this, PlanningActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickMonEquipe(View view) {
        Intent intent = new Intent(UserProfileActivity.this, Team.class);
        startActivity(intent);
        finish();
    }


    public void onClickMesHeures(View view) {
        Intent intent = new Intent(UserProfileActivity.this, HoursActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickRuptures(View view) {
        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
