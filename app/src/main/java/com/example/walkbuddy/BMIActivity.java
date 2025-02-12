package com.example.walkbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.ms.square.android.expandabletextview.ExpandableTextView;

public class BMIActivity extends AppCompatActivity {

    private ImageButton back;
    private MaterialButtonToggleGroup tg;
    private SeekBar seek_height, seek_weight;
    private TextView txt_height, txt_weight;
    private Button calculate;
    String gender;
    Float height;
    Float weight;
    double BMI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        getWindow().setStatusBarColor(ContextCompat.getColor(BMIActivity.this, R.color.statusBarPurple));

        back = findViewById(R.id.back);
        tg = findViewById(R.id.register_toggleGroup);
        seek_height = findViewById(R.id.SeekbarH);
        seek_weight = findViewById(R.id.SeekbarW);
        txt_height = findViewById(R.id.height_txt);
        txt_weight = findViewById(R.id.weight_txt);
        calculate = findViewById(R.id.calculate);
        ExpandableTextView expTv = (ExpandableTextView) findViewById(R.id.bmi_info).findViewById(R.id.bmi_info);

        // calling setText on the ExpandableTextView so that
        // text content will be  displayed to the user
        expTv.setText(getString(R.string.bmi_info));

        seek_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt_height.setText(progress + " cm");
                height = (float) progress / 100;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seek_weight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt_weight.setText(progress + " kg");
                weight = (float) progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

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

        back.setOnClickListener(v -> {
            startActivity(new Intent(BMIActivity.this, MainActivity.class));
            finish();
        });

        calculate.setOnClickListener(v -> {
            if (gender!=null && weight != null && height != null){
                BMI = weight / (height * height);
                BMI = Math.round(BMI * 100.0) / 100.0;
                BMIResultDialog bmiResultDialog = new BMIResultDialog(BMIActivity.this, BMI);
                bmiResultDialog.setCancelable(true);
                bmiResultDialog.show();
                Window window = bmiResultDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });
    }
}
