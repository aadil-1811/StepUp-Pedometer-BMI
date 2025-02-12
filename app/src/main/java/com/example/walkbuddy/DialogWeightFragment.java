package com.example.walkbuddy;

import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.kevalpatel2106.rulerpicker.RulerValuePicker;
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;

public class DialogWeightFragment extends DialogFragment {
    RulerValuePicker rvp;
    TextView weight;
    MaterialButton sub;
    String val = "null";
    private Boolean isFragment = false;

    DialogWeightFragment(Boolean frag){
        this.isFragment = frag;
    }

    public interface OnInputListener {
        void setWeight(String input);
    }
    public OnInputListener mOnInputListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialogweight, container, false);
        rvp = v.findViewById(R.id.ruler_picker);
        weight = v.findViewById(R.id.weight);
        sub = v.findViewById(R.id.sub);
        if(isFragment){
            try{
                mOnInputListener = (OnInputListener) getTargetFragment();
            }catch (ClassCastException e){
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                mOnInputListener = (OnInputListener) getActivity();
            }
            catch (ClassCastException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
        rvp.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
                weight.setText(selectedValue+" kgs");
                val = Integer.toString(selectedValue);
            }
            @Override
            public void onIntermediateValueChange(int selectedValue) { }
        });
        sub.setOnClickListener(v -> {
            mOnInputListener.setWeight(val);
            getDialog().dismiss();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isFragment){
            MainActivity.toOpen = "profile";
        }
    }
}