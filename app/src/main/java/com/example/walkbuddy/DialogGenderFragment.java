package com.example.walkbuddy;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

public class DialogGenderFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    String[] Genders ={"Male"," Female"};
    int flags[] = {R.drawable.info_male, R.drawable.info_female};
    Spinner spinner;
    MaterialButton sub;
    String val = "null";

    public interface OnInputListener {
        void setGender(String input);
    }
    public OnInputListener mOnInputListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialoggender, container, false);
        spinner = v.findViewById(R.id.simpleSpinner);
        spinner.setOnItemSelectedListener(this);
        GenderSpinnerAdapter customAdapter = new GenderSpinnerAdapter(getContext(),flags, Genders);
        spinner.setAdapter(customAdapter);
        sub = v.findViewById(R.id.sub);
        try{
            mOnInputListener = (OnInputListener) getTargetFragment();
        }catch (ClassCastException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
        sub.setOnClickListener(v -> {
            mOnInputListener.setGender(val);
            getDialog().dismiss();
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        MainActivity.toOpen = "profile";
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        val = Genders[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}