package com.example.walkbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextView login_tv2, login_tv3;
    Button login_b1;
    TextInputLayout login_t1, login_t2;
    TextInputEditText login_ed1, login_ed2;
    ConstraintLayout login_cl1;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(ContextCompat.getColor(LoginActivity.this, R.color.statusBarPurple));

        login_tv2 = findViewById(R.id.login_tv2);
        login_tv3 = findViewById(R.id.login_tv3);
        login_b1 = findViewById(R.id.login_b1);
        login_ed1 = findViewById(R.id.login_ed1);
        login_ed1.setFilters(Filters.UpperEmojiFilter());
        login_ed2 = findViewById(R.id.login_ed2);
        login_ed2.setFilters(Filters.EmojiFilter());
        login_t1 = findViewById(R.id.login_t1);
        login_t2 = findViewById(R.id.login_t2);
        login_cl1 = findViewById(R.id.login_cl1);
        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermission("Manifest.permission.ACTIVITY_RECOGNITION", 81);
        }

        login_cl1.setOnClickListener(null);
        login_tv2.setOnClickListener(v -> {
            Intent login_it = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(login_it);
            finish();
        });

        //forgot password
        login_tv3.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
        //end of forgot password

        //editText Validations using textChangedListener
        login_ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(login_ed1.getText()).toString().isEmpty()) {
                    login_t1.setErrorEnabled(true);
                    login_t1.setError("Username can't be empty!!!");
                } else {
                    login_t1.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        login_ed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(login_ed2.getText()).toString().isEmpty()) {
                    login_t2.setErrorEnabled(true);
                    login_t2.setError("Password can't be empty!!!");
                } else {
                    login_t2.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //end of editText Validations using textChangedListener

        login_b1.setOnClickListener(v -> {
            if (Objects.requireNonNull(login_ed1.getText()).toString().isEmpty()) {
                login_t1.setErrorEnabled(true);
                login_t1.setError("Username can't be empty!!!");
            } else if (Objects.requireNonNull(login_ed2.getText()).toString().isEmpty()) {
                login_t2.setErrorEnabled(true);
                login_t2.setError("Password can't be empty!!!");
            } else {
                check();
            }
        });
    }

    private void check() {
        String user = Objects.requireNonNull(login_ed1.getText()).toString().trim();
        String pass = Objects.requireNonNull(login_ed2.getText()).toString().trim();
        String id = Utility.EncodeString(user + "@wb.com");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                if (Objects.requireNonNull(snap.getKey()).equals(id)) {
                    if (Objects.requireNonNull(snap.child("info").child("password").getValue()).toString().equals(pass)) {
                        mAuth.signInWithEmailAndPassword(user + "@wb.com", pass).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), "Login Successful!!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(LoginActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, requestCode);
        }
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 81) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Showing the toast message
                Toast.makeText(LoginActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Permission is critical for functioning of the application, please allow the permission explicitly via settings", Toast.LENGTH_SHORT).show();
                finishAndRemoveTask();
            }
        }
    }
}