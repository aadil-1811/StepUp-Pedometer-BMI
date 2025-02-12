package com.example.walkbuddy;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private FloatingActionButton regi_fab;
    private TextInputLayout regi_t1, regi_t2, regi_t4, regi_t5, regi_t6;
    private TextInputEditText regi_ed1, regi_ed2, regi_ed4, regi_ed5, regi_ed6;
    private Button regi_b1;
    private ConstraintLayout regi_cl1;
    InputMethodManager imm;

    String name,username,mno,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setStatusBarColor(ContextCompat.getColor(RegisterActivity.this,R.color.statusBarPurple));

        regi_cl1 = findViewById(R.id.regi_cl1);
        regi_fab = findViewById(R.id.regi_fab);
        regi_t1 = findViewById(R.id.regi_t1);
        regi_t2 = findViewById(R.id.regi_t2);
        regi_t4 = findViewById(R.id.regi_t4);
        regi_t5 = findViewById(R.id.regi_t5);
        regi_t6 = findViewById(R.id.regi_t6);
        regi_ed1 = findViewById(R.id.regi_ed1);
        regi_ed1.setFilters(Filters.EmojiFilter());
        regi_ed2 = findViewById(R.id.regi_ed2);
        regi_ed2.setFilters(Filters.UpperEmojiFilter());
        regi_ed4 = findViewById(R.id.regi_ed4);
        regi_ed4.setFilters(Filters.EmojiFilter());
        regi_ed5 = findViewById(R.id.regi_ed5);
        regi_ed5.setFilters(Filters.EmojiFilter());
        regi_ed6 = findViewById(R.id.regi_ed6);
        regi_ed6.setFilters(Filters.EmojiFilter());
        regi_b1 = findViewById(R.id.regi_b1);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        regi_cl1.setOnClickListener(null);

        regi_fab.setOnClickListener(v -> {
            Intent register_it = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(register_it);
            finish();
        });

        // EditText Validations textChangedListeners
        regi_ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(regi_ed1.getText()).toString().isEmpty()){
                    regi_t1.setHelperText("Required*");
                }else {regi_t1.setHelperText(null);}}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        regi_ed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(regi_ed2.getText()).toString().isEmpty()){
                    regi_t2.setHelperText("Required*");
                }else {regi_t2.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        regi_ed4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(regi_ed4.getText()).toString().isEmpty()){
                    regi_t4.setHelperText("Required*");
                }else {regi_t4.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        regi_ed5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(regi_ed5.getText()).toString().isEmpty()){
                    regi_t5.setHelperText("Required*");
                }else {regi_t5.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        regi_ed6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(regi_ed6.getText()).toString().isEmpty()){
                    regi_t6.setHelperText("Required*");
                }else {regi_t6.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        //End of EditText Validations textChangedListeners


        // register button Validations
        regi_b1.setOnClickListener(v -> {
            errorfalse();
           if(Objects.requireNonNull(regi_ed1.getText()).toString().isEmpty()) {
                regi_t1.setErrorEnabled(true);
                regi_t1.setError("Name can't be empty!!!");
                regi_t1.requestFocus();
                imm.showSoftInput(regi_ed1, InputMethodManager.SHOW_IMPLICIT);
           } else if (Objects.requireNonNull(regi_ed2.getText()).toString().isEmpty()) {
                regi_t2.setErrorEnabled(true);
                regi_t2.setError("Username can't be empty!!!");
                regi_t2.requestFocus();
                imm.showSoftInput(regi_ed2, InputMethodManager.SHOW_IMPLICIT);
           } else if (validateusername(regi_ed2.getText().toString())) {
               regi_t2.setErrorEnabled(true);
               regi_t2.setError("Username must contain an lowercase letter & a number");
               regi_t2.requestFocus();
               imm.showSoftInput(regi_ed2, InputMethodManager.SHOW_IMPLICIT);
           } else if (regi_ed4.length() < 10) {
                regi_t4.setErrorEnabled(true);
                regi_t4.setError("Invalid Mobile No.!!!");
                regi_t4.requestFocus();
                imm.showSoftInput(regi_ed4, InputMethodManager.SHOW_IMPLICIT);
           } else if (Objects.requireNonNull(regi_ed5.getText()).toString().isEmpty()) {
                regi_t5.setErrorEnabled(true);
                regi_t5.setError("Password can't be empty!!!");
                regi_t5.requestFocus();
                imm.showSoftInput(regi_ed5, InputMethodManager.SHOW_IMPLICIT);
           } else if (regi_ed5.getText().length()<8) {
                regi_t5.setErrorEnabled(true);
                regi_t5.setError("Password needs to be atleast 8 characters long");
                regi_t5.requestFocus();
                imm.showSoftInput(regi_ed5, InputMethodManager.SHOW_IMPLICIT);
           } else if (validatepass(regi_ed5.getText().toString())) {
               regi_t5.setErrorEnabled(true);
               regi_t5.setError("Password must contain an lowercase letter, uppercase letter & a number");
               regi_t5.requestFocus();
               imm.showSoftInput(regi_ed5, InputMethodManager.SHOW_IMPLICIT);
           } else if (Objects.requireNonNull(regi_ed6.getText()).toString().isEmpty()) {
                regi_t6.setErrorEnabled(true);
                regi_t6.setError("Password can't be empty!!!");
                regi_t6.requestFocus();
                imm.showSoftInput(regi_ed6, InputMethodManager.SHOW_IMPLICIT);
           } else if (!(regi_ed5.getText().toString().equals(regi_ed6.getText().toString()))) {
                Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                regi_t6.requestFocus();
                imm.showSoftInput(regi_ed6, InputMethodManager.SHOW_IMPLICIT);
           } else {
                name = regi_ed1.getText().toString().trim();
                username = regi_ed2.getText().toString().trim();
                mno = Objects.requireNonNull(regi_ed4.getText()).toString().trim();
                password = regi_ed5.getText().toString().trim();
                OTPVerificationDialog otpDialog = new OTPVerificationDialog(RegisterActivity.this, RegisterActivity.this, username, name, mno, password, false);
                otpDialog.setCancelable(false);
                otpDialog.show();
                Window window = otpDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
           }
        });
        // end of register button validations
    }


//validate password
    public boolean validatepass(String password) {
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern lowercase = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");
        return (!lowercase.matcher(password).find()) || (!uppercase.matcher(password).find()) || (!digit.matcher(password).find());
    }
//End of validate password
//validate username
public boolean validateusername(String password) {
    Pattern lowercase = Pattern.compile("[a-z]");
    Pattern digit = Pattern.compile("[0-9]");
    return (!lowercase.matcher(password).find()) || (!digit.matcher(password).find());
}
//End of validate username
    private void errorfalse(){
        regi_t1.setErrorEnabled(false);
        regi_t2.setErrorEnabled(false);
        regi_t4.setErrorEnabled(false);
        regi_t5.setErrorEnabled(false);
        regi_t6.setErrorEnabled(false);
    }
}