/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.eatech.expense.MainActivity;
import org.eatech.expense.R;
import org.eatech.expense.SourceFormActivity;
import org.eatech.expense.adapter.SourceAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.dao.SourceDao;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FragmentSource extends SherlockListFragment implements
                                                         AdapterView.OnItemLongClickListener
{
    private static final String TAG = "Expense-" + FragmentSource.class.getSimpleName();

    private SourceDao                   sourceDAO;
    private SourceAdapter<SourceEntity> sourceAdapter;
    private MainActivity                mainActivity;
    private DatabaseHelper              dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_sources_list, container, false);

        ButterKnife.inject(this, rootView);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        try {
            dbHelper = HelperFactory.getInstance().getHelper();
            sourceDAO = dbHelper.getSourceDAO();
            sourceAdapter = new SourceAdapter<SourceEntity>(getSherlockActivity());

            List<SourceEntity> sources = sourceDAO.getAll();
            Log.i(TAG, "source count=" + sources.size());
            for (SourceEntity source : sources) {
                sourceAdapter.add(source);
            }

            getListView().setAdapter(sourceAdapter);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data == null) {
            return;
        }

        int status = data.getIntExtra("status", -1);
        switch (status) {
            case 1:
                try {
                    fillList();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, com.actionbarsherlock.view.MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_source_list_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getSherlockActivity(), SourceFormActivity.class);
                intent.putExtra("isEdit", 0);
                startActivityForResult(intent, 1);
                break;
        }

        return true;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        final SourceEntity source = sourceAdapter.getItem(position);
        final CharSequence[] items = { getString(R.string.action_edit), getString(R.string.action_delete) };

        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setItems(items, new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int item)
            {
                switch (item) {
                    case 0:
                        Intent intent = new Intent(getSherlockActivity(), SourceFormActivity.class);
                        intent.putExtra("isEdit", source.getId());
                        startActivityForResult(intent, 1);
                        break;
                    case 1:
                        try {
                            if (dbHelper.getSourceDAO().countOf() < 2) {
                                Toast.makeText(getSherlockActivity(), getString(R.string.msgValidationRemoveLastSource), Toast.LENGTH_SHORT).show();
                            } else {
                                dbHelper.getSourceDAO().delete(source);

                                fillList();

                                Toast.makeText(getSherlockActivity(), getString(R.string.msgSuccessRemoveSource), Toast.LENGTH_SHORT).show();
                            }
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

    private void fillList() throws SQLException
    {
        if (sourceAdapter.getCount() > 0) {
            sourceAdapter.clear();
        }
        setListAdapter(null);

        List<SourceEntity> sources = sourceDAO.getAll();
        Log.i(TAG, "source count=" + sources.size());
        for (SourceEntity source : sources) {
            sourceAdapter.add(source);
        }

        setListAdapter(sourceAdapter);
    }

}
