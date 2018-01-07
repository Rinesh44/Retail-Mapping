package com.example.android.retailmapping;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;

import java.util.ArrayList;

/**
 * Created by Shaakya on 9/4/2017.
 */

public class AdminHomeFragment extends Fragment{


    public AdminHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //fragment needs a view.so defining it and finding the layout as a container
        View rootView = inflater.inflate(R.layout.activity_admin_home_fragment, container, false);
        PieChart pieChart = (PieChart)rootView.findViewById(R.id.piechart);

        ArrayList<Entry> yvalues = new ArrayList();
        /*
        yvalues.add(new Entry(Float.valueOf(LoginActivity.samsungCount),0));
        yvalues.add(new Entry(Float.valueOf(LoginActivity.huaweiCount),1));
        yvalues.add(new Entry(Float.valueOf(LoginActivity.sonyCount),2));
        yvalues.add(new Entry(Float.valueOf(LoginActivity.colorsCount),3));
        */
        for(int i = 0; i <= LoginActivity.brands.size() - 1; i++) {
            yvalues.add(new Entry((Float) LoginActivity.brands.get(i).get("count"), i));
        }

        PieDataSet dataSet = new PieDataSet(yvalues, "");

        ArrayList<Integer> colors = new ArrayList<>();
        for(int i = 0; i <= LoginActivity.brands.size() - 1; i++){
            colors.add(Color.parseColor(String.valueOf(LoginActivity.brands.get(i).get("color"))));
        }
/*
        colors.add(getResources().getColor(R.color.huawei));
        colors.add(getResources().getColor(R.color.sony));
        colors.add(getResources().getColor(R.color.colors));
        */

        dataSet.setColors(colors);

        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 0; i <= LoginActivity.brands.size() - 1; i++){
            xVals.add(String.valueOf(LoginActivity.brands.get(i).get("name")));
        }
     /*
        xVals.add("Huawei");
        xVals.add("Sony");
        xVals.add("Colors");
        */

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new DefaultValueFormatter(0));
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        pieChart.setDescription("");
        pieChart.animateXY(1400,1400);

        return rootView;
    }


}
