package com.example.walkbuddy;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import java.util.List;


public class DialogStatsFragment extends DialogFragment {
    private ImageButton dismiss;
    private String val = "null";
    private Boolean isFragment;
    ListView listView;
    EditText search;
    List<ItemsModel> itemsModelList;

    DialogStatsFragment(Boolean frag, List<ItemsModel> itemsModelsl){
        this.isFragment = frag;
        this.itemsModelList = itemsModelsl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialogstats, container, false);
        dismiss = v.findViewById(R.id.dismiss);
        listView = v.findViewById(R.id.listView);
        search = v.findViewById(R.id.search);
        return v;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
        ActivityListAdapter adapter = new ActivityListAdapter(getActivity(), itemsModelList );
        listView.setAdapter(adapter);
        dismiss.setOnClickListener(v -> getDialog().dismiss());
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
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
