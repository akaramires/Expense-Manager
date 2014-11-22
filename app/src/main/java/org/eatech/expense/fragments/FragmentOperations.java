/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

import org.eatech.expense.R;
import org.eatech.expense.adapter.OperationAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.dao.OperationDao;
import org.eatech.expense.db.entities.OperationEntity;

import java.sql.SQLException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FragmentOperations extends SherlockListFragment
{
    private static final String TAG = "Expense-" + FragmentOperations.class.getSimpleName();

    @InjectView(android.R.id.list)
    ListView listView;

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
            dbHelper = HelperFactory.getInstance().getHelper();
            operationDao = dbHelper.getOperationDAO();
            operationAdapter = new OperationAdapter(getSherlockActivity());

            List<OperationEntity> operations = operationDao.getCurrentMonth();
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

    private void setAdapter() throws SQLException
    {
        if (!operationAdapter.isEmpty()) {
            operationAdapter.clear();
        }

        List<OperationEntity> operations = operationDao.getCurrentMonth();
        Log.i(TAG, "operations count=" + operations.size());
        for (OperationEntity operationEntity : operations) {
            operationAdapter.add(operationEntity);
        }
        operationAdapter.notifyDataSetChanged();
    }
}
