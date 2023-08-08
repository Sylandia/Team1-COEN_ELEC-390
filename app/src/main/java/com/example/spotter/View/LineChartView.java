package com.example.spotter.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.spotter.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineChartView extends AppCompatActivity {

    private LineChart lineChart;
    private List<String> xValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        getSupportActionBar().setTitle("IMU Chart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lineChart = findViewById(R.id.LineChart_view);

        Description description = new Description();
        description.setText("IMU Readings");
        description.setPosition(350f, 15f); //will need to change
        description.setTextColor(Color.WHITE);
        description.setTextSize(14f);
        lineChart.setDescription(description);
        lineChart.setPinchZoom(true);
        lineChart.setTouchEnabled(true);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisRight().setEnabled(false);

        //Get values from Firebase

        xValues = Arrays.asList("1", "2", "3", "4", "5", "6", "7");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(xValues.size());
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);


        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);
        yAxis.setTextColor(Color.WHITE);

        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.WHITE);

        List <Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 10f));
        entries1.add(new Entry(1, 20f));
        entries1.add(new Entry(2, 45f));
        entries1.add(new Entry(3, 66f));
        entries1.add(new Entry(4, 69f));
        entries1.add(new Entry(5, 42f));
        entries1.add(new Entry(6, 30f));

        List <Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(0, 33f));
        entries2.add(new Entry(1, 23f));
        entries2.add(new Entry(2, 43f));
        entries2.add(new Entry(3, 54f));
        entries2.add(new Entry(4, 65f));
        entries2.add(new Entry(5, 83f));
        entries2.add(new Entry(6, 24f));

        LineDataSet lineDataSet1 = new LineDataSet(entries1, "IMU1");
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setDrawValues(false);

        LineDataSet lineDataSet2 = new LineDataSet(entries2, "IMU2");
        lineDataSet2.setColor(Color.RED);
        lineDataSet2.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet1, lineDataSet2);

        lineChart.setData(lineData);
        lineData.setValueTextColor(Color.WHITE);

        lineChart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info_bar, menu);
        return true;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Home button clicked
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_info_chart_activity) {
            FragmentManager fm = getSupportFragmentManager();
            InfoChartFragment hp = new InfoChartFragment();
            hp.show(fm, "fragment_info_chart");
            return true;
        }
        return(super.onOptionsItemSelected(item));
    }
}