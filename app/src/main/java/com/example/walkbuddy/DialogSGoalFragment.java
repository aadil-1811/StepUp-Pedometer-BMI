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
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import it.sephiroth.android.library.numberpicker.NumberPicker;

public class DialogSGoalFragment extends DialogFragment {
    NumberPicker numberPicker;
    MaterialButton sub;

    private Boolean isFragment = false;

    DialogSGoalFragment(Boolean frag){
        this.isFragment = frag;
    }
    public interface OnInputListener {
        void setStepGoal(String input);
    }
    public OnInputListener mOnInputListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialogsgoal, container, false);
        numberPicker = v.findViewById(R.id.number_picker);
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
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnInputListener.setStepGoal(Integer.toString(numberPicker.getProgress()));
                dismiss();
            }
        });
    }
}