package com.example.walkbuddy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OTPVerificationDialog extends Dialog {

    private EditText otp_ed1, otp_ed2, otp_ed3, otp_ed4, otp_ed5, otp_ed6;
    private TextView otp_resendBtn, otp_mobno;
    private Button otp_verifyBtn;

    private final String username, name, mobno, pass;
    private String otpId;
    FirebaseAuth mAuth;
    AppCompatActivity activity;

    //resend otp time in seconds
    private final int resendTime = 60;
    //will be true after 30 seconds
    private boolean resendEnabled = false, fp = false;
    private int selectedETpos = 0;


    public OTPVerificationDialog(@NonNull Context context, AppCompatActivity act, String username, String name,String mno, String pass, Boolean fp) {
        super(context);
        this.username = username;
        this.name = name;
        this.mobno = mno;
        this.pass = pass;
        this.activity = act;
        this.fp = fp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
        mAuth = FirebaseAuth.getInstance();
        initiateOTP();
        setContentView(R.layout.dialog_otpverify);

        otp_ed1 = findViewById(R.id.otp_ed1);
        otp_ed2 = findViewById(R.id.otp_ed2);
        otp_ed3 = findViewById(R.id.otp_ed3);
        otp_ed4 = findViewById(R.id.otp_ed4);
        otp_ed5 = findViewById(R.id.otp_ed5);
        otp_ed6 = findViewById(R.id.otp_ed6);
        otp_resendBtn = findViewById(R.id.otp_resendBtn);
        otp_verifyBtn = findViewById(R.id.otp_btnVerify);
        otp_mobno = findViewById(R.id.otp_tv_mobno);
        otp_mobno.setText("+91 "+mobno);

        otp_ed1.addTextChangedListener(textwatcher);
        otp_ed2.addTextChangedListener(textwatcher);
        otp_ed3.addTextChangedListener(textwatcher);
        otp_ed4.addTextChangedListener(textwatcher);
        otp_ed5.addTextChangedListener(textwatcher);
        otp_ed6.addTextChangedListener(textwatcher);


        //by default open keyboard on first editText
        showKeyboard(otp_ed1);

        //start countdown timer
        startCountDownTimer();

        otp_resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resendEnabled){
                    initiateOTP();
                    startCountDownTimer();
                }
            }
        });

        otp_verifyBtn.setOnClickListener(v -> {
            final String OTP = otp_ed1.getText().toString()+otp_ed2.getText().toString()
                    +otp_ed3.getText().toString() +otp_ed4.getText().toString()
                    +otp_ed5.getText().toString() +otp_ed6.getText().toString();
            if (OTP.length()==6){
                PhoneAuthCredential cred = PhoneAuthProvider.getCredential(otpId, OTP);
                signInWithPhoneAuthCredential(cred);
            }
        });


    }

    private final TextWatcher textwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()>0){
                if (selectedETpos==0){
                    selectedETpos = 1;
                    showKeyboard(otp_ed2);
                } else if(selectedETpos==1){
                    selectedETpos = 2;
                    showKeyboard(otp_ed3);
                } else if(selectedETpos==2){
                    selectedETpos = 3;
                    showKeyboard(otp_ed4);
                } else if(selectedETpos==3){
                    selectedETpos = 4;
                    showKeyboard(otp_ed5);
                } else if(selectedETpos==4){
                    selectedETpos = 5;
                    showKeyboard(otp_ed6);
                } else{
                    otp_verifyBtn.setBackgroundResource(R.drawable.otpverify_butbg);
                }
            }
        }
    };

    private void showKeyboard(EditText otp_ed){
        otp_ed.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(otp_ed, InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCountDownTimer(){
        resendEnabled = false;
        otp_resendBtn.setTextColor(Color.parseColor("#99000000"));
        new CountDownTimer(resendTime * 1000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                otp_resendBtn.setText("Resend Code ("+(millisUntilFinished/1000)+")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                otp_resendBtn.setText("Resend OTP");
                otp_resendBtn.setTextColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
            }
        }.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL){
            if(selectedETpos == 5){
                selectedETpos = 4;
                showKeyboard(otp_ed5);
            } else if(selectedETpos == 4){
                selectedETpos = 3;
                showKeyboard(otp_ed4);
            } else if(selectedETpos == 3){
                selectedETpos = 2;
                showKeyboard(otp_ed3);
            } else if(selectedETpos == 2){
                selectedETpos = 1;
                showKeyboard(otp_ed2);
            } else if(selectedETpos == 1){
                selectedETpos = 0;
                showKeyboard(otp_ed1);
            }
            otp_verifyBtn.setBackgroundResource(R.drawable.otpverify_butbg1);
            return true;
        }else{
            return super.onKeyUp(keyCode, event);
        }
    }

    private void initiateOTP(){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+mobno)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            signInWithPhoneAuthCredential(credential);
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            dismiss();
        }
        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            otpId = verificationId;
        }
    };

    private void createUser(){
        mAuth.createUserWithEmailAndPassword(username+"@wb.com", pass)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        registerRealtime();
                        mAuth.signInWithEmailAndPassword(username+"@wb.com", pass)
                                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            dismiss();
                                            activity.startActivity(new Intent(getContext(), GetInfoActivity.class));
                                            activity.finish();}
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            user.delete();
                            if(fp){
                                ForgotPasswordActivity.valid = true;
                                dismiss();
                            } else {
                                createUser();
                                Toast.makeText(getContext(), "Registration Successful!!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //Invalid OTP code
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                });
    }
    private void registerRealtime(){
        String id = Utility.EncodeString(username+"@wb.com");
        FirebaseDatabase.getInstance().getReference().child(id);
        HashMap<String, Object> map = new HashMap<>();
        map.put("username",username);
        map.put("password", pass);
        map.put("mobile",mobno);
        map.put("name",name);
        map.put("gender","null");
        map.put("dob","null");
        map.put("age","null");
        map.put("height","null");
        map.put("weight","null");
        map.put("goal","null");

        FirebaseDatabase.getInstance().getReference().child(id).child("info").updateChildren(map);
    }
}
