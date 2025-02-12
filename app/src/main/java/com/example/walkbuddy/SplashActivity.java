package com.example.walkbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    TextView tv1, tv2;
    FirebaseAuth mfirebaseAuth;
    FirebaseUser mfirebaseUser = null;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(ContextCompat.getColor(SplashActivity.this, R.color.statusBarPurple));
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        mfirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mfirebaseAuth.getCurrentUser();


        Animation anim_tv1 = AnimationUtils.loadAnimation(this, R.anim.splash_tv1);
        Animation anim_tv2 = AnimationUtils.loadAnimation(this, R.anim.splash_tv2);
        tv1.setAnimation(anim_tv1);
        tv2.setAnimation(anim_tv2);

        Thread td = new Thread() {
            public void run() {
                try {
                    sleep(4300);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mfirebaseUser == null) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        String str = mfirebaseUser.getEmail();
                        String id = Utility.EncodeString(str);
                        setCurrentDate(); // create today's date entry in DB
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("info");
                        ref.get().addOnSuccessListener(dataSnapshot -> {
                            if (dataSnapshot.child("gender").getValue().equals("null")) {
                                startActivity(new Intent(SplashActivity.this, GetInfoActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    }
                }
            }
        };
        td.start();
    }

    private void setCurrentDate() {
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(str);

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        date = Utility.zeroX(day) + "," + Utility.zeroX((month + 1)) + "," + Utility.zeroX(year);
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child(id).child("steps");
        ref3.get().addOnSuccessListener(snapshot -> {
            if(!snapshot.hasChild(date)) {
                ref3.child(date).child("s").setValue(0);
                ref3.child(date).child("c").setValue(0);
                ref3.child(date).child("d").setValue(0);
            }
        });
    }
}