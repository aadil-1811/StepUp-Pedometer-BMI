package com.example.walkbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {
    ImageButton back;
    Button btn_otp, btn_submit;
    TextInputEditText ed_mobile, ed_pass, ed_pass2;
    TextInputLayout tl_mobile, tl_pass, tl_pass2;
    CardView cardview1, cardview2;
    String mob,oldpass,pass,username;
    InputMethodManager imm;
    FirebaseAuth mAuth;
    public static Boolean valid = false;
    boolean isPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        getWindow().setStatusBarColor(ContextCompat.getColor(ForgotPasswordActivity.this,R.color.statusBarPurple));
        back = findViewById(R.id.back);
        ed_mobile = findViewById(R.id.mobile_ed);
        ed_pass = findViewById(R.id.ed_pass);
        ed_pass.setFilters(Filters.EmojiFilter());
        ed_pass2 = findViewById(R.id.ed_pass2);
        ed_pass2.setFilters(Filters.EmojiFilter());
        tl_mobile = findViewById(R.id.mobile_tl);
        tl_pass = findViewById(R.id.pass_tl);
        tl_pass2 = findViewById(R.id.pass2_tl);
        btn_otp = findViewById(R.id.btn_otp);
        btn_submit = findViewById(R.id.btn_submit);
        cardview1 = findViewById(R.id.cardView1);
        cardview2 = findViewById(R.id.cardView2);
        mAuth = FirebaseAuth.getInstance();


        ed_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(ed_mobile.getText()).toString().isEmpty()){
                    tl_mobile.setHelperText("Required*");
                }else {tl_mobile.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        ed_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(ed_pass.getText()).toString().isEmpty()){
                    tl_pass.setHelperText("Required*");
                }else {tl_pass.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        ed_pass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(ed_pass2.getText()).toString().isEmpty()){
                    tl_pass2.setHelperText("Required*");
                }else {tl_pass2.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        back.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });

        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_mobile.length() < 10) {
                    tl_mobile.setErrorEnabled(true);
                    tl_mobile.setError("Invalid Mobile No.!!!");
                    tl_mobile.requestFocus();
                    imm.showSoftInput(ed_mobile, InputMethodManager.SHOW_IMPLICIT);
                }else{
                    mob = ed_mobile.getText().toString();
                    check();
                }
            }
        });
    }

    private void check(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    if (snap.child("info").child("mobile").getValue().equals(mob)){
                        username = snap.child("info").child("username").getValue().toString();
                        oldpass =  snap.child("info").child("password").getValue().toString();
                        isPresent = true;
                    }
                }
                if (isPresent){
                    validate();
                } else {
                    Toast.makeText(getApplicationContext(), "No such Mobile no. Found!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validate(){
        OTPVerificationDialog otpDialog = new OTPVerificationDialog(ForgotPasswordActivity.this, ForgotPasswordActivity.this, null, null, mob, null, true);
        otpDialog.setCancelable(false);
        otpDialog.show();
        Window window = otpDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        otpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(valid){
                    cardview1.setVisibility(View.GONE);
                    cardview2.setVisibility(View.VISIBLE);
                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ed_pass.getText().length()<8) {
                                tl_pass.setErrorEnabled(true);
                                tl_pass.setError("Password needs to be atleast 8 characters long");
                                tl_pass.requestFocus();
                                imm.showSoftInput(ed_pass, InputMethodManager.SHOW_IMPLICIT);
                            } else if (validatepass(ed_pass.getText().toString())) {
                                tl_pass.setErrorEnabled(true);
                                tl_pass.setError("Password must contain an lowercase letter, uppercase letter & a number");
                                tl_pass.requestFocus();
                                imm.showSoftInput(ed_pass, InputMethodManager.SHOW_IMPLICIT);
                            } else if (Objects.requireNonNull(ed_pass2.getText()).toString().isEmpty()) {
                                tl_pass2.setErrorEnabled(true);
                                tl_pass2.setError("Password can't be empty!!!");
                                tl_pass2.requestFocus();
                                imm.showSoftInput(ed_pass2, InputMethodManager.SHOW_IMPLICIT);
                            } else if (!(ed_pass.getText().toString().equals(ed_pass2.getText().toString()))) {
                                Toast.makeText(ForgotPasswordActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                                tl_pass2.requestFocus();
                                imm.showSoftInput(ed_pass2, InputMethodManager.SHOW_IMPLICIT);
                            } else {
                                pass = ed_pass.getText().toString();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snap: dataSnapshot.getChildren()){
                                            if (snap.child("info").child("mobile").getValue().equals(mob)){
                                                mAuth.signInWithEmailAndPassword(username+"@wb.com",oldpass).addOnCompleteListener(task -> {
                                                    if(task.isSuccessful()){
                                                        FirebaseUser mUser = task.getResult().getUser();
                                                        mUser.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    String str = mUser.getEmail();
                                                                    String id = Utility.EncodeString(str);
                                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("info");
                                                                    ref.child("password").setValue(pass);
                                                                    Toast.makeText(getApplicationContext(), "Password reset Successful!!", Toast.LENGTH_SHORT).show();
                                                                    FirebaseAuth.getInstance().signOut();
                                                                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "Password reset Operation failed!!", Toast.LENGTH_SHORT).show();
                                                                    FirebaseAuth.getInstance().signOut();
                                                                }
                                                            }
                                                        });
                                                    } else{
                                                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

//validate password
    public boolean validatepass(String password) {
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern lowercase = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");
        return (!lowercase.matcher(password).find()) || (!uppercase.matcher(password).find()) || (!digit.matcher(password).find());
    }
//End of validate password
}