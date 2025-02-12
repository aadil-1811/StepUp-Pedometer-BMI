package com.example.walkbuddy;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfFragment extends Fragment implements DialogHeightFragment.OnInputListener, DialogWeightFragment.OnInputListener, DialogGenderFragment.OnInputListener {
    MaterialButton logout, delete;
    TextView titleUsername, titleName, dob_val, gender_val, height_val, weight_val, mobile_val;
    EditText name_val, username_val;
    boolean name_edit = false, username_edit = false;
    ImageView gender_icon;
    FirebaseAuth mfirebaseAuth;
    FirebaseUser mfirebaseUser;
    StorageReference mStorage;
    ImageButton edit_name, edit_username, edit_gender, edit_weight, edit_height, edit_dob, edit_mob;
    String username, name, dob, gender, height, weight, mobile, age;
    FloatingActionButton prof_editpic;
    CircleImageView prof_pic;
    InputMethodManager imm;
    private int mYear, mMonth, mDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prof, container, false);
        logout = v.findViewById(R.id.logout);
        delete = v.findViewById(R.id.delete);
        titleUsername = v.findViewById(R.id.titleUsername);
        titleName = v.findViewById(R.id.titleName);
        name_val = v.findViewById(R.id.name_val);
        name_val.setFilters(Filters.EmojiFilter());
        username_val = v.findViewById(R.id.username_val);
        username_val.setFilters(Filters.UpperEmojiFilter());
        dob_val = v.findViewById(R.id.dob_val);
        gender_val = v.findViewById(R.id.gender_val);
        height_val = v.findViewById(R.id.height_val);
        weight_val = v.findViewById(R.id.weight_val);
        mobile_val = v.findViewById(R.id.mob_val);
        prof_editpic = v.findViewById(R.id.prof_editpic);
        prof_pic = v.findViewById(R.id.prof_pic);
        edit_name = v.findViewById(R.id.edit_name);
        edit_username = v.findViewById(R.id.edit_username);
        edit_gender = v.findViewById(R.id.edit_gender);
        edit_weight = v.findViewById(R.id.edit_weight);
        edit_height = v.findViewById(R.id.edit_height);
        edit_dob = v.findViewById(R.id.edit_dob);
        edit_mob = v.findViewById(R.id.edit_mob);
        gender_icon = v.findViewById(R.id.gender_icon);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        mfirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();
        updateUI();
        delete.setOnClickListener(v18 -> {
            Dialog1DelAccFragment pcFragment = new Dialog1DelAccFragment();
            pcFragment.setTargetFragment(ProfFragment.this, 111);
            pcFragment.show(getActivity().getSupportFragmentManager(), "pcFragment");
        });
        logout.setOnClickListener(v1 -> {
            FirebaseAuth.getInstance().signOut();
            getActivity().stopService(new Intent(getActivity().getApplicationContext(), StepsService.class));
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        });
        prof_pic.setOnClickListener(v110 -> openDialog());
        prof_editpic.setOnClickListener(v19 -> ImagePicker.with(getActivity())
                .cropSquare()            //Crop image(Optional), Check Customization for more
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(9));
        edit_name.setOnClickListener(v16 -> {
            if (!name_edit) {
                name_val.requestFocus();
                imm.showSoftInput(name_val, InputMethodManager.SHOW_IMPLICIT);
                name_edit = true;
                edit_name.setImageResource(R.drawable.prof_save);
                name_val.setEnabled(true);
            } else {
                if (!name_val.getText().toString().isEmpty()) {
                    name_edit = false;
                    edit_name.setImageResource(R.drawable.prof_edit);
                    name_val.setEnabled(false);
                    setName();
                }
            }
        });
        edit_username.setOnClickListener(v17 -> {
            if (!username_edit) {
                username_val.requestFocus();
                imm.showSoftInput(username_val, InputMethodManager.SHOW_IMPLICIT);
                username_edit = true;
                edit_username.setImageResource(R.drawable.prof_save);
                username_val.setEnabled(true);
            } else {
                if (!username_val.getText().toString().isEmpty()) {
                    username_edit = false;
                    edit_username.setImageResource(R.drawable.prof_edit);
                    username_val.setEnabled(false);
                    setUsername();
                }
            }
        });
        edit_gender.setOnClickListener(v15 -> {
            DialogGenderFragment dgFragment = new DialogGenderFragment();
            dgFragment.setTargetFragment(ProfFragment.this, 104);
            dgFragment.show(getActivity().getSupportFragmentManager(), "dgFragment");
        });
        edit_height.setOnClickListener(v12 -> {
            DialogHeightFragment dhFragment = new DialogHeightFragment(true);
            dhFragment.setTargetFragment(ProfFragment.this, 102);
            dhFragment.show(getActivity().getSupportFragmentManager(), "dhFragment");
        });
        edit_weight.setOnClickListener(v13 -> {
            DialogWeightFragment dwFragment = new DialogWeightFragment(true);
            dwFragment.setTargetFragment(ProfFragment.this, 103);
            dwFragment.show(getActivity().getSupportFragmentManager(), "dwFragment");
        });
        edit_dob.setOnClickListener(v14 -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        dob = Utility.zeroX(dayOfMonth) + "-" + Utility.zeroX((monthOfYear + 1)) + "-" + Utility.zeroX(year);
                        age = getAge(year, monthOfYear, dayOfMonth);
                        MainActivity.toOpen = "profile";
                        setDob();
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });
        return v;
    }

    private void updateUI() {

        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(Objects.requireNonNull(str));

        StorageReference filepath = mStorage.child(id);
        filepath.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(prof_pic));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("info");
        String ok = id.substring(0, id.indexOf("@"));
        ref.child("username").setValue(ok);
        ref.get().addOnSuccessListener(snapshot -> {
            name = snapshot.child("name").getValue().toString();
            username = snapshot.child("username").getValue().toString();
            mobile = snapshot.child("mobile").getValue().toString();
            weight = snapshot.child("weight").getValue().toString();
            height = snapshot.child("height").getValue().toString();
            gender = snapshot.child("gender").getValue().toString();
            dob = snapshot.child("dob").getValue().toString();
            titleUsername.setText(username);
            titleName.setText(name);
            name_val.setText(name);
            username_val.setText(username);
            mobile_val.setText(mobile);
            weight_val.setText(weight + " kgs");
            height_val.setText(height + " cms");
            gender_val.setText(gender);
            if (gender.equals("Male"))
                gender_icon.setImageResource(R.drawable.info_male);
            else
                gender_icon.setImageResource(R.drawable.info_female);
            dob_val.setText(dob);
        });
    }

    private void setName() {
        name = name_val.getText().toString();
        titleName.setText(name);
        setData("name", name);
        MainActivity.toOpen = "profile";
    }

    private void setUsername() {
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        username = username_val.getText().toString();
        String oldmail = mfirebaseUser.getEmail();
        String oldid = Utility.EncodeString(oldmail);
        String newmail = username + "@wb.com";
        mfirebaseUser.updateEmail(newmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Username updated", Toast.LENGTH_SHORT).show();
                String newid = Utility.EncodeString(newmail);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child(oldid).get().addOnSuccessListener(dataSnapshot -> {
                    ref.child(newid).setValue(dataSnapshot.getValue());
                    ref.child(oldid).removeValue();
                });
                final long TEN_MEGABYTE = 1024 * 1024*10;
                mStorage.child(oldid).getBytes(TEN_MEGABYTE).addOnSuccessListener(bytes -> {
                    mStorage.child(newid).putBytes(bytes);
                    mStorage.child(oldid).delete();
                });
                titleUsername.setText(username);
                username_val.setText(username);
            } else {
                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void setGender(String input) {
        gender = input;
        if (gender.equals("Male"))
            gender_icon.setImageResource(R.drawable.info_male);
        else
            gender_icon.setImageResource(R.drawable.info_female);
        gender_val.setText(gender);
        setData("gender", gender);
    }

    @Override
    public void setHeight(String input) {
        height = input;
        height_val.setText(height + " cms");
        setData("height", height);
    }

    @Override
    public void setWeight(String input) {
        weight = input;
        weight_val.setText(weight + " kgs");
        setData("weight", weight);
    }

    public void setDob() {
        dob_val.setText(dob);
        setData("dob", dob);
        setData("age", age);
    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
            age--;
        Integer ageInt = Integer.valueOf(age);
        String ageS = ageInt.toString();
        return ageS;
    }

    public void setData(String key, String val) {
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(str);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("info");
        ref.child(key).setValue(val);
    }

    public void openDialog() {
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(str);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_profpic);
        ImageView dialog_iv = dialog.findViewById(R.id.dialog_iv);
        StorageReference filepath = mStorage.child(id);
        filepath.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(dialog_iv));

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        dialog.show();
    }


}