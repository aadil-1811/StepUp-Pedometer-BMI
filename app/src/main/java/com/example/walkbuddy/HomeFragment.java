package com.example.walkbuddy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

public class HomeFragment extends Fragment implements DialogSGoalFragment.OnInputListener {

    TextView home_tv1, home_tv2, home_tv3, home_tv_dis, home_tv_cal, step_goal, bmi_calc;
    ProgressBar home_prog;
    ImageView home_iv, home_iv1;
    ImageButton edit_goal;
    FirebaseAuth mfirebaseAuth;
    FirebaseUser mfirebaseUser;
    HalfGauge halfGauge;
    private String date, cal, dis, cat;
    private int steps, weight;
    Double height;
    private int goal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mfirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        home_tv1 = v.findViewById(R.id.home_tv_prog);
        home_tv2 = v.findViewById(R.id.home_user);
        home_tv3 = v.findViewById(R.id.bmi_indicator);
        home_prog = v.findViewById(R.id.home_circprog);
        home_iv = v.findViewById(R.id.home_star);
        home_iv1 = v.findViewById(R.id.home_star1);
        halfGauge = v.findViewById(R.id.halfGauge);
        home_tv_dis = v.findViewById(R.id.home_tv_distance);
        home_tv_cal = v.findViewById(R.id.home_tv_cal);
        step_goal = v.findViewById(R.id.goalsteps);
        edit_goal = v.findViewById(R.id.edit_goal);
        bmi_calc = v.findViewById(R.id.bmi_calc);

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        date = Utility.zeroX(day) + "," + Utility.zeroX((month + 1)) + "," + Utility.zeroX(year);

        bmi_calc.setOnClickListener(v1 -> startActivity(new Intent(getContext(), BMIActivity.class)));

        Range r1 = new Range();
        r1.setColor(Color.parseColor("#878A88"));
        r1.setFrom(10.0);
        r1.setTo(15.0);
        Range r2 = new Range();
        r2.setColor(Color.parseColor("#ADB2AF"));
        r2.setFrom(15.0);
        r2.setTo(18.5);
        Range r3 = new Range();
        r3.setColor(Color.parseColor("#0dd415"));
        r3.setFrom(18.5);
        r3.setTo(25);
        Range r4 = new Range();
        r4.setColor(Color.parseColor("#FFBA00"));
        r4.setFrom(25);
        r4.setTo(30.0);
        Range r5 = new Range();
        r5.setColor(Color.parseColor("#FF310F"));
        r5.setFrom(30);
        r5.setTo(40.0);
        halfGauge.setMinValue(10.0);
        halfGauge.setMaxValue(40.0);
        halfGauge.addRange(r1);
        halfGauge.addRange(r2);
        halfGauge.addRange(r3);
        halfGauge.addRange(r4);
        halfGauge.addRange(r5);

        edit_goal.setOnClickListener(v12 -> {
            DialogSGoalFragment dsFragment = new DialogSGoalFragment(true);
            dsFragment.setTargetFragment(HomeFragment.this, 132);
            dsFragment.show(getActivity().getSupportFragmentManager(), "dsFragment");
        });

        updateUI();
        ContextCompat.startForegroundService(getActivity().getApplicationContext(), new Intent(getActivity().getApplicationContext(), StepsService.class));
//        getActivity().startService(new Intent(getActivity().getApplicationContext(), StepsService.class));

        v.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeBottom() {
                Toast.makeText(getContext(), "Updating steps...", Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {

        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(Objects.requireNonNull(str));
        DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference().child(id).child("info");
        ref0.get().addOnSuccessListener(snapshot -> {
            weight = Integer.parseInt(snapshot.child("weight").getValue().toString());
            height = Double.parseDouble(snapshot.child("height").getValue().toString());
            goal = Integer.parseInt(snapshot.child("goal").getValue().toString());
            calculateBMI();
            home_prog.setMax(goal);
            String name = snapshot.child("name").getValue().toString();
            home_tv2.setText("Hi " + name + " !");
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("steps");

        ref.get().addOnSuccessListener(dataSnapshot -> {
            if (!dataSnapshot.hasChild(date)) {
                ref.child(date).child("s").setValue(0);
                ref.child(date).child("c").setValue(0);
                ref.child(date).child("d").setValue(0);
                updateUI();
            } else {
                steps = Integer.parseInt(dataSnapshot.child(date).child("s").getValue().toString());
                home_prog.setProgress(steps);
                home_tv1.setText(steps + "\nsteps");
                if (steps >= home_prog.getMax()) {
                    home_iv.setVisibility(View.INVISIBLE);
                    home_iv1.setVisibility(View.VISIBLE);
                    step_goal.setText("\uD83D\uDD25 Today's Goal reached \uD83D\uDD25");
                } else {
                    home_iv1.setVisibility(View.INVISIBLE);
                    home_iv.setVisibility(View.VISIBLE);
                    int req = home_prog.getMax() - steps;
                    step_goal.setText(req + " steps required to reach today's goal");
                }

                cal = dataSnapshot.child(date).child("c").getValue().toString();
                dis = dataSnapshot.child(date).child("d").getValue().toString();
                home_tv_dis.setText(dis + " km");
                home_tv_cal.setText(cal + " cal");
            }
        });
    }

    private void calculateBMI() {
        double h = height / 100;
        double bmi = weight / (h * h);
        double bmi_rounded = Math.round(bmi * 100.0) / 100.0;
        halfGauge.setValue(bmi_rounded);
        cat = getCategory((float) bmi);
        home_tv3.setText("Your BMI is\n" + bmi_rounded + "\n" + cat);
    }

    public String getCategory(float result) {
        String category;
        if (result < 15) {
            category = "(Severely underweight)";
        } else if (result >= 15 && result <= 18.5) {
            category = "(Underweight)";
        } else if (result > 18.5 && result <= 25) {
            category = "(Normal)";
        } else if (result > 25 && result <= 30) {
            category = "(Overweight)";
        } else if (result > 30 && result <= 40) {
            category = "(Obese)";
        } else {
            category = "(Severely obese)";
        }
        return category;
    }

    @Override
    public void setStepGoal(String input) {
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(str);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("info");
        ref.child("goal").setValue(input);
        updateUI();
    }

}