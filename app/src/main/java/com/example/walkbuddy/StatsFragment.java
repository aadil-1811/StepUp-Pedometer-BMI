package com.example.walkbuddy;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class StatsFragment extends Fragment {

    String date, init;
    Date initdate, currdate;
    long length;
    String[] days = new String[7];
    Float[] c = new Float[7];
    Float[] d = new Float[7];
    Float[] s = new Float[7];
    FirebaseAuth mfirebaseAuth;
    FirebaseUser mfirebaseUser;
    LineChart lineChartDis, lineChartCal;
    BarChart barChart;
    ImageButton prev, next, open;
    CardView cv1, cv2, cv3;
    ListView listView;
    List<ItemsModel> itemsModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stats, container, false);
        mfirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
        lineChartDis = v.findViewById(R.id.lc);
        lineChartCal = v.findViewById(R.id.lc2);
        barChart = v.findViewById(R.id.bc);
        prev = v.findViewById(R.id.prev);
        next = v.findViewById(R.id.next);
        open = v.findViewById(R.id.open);
        cv1 = v.findViewById(R.id.cardView1);
        cv2 = v.findViewById(R.id.cardView);
        cv3 = v.findViewById(R.id.cardView3);
        listView = v.findViewById(R.id.listView);

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        date = Utility.zeroX(day) + "," + Utility.zeroX((month + 1)) + "," + Utility.zeroX(year);
        int j = 0;
        for (int i = 0; i < 7; i++) {
            days[i] = getCalculatedDate("dd,MM,yyyy", j);
            j--;
        }

        getData();
        statTransit();
        fillList();
        open.setOnClickListener(v1 -> {
            DialogStatsFragment dsFragment = new DialogStatsFragment(true, itemsModelList);
            dsFragment.setTargetFragment(StatsFragment.this, 133);
            dsFragment.show(requireActivity().getSupportFragmentManager(), "dsFragment");
        });
        return v;
    }

    private void getData() {
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(Objects.requireNonNull(str));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("steps");
        ref.get().addOnSuccessListener(ds -> {
            for (int i = 0; i < 7; i++) {
                if (ds.hasChild(days[i])) {
                    c[i] = Float.parseFloat(Objects.requireNonNull(ds.child(days[i]).child("c").getValue()).toString());
                    d[i] = Float.parseFloat(Objects.requireNonNull(ds.child(days[i]).child("d").getValue()).toString());
                    s[i] = Float.parseFloat(Objects.requireNonNull(ds.child(days[i]).child("s").getValue()).toString());
                } else {
                    c[i] = 0f;
                    d[i] = 0f;
                    s[i] = 0f;
                }
            }
            ArrayList<BarEntry> dataVals = new ArrayList<>();
            ArrayList<Entry> dataVals1 = new ArrayList<>();
            ArrayList<Entry> dataVals2 = new ArrayList<>();
            int j = 1;
            for (int i = 6; i >= 0; i--) {
                dataVals.add(new BarEntry(j, s[i]));
                dataVals1.add(new Entry(j, c[i]));
                dataVals2.add(new Entry(j, d[i]));
                j++;
            }
            displayBarChart(dataVals);

            displayLineChart(dataVals2);

            displayLineChart2(dataVals1);
        });
    }

    private void displayBarChart(ArrayList<BarEntry> dataVals) {
        BarDataSet barStepDataSet = new BarDataSet(dataVals, "Steps taken");
        barStepDataSet.setColor(Color.parseColor("#f1c232"));

        barStepDataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        barStepDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Math.round(value) + " steps";
            }
        });
        BarData barData = new BarData();
        barData.addDataSet(barStepDataSet);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(7);
        xAxis.setTypeface(Typeface.SERIF);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                String day;
                switch ((int) value) {
                    case 1:
                        day = getCalculatedDate("dd-MMM", -6);
                        break;
                    case 2:
                        day = getCalculatedDate("dd-MMM", -5);
                        break;
                    case 3:
                        day = getCalculatedDate("dd-MMM", -4);
                        break;
                    case 4:
                        day = getCalculatedDate("dd-MMM", -3);
                        break;
                    case 5:
                        day = getCalculatedDate("dd-MMM", -2);
                        break;
                    case 6:
                        day = getCalculatedDate("dd-MMM", -1);
                        break;
                    case 7:
                        day = getCalculatedDate("dd-MMM", 0);
                        break;
                    default:
                        day = String.valueOf(value);
                }
                return day;
            }
        });
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setTextSize(8);
        yAxisLeft.setTypeface(Typeface.SERIF);

        Legend legend = barChart.getLegend();
        legend.setTypeface(Typeface.SERIF);
        legend.setTextSize(8);

        Description desc = new Description();
        desc.setText("Steps data");
        desc.setTypeface(Typeface.SERIF);
        desc.setTextColor(Color.BLACK);
        barChart.setDescription(desc);
        barChart.animateXY(3000, 3000);
        barChart.setDrawBorders(true);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void displayLineChart(ArrayList<Entry> dataVals2) {

        LineDataSet lineDisDataSet2 = new LineDataSet(dataVals2, "Distance covered (km)");
        lineDisDataSet2.setCircleColor(Color.BLUE);
        lineDisDataSet2.setColor(Color.BLUE);

        lineDisDataSet2.setValueTypeface(Typeface.DEFAULT_BOLD);
        lineDisDataSet2.setValueTextSize(7);
        lineDisDataSet2.setCircleHoleColor(Color.BLUE);

        lineDisDataSet2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + " km";
            }
        });

        XAxis xAxis = lineChartDis.getXAxis();
        xAxis.setTextSize(7);
        xAxis.setTypeface(Typeface.SERIF);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                String day;
                switch ((int) value) {
                    case 1:
                        day = getCalculatedDate("dd-MMM", -6);
                        break;
                    case 2:
                        day = getCalculatedDate("dd-MMM", -5);
                        break;
                    case 3:
                        day = getCalculatedDate("dd-MMM", -4);
                        break;
                    case 4:
                        day = getCalculatedDate("dd-MMM", -3);
                        break;
                    case 5:
                        day = getCalculatedDate("dd-MMM", -2);
                        break;
                    case 6:
                        day = getCalculatedDate("dd-MMM", -1);
                        break;
                    case 7:
                        day = getCalculatedDate("dd-MMM", 0);
                        break;
                    default:
                        day = String.valueOf(value);
                }
                return day;
            }
        });
        YAxis yAxisRight = lineChartDis.getAxisRight();
        yAxisRight.setEnabled(false);
        YAxis yAxisLeft = lineChartDis.getAxisLeft();
        yAxisLeft.setEnabled(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDisDataSet2);

        Legend legend = lineChartDis.getLegend();
        legend.setEnabled(true);
        legend.setXEntrySpace(20);
        legend.setTypeface(Typeface.SERIF);
        legend.setFormToTextSpace(5);
        legend.setTextSize(8);
        legend.setForm(Legend.LegendForm.CIRCLE);

        Description desc = new Description();
        desc.setText("Calorie & Distance data");
        desc.setTypeface(Typeface.SERIF);
        desc.setTextColor(Color.BLACK);
        lineChartDis.setDescription(desc);

        LineData data = new LineData(dataSets);
        lineChartDis.animateXY(3000, 3000);
        lineChartDis.setData(data);
        lineChartDis.setDrawBorders(true);
        lineChartDis.setDrawGridBackground(true);
        lineChartDis.invalidate();
    }

    private void displayLineChart2(ArrayList<Entry> dataVals1) {
        LineDataSet lineCalDataSet1 = new LineDataSet(dataVals1, "Calories Burned (cal)");
        lineCalDataSet1.setCircleColor(Color.RED);
        lineCalDataSet1.setColor(Color.RED);
        lineCalDataSet1.setValueTypeface(Typeface.DEFAULT_BOLD);
        lineCalDataSet1.setValueTextSize(7);
        lineCalDataSet1.setCircleHoleColor(Color.RED);

        lineCalDataSet1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + " cal";
            }
        });

        XAxis xAxis = lineChartCal.getXAxis();
        xAxis.setTextSize(7);
        xAxis.setTypeface(Typeface.SERIF);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                String day;
                switch ((int) value) {
                    case 1:
                        day = getCalculatedDate("dd-MMM", -6);
                        break;
                    case 2:
                        day = getCalculatedDate("dd-MMM", -5);
                        break;
                    case 3:
                        day = getCalculatedDate("dd-MMM", -4);
                        break;
                    case 4:
                        day = getCalculatedDate("dd-MMM", -3);
                        break;
                    case 5:
                        day = getCalculatedDate("dd-MMM", -2);
                        break;
                    case 6:
                        day = getCalculatedDate("dd-MMM", -1);
                        break;
                    case 7:
                        day = getCalculatedDate("dd-MMM", 0);
                        break;
                    default:
                        day = String.valueOf(value);
                }
                return day;
            }
        });
        YAxis yAxisRight = lineChartCal.getAxisRight();
        yAxisRight.setEnabled(false);
        YAxis yAxisLeft = lineChartCal.getAxisLeft();
        yAxisLeft.setEnabled(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineCalDataSet1);

        Legend legend = lineChartCal.getLegend();
        legend.setEnabled(true);
        legend.setXEntrySpace(20);
        legend.setTypeface(Typeface.SERIF);
        legend.setFormToTextSpace(5);
        legend.setTextSize(8);
        legend.setForm(Legend.LegendForm.CIRCLE);

        Description desc = new Description();
        desc.setText("Calories Burnt Data");
        desc.setTypeface(Typeface.SERIF);
        desc.setTextColor(Color.BLACK);
        lineChartCal.setDescription(desc);

        LineData data = new LineData(dataSets);
        lineChartCal.animateXY(3000, 3000);
        lineChartCal.setData(data);
        lineChartCal.setDrawBorders(true);
        lineChartCal.setDrawGridBackground(true);
        lineChartCal.invalidate();
    }

    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    private void statTransit() {

        prev.setOnClickListener(v -> {
            if (cv1.getVisibility() == View.VISIBLE) {
                cv1.setVisibility(View.INVISIBLE);
                cv3.setVisibility(View.VISIBLE);
            } else if (cv2.getVisibility() == View.VISIBLE) {
                cv2.setVisibility(View.INVISIBLE);
                cv1.setVisibility(View.VISIBLE);
            } else if (cv3.getVisibility() == View.VISIBLE) {
                cv3.setVisibility(View.INVISIBLE);
                cv2.setVisibility(View.VISIBLE);
            }
        });

        next.setOnClickListener(v -> {
            if (cv1.getVisibility() == View.VISIBLE) {
                cv1.setVisibility(View.INVISIBLE);
                cv2.setVisibility(View.VISIBLE);
            } else if (cv2.getVisibility() == View.VISIBLE) {
                cv2.setVisibility(View.INVISIBLE);
                cv3.setVisibility(View.VISIBLE);
            } else if (cv3.getVisibility() == View.VISIBLE) {
                cv3.setVisibility(View.INVISIBLE);
                cv1.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fillList() {
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(Objects.requireNonNull(str));
        DatabaseReference ref23 = FirebaseDatabase.getInstance().getReference().child(id).child("info");
        ref23.get().addOnSuccessListener(dataSnapshot -> {
            init = Objects.requireNonNull(dataSnapshot.child("init").getValue()).toString();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd,MM,yyyy");
            try {
                initdate = sdf.parse(init);
                currdate = sdf.parse(date);
                assert currdate != null;
                long diff = currdate.getTime() - initdate.getTime();
                length = (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS))+1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("steps");
        ref.get().addOnSuccessListener(ds -> {
            String[] steps = new String[(int) length];
            String[] dis = new String[(int) length];
            String[] cals = new String[(int) length];
            String[] dates = new String[(int) length];

            int j = 0;
            for (int i = 0; i < length; i++) {
                dates[i] = getCalculatedDate("dd,MM,yyyy", j);
                if (ds.hasChild(dates[i])) {
                    steps[i] = Objects.requireNonNull(ds.child(dates[i]).child("s").getValue()).toString();
                    dis[i] = Objects.requireNonNull(ds.child(dates[i]).child("d").getValue()).toString();
                    cals[i] = Objects.requireNonNull(ds.child(dates[i]).child("c").getValue()).toString();
                } else {
                    steps[i] = "0";
                    dis[i] = "0";
                    cals[i] = "0";
                }

                DateFormat originalFormat = new SimpleDateFormat("dd,MM,yyyy", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd - MMMM - yyyy");
                Date date;
                try {
                    date = originalFormat.parse(dates[i]);
                    assert date != null;
                    dates[i] = targetFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                itemsModelList.add(new ItemsModel(dates[i], steps[i], dis[i], cals[i]));
                j--;
            }
            ActivityListAdapter adapter = new ActivityListAdapter(getActivity(), itemsModelList);
            listView.setAdapter(adapter);
        });
    }
}