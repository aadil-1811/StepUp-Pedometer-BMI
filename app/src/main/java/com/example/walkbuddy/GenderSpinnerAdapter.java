package com.example.walkbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GenderSpinnerAdapter extends BaseAdapter {
    Context context;
    int flags[];
    String[] Genders;
    LayoutInflater inflter;

    public GenderSpinnerAdapter(Context applicationContext, int[] flags, String[] Genders) {
        this.context = applicationContext;
        this.flags = flags;
        this.Genders = Genders;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_genderspinner, null);
        ImageView icon = view.findViewById(R.id.imageView);
        TextView names = view.findViewById(R.id.textView);
        icon.setImageResource(flags[i]);
        names.setText(Genders[i]);
        return view;
    }
}
