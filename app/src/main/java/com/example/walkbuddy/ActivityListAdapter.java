package com.example.walkbuddy;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityListAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private final SparseBooleanArray mCollapsedStatus;
    private List<ItemsModel> itemsModelsl;
    private List<ItemsModel> itemsModelListFiltered;

    public ActivityListAdapter(Context context, List<ItemsModel> itemsModelsl) {
        mContext = context;
        mCollapsedStatus = new SparseBooleanArray();
        this.itemsModelsl = itemsModelsl;
        this.itemsModelListFiltered = itemsModelsl;
    }


    @Override
    public int getCount() {
        return itemsModelListFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsModelListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_activitylist, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.expandableTextView = (ExpandableTextView) convertView.findViewById(R.id.expand_text_view);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_date.setText(itemsModelListFiltered.get(position).getDate1() + " \uD83D\uDC63");
        viewHolder.expandableTextView.setText("Steps :- " + itemsModelListFiltered.get(position).getStep1() + "\nDistance :- " + itemsModelListFiltered.get(position).getDis1() + " km\nCalories :- " + itemsModelListFiltered.get(position).getCal1() + " cal", mCollapsedStatus, position);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.count = itemsModelsl.size();
                    filterResults.values = itemsModelsl;

                } else {
                    List<ItemsModel> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for (ItemsModel itemsModel : itemsModelsl) {
                        if (itemsModel.getDate1().startsWith(searchStr)) {
                            resultsModel.add(itemsModel);
                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itemsModelListFiltered = (List<ItemsModel>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    private static class ViewHolder {
        ExpandableTextView expandableTextView;
        TextView tv_date;
    }

}
