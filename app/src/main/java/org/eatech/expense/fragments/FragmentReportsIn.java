/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;

import org.eatech.expense.HelperDate;
import org.eatech.expense.R;
import org.eatech.expense.adapter.RangeDatePickerDialog;
import org.eatech.expense.adapter.SourceAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.dao.OperationDao;
import org.eatech.expense.db.entities.OperationEntity;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FragmentReportsIn extends SherlockFragment implements
                                                        AdapterView.OnItemSelectedListener
{
    private static final String TAG = "Expense-" + FragmentReportsIn.class.getSimpleName();

    @InjectView(R.id.etStart)
    EditText etStart;

    @InjectView(R.id.etEnd)
    EditText etEnd;

    @InjectView(R.id.spinSource)
    Spinner spinSource;

    @InjectView(R.id.chartIn)
    PieChart mChartIn;

    private SimpleDateFormat            dateFormatter;
    private Calendar                    date_start;
    private Calendar                    date_end;
    private RangeDatePickerDialog       dialogDatePickerStart;
    private RangeDatePickerDialog       dialogDatePickerEnd;
    private SourceAdapter<SourceEntity> adapterSource;
    private DatabaseHelper              dbHelper;
    private OperationDao                operationDao;

    public static Fragment newInstance()
    {
        return new FragmentReportsIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_reports_in, container, false);
        ButterKnife.inject(this, rootView);

        try {
            dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            dbHelper = HelperFactory.getInstance().getHelper();
            operationDao = dbHelper.getOperationDAO();

            initDateFilter();
            initAdapterSource();

            mChartIn.setDescription("");
            mChartIn.setUsePercentValues(false);
            mChartIn.setCenterText("Доходы");
            mChartIn.setCenterTextSize(22f);
            mChartIn.setHoleRadius(45f);
            mChartIn.setTransparentCircleRadius(50f);
            mChartIn.setData(getData());

            Legend l = mChartIn.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @OnClick({ R.id.etStart, R.id.etEnd })
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.etStart:
                dialogDatePickerStart.show();
                break;
            case R.id.etEnd:
                dialogDatePickerEnd.show();
                break;
        }
    }

    protected PieData getData() throws SQLException
    {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        SourceEntity source = adapterSource.getItem(spinSource.getSelectedItemPosition());

        List<OperationEntity> operations = operationDao
            .getAllByPeriodBuilder(date_start.getTimeInMillis(), date_end.getTimeInMillis())
            .and().eq(OperationEntity.COL_SOURCE_ID, source.getId())
            .and().eq(OperationEntity.COL_TYPE, "in")
            .query();

        HashMap<Integer, String> chartDataTitles = new HashMap<Integer, String>();
        HashMap<Integer, Double> chartDataValues = new HashMap<Integer, Double>();
        for (OperationEntity operation : operations) {
            int key = operation.getDestination().getId();
            String title = operation.getDestination().getTitle();
            double sum = operation.getCount() * operation.getCost();

            if (chartDataValues.containsKey(key)) {
                chartDataValues.put(key, chartDataValues.get(key) + sum);
            } else {
                chartDataValues.put(key, sum);
            }

            chartDataTitles.put(key, title);
        }

        int index = 0;
        for (Map.Entry<Integer, Double> entry : chartDataValues.entrySet()) {
            xVals.add(chartDataTitles.get(entry.getKey()));
            entries.add(new Entry(Float.parseFloat(entry.getValue().toString()), index));

            index++;
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(ColorTemplate.COLORFUL_COLORS);
        ds.setSliceSpace(2f);

        return new PieData(xVals, ds);
    }

    private void invalidateChart()
    {
        try {
            mChartIn.setData(getData());
            mChartIn.invalidate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initAdapterSource() throws SQLException
    {
        adapterSource = new SourceAdapter<SourceEntity>(getSherlockActivity());
        adapterSource.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        List<SourceEntity> sourceEntityList = dbHelper.getSourceDAO().getAll();
        for (SourceEntity srcEntity : sourceEntityList) {
            adapterSource.add(srcEntity);
        }

        spinSource.setAdapter(adapterSource);
        spinSource.setSelection(0);
        spinSource.setOnItemSelectedListener(this);
    }

    private void initDateFilter()
    {
        date_start = HelperDate.getStartCurrentMonthCal();
        date_end = HelperDate.getEndCurrentMonthCal();

        etStart.setText(dateFormatter.format(date_start.getTimeInMillis()));
        etStart.setFocusable(false);
        dialogDatePickerStart = new RangeDatePickerDialog(getSherlockActivity(), new DatePickerDialog.OnDateSetListener()
        {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_start = dialogDatePickerEnd.minDate = newDate;
                etStart.setText(dateFormatter.format(date_start.getTime()));

                invalidateChart();
            }
        }, date_start, null, date_end);

        etEnd.setText(dateFormatter.format(date_end.getTimeInMillis()));
        etEnd.setFocusable(false);
        dialogDatePickerEnd = new RangeDatePickerDialog(getSherlockActivity(), new DatePickerDialog.OnDateSetListener()
        {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_end = dialogDatePickerStart.maxDate = newDate;
                etEnd.setText(dateFormatter.format(date_end.getTime()));

                invalidateChart();
            }
        }, date_end, date_start, null);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        invalidateChart();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
}
