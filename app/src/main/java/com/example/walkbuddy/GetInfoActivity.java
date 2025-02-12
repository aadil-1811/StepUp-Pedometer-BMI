package com.example.walkbuddy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;

public class GetInfoActivity extends AppCompatActivity
        implements DialogHeightFragment.OnInputListener,
        DialogWeightFragment.OnInputListener,
        DialogSGoalFragment.OnInputListener {
    private MaterialButton btn_date, btn_height, btn_weight, btn_goal, sub;
    private int mYear, mMonth, mDay;
    private MaterialButtonToggleGroup tg;
    private String gender = "null", age = "null", height = "null", weight = "null", goal = "null", dob = "null";
    private FirebaseAuth mfirebaseAuth;
    private FirebaseUser mfirebaseUser;
    private StorageReference mStorage;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getinfo);
        getWindow().setStatusBarColor(ContextCompat.getColor(GetInfoActivity.this, R.color.statusBarPurple));

        tg = findViewById(R.id.register_toggleGroup);
        btn_date = findViewById(R.id.btn_date);
        btn_height = findViewById(R.id.btn_height);
        btn_weight = findViewById(R.id.btn_weight);
        btn_goal = findViewById(R.id.btn_goal);
        sub = findViewById(R.id.sub);
        mfirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        tg.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                switch (checkedId) {
                    case R.id.male:
                        gender = "Male";
                        break;
                    case R.id.female:
                        gender = "Female";
                        break;
                }
            } else {
                if (tg.getCheckedButtonId() == View.NO_ID) {
                    gender = "null";
                }
            }
        });

        btn_date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(GetInfoActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        btn_date.setText(String.format("Date of Birth - %s-%s-%s", Utility.zeroX(dayOfMonth), Utility.zeroX((monthOfYear + 1)), Utility.zeroX(year)));
                        dob = Utility.zeroX(dayOfMonth) + "-" + Utility.zeroX((monthOfYear + 1)) + "-" + Utility.zeroX(year);
                        age = getAge(year, monthOfYear, dayOfMonth);
                        btn_date.setTextColor(Color.parseColor("#730948"));
                        btn_date.setStrokeColor(AppCompatResources.getColorStateList(GetInfoActivity.this, R.color.statusBarPurple));
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        btn_height.setOnClickListener(v -> {
            DialogHeightFragment dhFragment = new DialogHeightFragment(false);
            dhFragment.show(getSupportFragmentManager(), "dhFragment");
        });

        btn_weight.setOnClickListener(v -> {
            DialogWeightFragment dwFragment = new DialogWeightFragment(false);
            dwFragment.show(getSupportFragmentManager(), "dwFragment");
        });

        btn_goal.setOnClickListener(v -> {
            DialogSGoalFragment dsgFragment = new DialogSGoalFragment(false);
            dsgFragment.show(getSupportFragmentManager(), "dsgFragment");
        });

        sub.setOnClickListener(v -> {
            if (!gender.equals("null") && !age.equals("null") && !height.equals("null") && !weight.equals("null") && !goal.equals("null"))
                addData();
            else
                Toast.makeText(getApplicationContext(), "Please fill all the required details!!", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void setHeight(String input) {
        btn_height.setText(String.format("Height - %s cms", input));
        height = input;
        btn_height.setTextColor(Color.parseColor("#730948"));
        btn_height.setStrokeColor(AppCompatResources.getColorStateList(GetInfoActivity.this, R.color.statusBarPurple));
    }

    @Override
    public void setWeight(String input) {
        btn_weight.setText(String.format("Weight - %s kgs", input));
        weight = input;
        btn_weight.setTextColor(Color.parseColor("#730948"));
        btn_weight.setStrokeColor(AppCompatResources.getColorStateList(GetInfoActivity.this, R.color.statusBarPurple));
    }

    @Override
    public void setStepGoal(String input) {
        btn_goal.setText(String.format("Step Goal - %s steps", input));
        goal = input;
        btn_goal.setTextColor(Color.parseColor("#730948"));
        btn_goal.setStrokeColor(AppCompatResources.getColorStateList(GetInfoActivity.this, R.color.statusBarPurple));
    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
            age--;
        Integer ageInt = age;
        String ageS = ageInt.toString();
        return ageS;
    }

    public void addData() {
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(str);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("info");
        ref.child("gender").setValue(gender);
        ref.child("height").setValue(height);
        ref.child("weight").setValue(weight);
        ref.child("goal").setValue(goal);
        ref.child("age").setValue(age);
        ref.child("dob").setValue(dob);
        setCurrentDate();
        StorageReference filepath = mStorage.child(id);
        Uri picUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.prof_pic);
        filepath.putFile(picUri).addOnSuccessListener(taskSnapshot -> {
            startActivity(new Intent(GetInfoActivity.this, MainActivity.class));
            finish();
        });
    }

    private void setCurrentDate() {
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(str);

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        date = Utility.zeroX(day) + "," + Utility.zeroX((month + 1)) + "," + Utility.zeroX(year);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("info");
        ref.child("init").setValue(date);
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child(id).child("steps");
        ref3.get().addOnSuccessListener(snapshot -> {
            ref3.child(date).child("s").setValue(0);
            ref3.child(date).child("c").setValue(0);
            ref3.child(date).child("d").setValue(0);
        });
    }
}