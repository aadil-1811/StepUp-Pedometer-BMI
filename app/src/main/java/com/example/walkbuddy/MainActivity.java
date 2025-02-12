package com.example.walkbuddy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    MeowBottomNavigation main_mbn;
    public static String toOpen = "none";
    private StorageReference mStorage;
    private FirebaseAuth mfirebaseAuth;
    private FirebaseUser mfirebaseUser;
    Uri picUri;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.statusBarPurple));
        main_mbn = findViewById(R.id.main_bottom_nav);
        mfirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        setCurrentDate();
        //add menu items
        main_mbn.add(new MeowBottomNavigation.Model(1, R.drawable.main_stats));
        main_mbn.add(new MeowBottomNavigation.Model(2, R.drawable.main_home));
        main_mbn.add(new MeowBottomNavigation.Model(3, R.drawable.main_prof));

        main_mbn.setOnShowListener(item -> {
            //Initialize Fragment
            Fragment frag = null;
            switch (item.getId()){
                case 1:
                    //initialize bmi frag
                    frag = new StatsFragment();
                    break;
                case 2:
                    //initialize home frag
                    frag = new HomeFragment();
                    break;
                case 3:
                    //initialize profile frag
                    frag = new ProfFragment();
                    break;
            }
            //load fragment
            loadFragment(frag);
        });

        main_mbn.setOnClickMenuListener(item -> { });
        main_mbn.setOnReselectListener(item -> { });
        // set default frag to open
        if(toOpen.equals("profile")){
            main_mbn.show(3, true);
            toOpen = "none";
        } else if(toOpen.equals("stat")){
            main_mbn.show(1, true);
            toOpen = "none";
        } else {
            main_mbn.show(2, true);
        }

    }

    private void loadFragment(Fragment frag) {
        //Replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fl,frag)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 9){
            if(resultCode == Activity.RESULT_OK){
            picUri = data.getData();
            String email = mfirebaseUser.getEmail();
            String id = Utility.EncodeString(Objects.requireNonNull(email));
            // Use Uri object instead of File to avoid storage permissions
            StorageReference filepath = mStorage.child(id);
            filepath.putFile(picUri).addOnSuccessListener(taskSnapshot -> {
                loadFragment(new ProfFragment());
            });
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getApplicationContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
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
            if(!snapshot.hasChild(date)){
                ref3.child(date).child("s").setValue(0);
                ref3.child(date).child("c").setValue(0);
                ref3.child(date).child("d").setValue(0);
            }
        });
    }
}