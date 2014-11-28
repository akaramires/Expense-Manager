/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import org.eatech.expense.HelperDate;
import org.eatech.expense.MainActivity;
import org.eatech.expense.R;
import org.eatech.expense.adapter.OperationAdapter;
import org.eatech.expense.adapter.RangeDatePickerDialog;
import org.eatech.expense.adapter.SourceAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.dao.OperationDao;
import org.eatech.expense.db.entities.OperationEntity;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FragmentOperations extends SherlockListFragment implements
                                                             AdapterView.OnItemSelectedListener,
                                                             AdapterView.OnItemLongClickListener
{
    private static final String TAG = "Expense-" + FragmentOperations.class.getSimpleName();

    @InjectView(R.id.tvIn)
    TextView tvIn;

    @InjectView(R.id.tvOut)
    TextView tvOut;

    @InjectView(R.id.etStart)
    EditText etStart;

    @InjectView(R.id.etEnd)
    EditText etEnd;

    @InjectView(R.id.spinSource)
    Spinner spinSource;

    private DatabaseHelper              dbHelper;
    private OperationDao                operationDao;
    private OperationAdapter            operationAdapter;
    private SimpleDateFormat            dateFormatter;
    private RangeDatePickerDialog       dialogDatePickerEnd;
    private RangeDatePickerDialog       dialogDatePickerStart;
    private Calendar                    date_start;
    private Calendar                    date_end;
    private MainActivity                mainActivity;
    private SourceAdapter<SourceEntity> adapterSource;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_operations_list, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        try {
            dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

            initDateFilter();
            initVars();
            initAdapterSource();

            List<OperationEntity> operations = operationDao.getAllByPeriod(getSource().getId(), date_start.getTimeInMillis(), date_end.getTimeInMillis());
            Log.i(TAG, "operations count=" + operations.size());
            for (OperationEntity operationEntity : operations) {
                operationAdapter.add(operationEntity);
            }

            getListView().setAdapter(operationAdapter);
            getListView().setOnItemLongClickListener(this);

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
                initAdapterSource();
                setAdapter();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.clear();
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getSherlockActivity().getMenuInflater();
        inflater.inflate(R.menu.listview_actions, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo operation = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        OperationEntity operationEntity = operationAdapter.getItem(operation.position);

        switch (item.getItemId()) {
            case R.id.action_edit:
                mainActivity.tmp_oper_id = operationEntity.getId();
                mainActivity.mViewPager.setCurrentItem(0);
                return true;
            case R.id.action_delete:
                try {
                    operationDao.delete(operationEntity);
                    setAdapter();
                    Toast.makeText(getSherlockActivity(), getString(R.string.msgSuccessRemoveOperation), Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void initVars() throws SQLException
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
        initVars();

        if (!operationAdapter.isEmpty()) {
            operationAdapter.clear();
        }

        tvIn.setText("+ " + getSource().getCurrency().getSymbol_left() + operationDao.getInByPeriod(getSource().getId(), date_start.getTimeInMillis(), date_end.getTimeInMillis()) + getSource().getCurrency().getSymbol_right());
        tvOut.setText("- " + getSource().getCurrency().getSymbol_left() + operationDao.getOutByPeriod(getSource().getId(), date_start.getTimeInMillis(), date_end.getTimeInMillis()) + getSource().getCurrency().getSymbol_right());

        List<OperationEntity> operations = operationDao.getAllByPeriod(getSource().getId(), date_start.getTimeInMillis(), date_end.getTimeInMillis());
        Log.i(TAG, "operations count=" + operations.size());
        for (OperationEntity operationEntity : operations) {
            operationAdapter.add(operationEntity);
        }
        operationAdapter.notifyDataSetChanged();
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
                try {
                    setAdapter();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, date_end, date_start, null);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        try {
            setAdapter();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {
    }

    private SourceEntity getSource()
    {
        return adapterSource.getItem(spinSource.getSelectedItemPosition());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        final OperationEntity operation = operationAdapter.getItem(position);
        final CharSequence[] items = { getString(R.string.action_edit), getString(R.string.action_delete) };

        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setItems(items, new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int item)
            {
                switch (item) {
                    case 0:
                        mainActivity.tmp_oper_id = operation.getId();
                        mainActivity.mViewPager.setCurrentItem(2);
                        break;
                    case 1:
                        try {
                            SourceEntity source = dbHelper.getSourceDAO().queryForId(operation.getSource().getId());
                            double old = operation.getCount() * operation.getCost();
                            source.setSum_current(String.valueOf(source.getSum_current() + old));
                            dbHelper.getSourceDAO().update(source);

                            operationDao.delete(operation);
                            setAdapter();
                            Toast.makeText(getSherlockActivity(), getString(R.string.msgSuccessRemoveOperation), Toast.LENGTH_SHORT).show();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return false;
    }
}
