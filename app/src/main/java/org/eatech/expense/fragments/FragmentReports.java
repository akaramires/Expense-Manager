/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;

import org.eatech.expense.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FragmentReports extends Fragment
{
    @InjectView(R.id.chart)
    PieChart mChart;

    public static Fragment newInstance()
    {
        return new FragmentReports();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);
        ButterKnife.inject(this, rootView);

        mChart.setDescription("");
        mChart.setUsePercentValues(true);
        mChart.setCenterText("Quarterly\nRevenue");
        mChart.setCenterTextSize(22f);

        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(45f);
        mChart.setTransparentCircleRadius(50f);

        // enable / disable drawing of x- and y-values
        //        mChart.setDrawYValues(false);
        //        mChart.setDrawXValues(false);

        mChart.setData(generatePieData());

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * generates less data (1 DataSet, 4 values)
     *
     * @return
     */
    protected PieData generatePieData()
    {

        int count = 4;

        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Quarter 1");
        xVals.add("Quarter 2");
        xVals.add("Quarter 3");
        xVals.add("Quarter 4");

        entries1.add(new Entry(10, 0));

        entries1.add(new Entry(5, 1));

        entries1.add(new Entry(25, 2));

        entries1.add(new Entry(60, 3));

        PieDataSet ds1 = new PieDataSet(entries1, "Quarterly Revenues 2014");
        ds1.setColors(ColorTemplate.COLORFUL_COLORS);
        ds1.setSliceSpace(2f);

        PieData d = new PieData(xVals, ds1);
        return d;
    }
}
