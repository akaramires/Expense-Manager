/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import org.eatech.expense.R;
import org.eatech.expense.adapter.OperationAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.dao.OperationDao;
import org.eatech.expense.db.entities.OperationEntity;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FragmentOperations extends SherlockListFragment
{
    private static final String TAG = "Expense-" + FragmentOperations.class.getSimpleName();

    @InjectView(android.R.id.list)
    ListView listView;

    @InjectView(R.id.tvMonth)
    TextView tvMonth;

    @InjectView(R.id.tvIn)
    TextView tvIn;

    @InjectView(R.id.tvOut)
    TextView tvOut;

    private DatabaseHelper   dbHelper;
    private OperationDao     operationDao;
    private OperationAdapter operationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_operations_list, container, false);
        ButterKnife.inject(this, rootView);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        try {
            setVars();

            List<OperationEntity> operations = operationDao.getCurrentMonth();
            Log.i(TAG, "operations count=" + operations.size());
            for (OperationEntity operationEntity : operations) {
                operationAdapter.add(operationEntity);
            }

            listView.setAdapter(operationAdapter);

            GregorianCalendar gCal = new GregorianCalendar();
            tvMonth.setText(android.text.format.DateFormat.format("MMMM", gCal));

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

        List<OperationEntity> operations = operationDao.getCurrentMonth();
        Log.i(TAG, "operations count=" + operations.size());
        for (OperationEntity operationEntity : operations) {
            operationAdapter.add(operationEntity);
        }
        operationAdapter.notifyDataSetChanged();

        tvIn.setText("+" + operationDao.getInAtCurMonth());
        tvOut.setText("-" + operationDao.getOutAtCurMonth());
    }
}
