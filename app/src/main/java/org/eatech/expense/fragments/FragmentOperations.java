/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.eatech.expense.HelperDate;
import org.eatech.expense.R;
import org.eatech.expense.SourceActivity;
import org.eatech.expense.adapter.OperationAdapter;
import org.eatech.expense.adapter.RangeDatePickerDialog;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.dao.OperationDao;
import org.eatech.expense.db.entities.OperationEntity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FragmentOperations extends SherlockListFragment
{
    private static final String TAG = "Expense-" + FragmentOperations.class.getSimpleName();

    @InjectView(android.R.id.list)
    ListView listView;

    @InjectView(R.id.tvIn)
    TextView tvIn;

    @InjectView(R.id.tvOut)
    TextView tvOut;

    @InjectView(R.id.etStart)
    EditText etStart;

    @InjectView(R.id.etEnd)
    EditText etEnd;

    private DatabaseHelper        dbHelper;
    private OperationDao          operationDao;
    private OperationAdapter      operationAdapter;
    private SimpleDateFormat      dateFormatter;
    private Calendar              current;
    private RangeDatePickerDialog dialogDatePickerEnd;
    private RangeDatePickerDialog dialogDatePickerStart;
    private Calendar              date_start;
    private Calendar              date_end;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_operations_list, container, false);
        ButterKnife.inject(this, rootView);

        //        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        try {
            dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            current = Calendar.getInstance();

            initDateFilter();
            setVars();

            List<OperationEntity> operations = operationDao.getAllByPeriod(date_start.getTimeInMillis(), date_end.getTimeInMillis());
            Log.i(TAG, "operations count=" + operations.size());
            for (OperationEntity operationEntity : operations) {
                operationAdapter.add(operationEntity);
            }

            listView.setAdapter(operationAdapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                setAdapter();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedState)
    {
        registerForContextMenu(listView);
        super.onActivityCreated(savedState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getSherlockActivity().getMenuInflater();
        inflater.inflate(R.menu.listview_actions, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo source = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        OperationEntity operationEntity = operationAdapter.getItem(source.position);

        switch (item.getItemId()) {
            case R.id.action_edit:
                return true;
            case R.id.action_delete:
                try {
                    operationDao.delete(operationEntity);
                    setAdapter();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setVars() throws SQLException
    {
        if (dbHelper == null) {
            dbHelper = HelperFactory.getInstance().getHelper();
        }

        if (operationDao == null) {
            operationDao = dbHelper.getOperationDAO();
        }
        if (operationAdapter == null) {
            operationAdapter = new OperationAdapter(getSherlockActivity());
        }
    }

    private void setAdapter() throws SQLException
    {
        setVars();

        if (!operationAdapter.isEmpty()) {
            operationAdapter.clear();
        }

        tvIn.setText("+" + operationDao.getInByPeriod(date_start.getTimeInMillis(), date_end.getTimeInMillis()));
        tvOut.setText("-" + operationDao.getOutByPeriod(date_start.getTimeInMillis(), date_end.getTimeInMillis()));

        List<OperationEntity> operations = operationDao.getAllByPeriod(date_start.getTimeInMillis(), date_end.getTimeInMillis());
        Log.i(TAG, "operations count=" + operations.size());
        for (OperationEntity operationEntity : operations) {
            operationAdapter.add(operationEntity);
        }
        operationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, com.actionbarsherlock.view.MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_calendar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menuItemCancel:

                break;
        }

        return true;
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
                try {
                    setAdapter();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, current, null, date_end);

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
                try {
                    setAdapter();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, current, date_start, null);
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
}
