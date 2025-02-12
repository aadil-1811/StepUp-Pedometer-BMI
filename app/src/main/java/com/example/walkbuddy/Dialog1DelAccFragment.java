package com.example.walkbuddy;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class Dialog1DelAccFragment extends DialogFragment {
    MaterialButton cancel,proceed;
    EditText password;
    String pass;
    FirebaseAuth mfirebaseAuth;
    FirebaseUser mfirebaseUser;
    StorageReference mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dialog1delacc, container, false);
        password = v.findViewById(R.id.password);
        proceed = v.findViewById(R.id.proceed);
        cancel = v.findViewById(R.id.cancel);
        mfirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        return v;
    }

    @Override
    public void onResume() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
        proceed.setOnClickListener(v1 -> {
            String str = mfirebaseUser.getEmail();
            String id = Utility.EncodeString(Objects.requireNonNull(str));
            pass = password.getText().toString().trim();
            FirebaseDatabase.getInstance().getReference().child(id).child("info").get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.child("password").getValue().equals(pass)){

                    AuthCredential credential = EmailAuthProvider.getCredential(mfirebaseUser.getEmail(), pass);
                    mfirebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(task1 -> {
                               if (task1.isSuccessful()){
                                   mfirebaseUser.delete().addOnCompleteListener(task -> {
                                       if (task.isSuccessful()){
                                           StorageReference filepath = mStorage.child(id);
                                           filepath.delete();
                                           getActivity().stopService(new Intent(getActivity().getApplicationContext(), StepsService.class));
                                           getDialog().dismiss();
                                           FirebaseDatabase.getInstance().getReference().child(id).removeValue();
                                           startActivity(new Intent(getContext(), SplashActivity.class));
                                           getActivity().finish();
                                           Toast.makeText(getContext(), "Account Terminated!!!", Toast.LENGTH_SHORT).show();
                                       } else {
                                           Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                           getDialog().dismiss();
                                       }
                                   });
                               } else {
                                   Toast.makeText(getContext(), Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                   getDialog().dismiss();
                               }
                            });
                } else {
                    Toast.makeText(getContext(), "Invalid Password!!!", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
            });
        });
        cancel.setOnClickListener(v -> getDialog().dismiss());
    }
}