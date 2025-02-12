package com.example.walkbuddy;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;

public class BMIResultDialog extends Dialog {

    Float bmi;
    TextView result, txt_cat;
    HalfGauge halfGauge;

    public BMIResultDialog(@NonNull Context context, double bmi) {
        super(context);
        this.bmi = (float) bmi;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bmi_result);
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
        result = findViewById(R.id.result);
        result.setText(""+bmi);
        halfGauge = findViewById(R.id.halfGauge);
        txt_cat = findViewById(R.id.cat);

        setGauge();
    }

    public void setGauge(){
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
        double bmi_rounded = Math.round(bmi * 100.0) / 100.0;
        halfGauge.setValue(bmi_rounded);
        setCategory(bmi);

    }

    public void setCategory (float result) {
        String category;
        if (result < 15) {
            category = "Severely underweight";
            txt_cat.setText(category);
            txt_cat.setTextColor(Color.parseColor("#878A88"));
        } else if (result >=15 && result <= 18.5) {
            category = "Underweight";
            txt_cat.setText(category);
            txt_cat.setTextColor(Color.parseColor("#ADB2AF"));
        } else if (result >18.5 && result <= 25) {
            category = "Normal (healthy weight)";
            txt_cat.setText(category);
            txt_cat.setTextColor(Color.parseColor("#0dd415"));
        } else if (result >25 && result <= 30) {
            category = "Overweight";
            txt_cat.setText(category);
            txt_cat.setTextColor(Color.parseColor("#FFBA00"));
        } else if (result >30 && result <= 40) {
            category = "Obese";
            txt_cat.setText(category);
            txt_cat.setTextColor(Color.parseColor("#FF310F"));
        } else {
            category ="Severely obese";
            txt_cat.setText(category);
            txt_cat.setTextColor(Color.parseColor("#FF310F"));
        }
    }
}
