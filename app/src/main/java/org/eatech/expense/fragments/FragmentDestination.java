/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.eatech.expense.CategoryFormActivity;
import org.eatech.expense.MainActivity;
import org.eatech.expense.R;
import org.eatech.expense.SourceFormActivity;
import org.eatech.expense.adapter.CategoryAdapter;
import org.eatech.expense.adapter.CategoryDestionations;
import org.eatech.expense.adapter.DestinationAdapter;
import org.eatech.expense.adapter.ExpListAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.dao.CategoryDao;
import org.eatech.expense.db.dao.DestinationDao;
import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.DestinationEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FragmentDestination extends SherlockFragment implements
                                                          ExpandableListView.OnGroupClickListener,
                                                          AdapterView.OnItemLongClickListener
{
    private static final String TAG = "Expense-" + FragmentDestination.class.getSimpleName();

    @InjectView(R.id.elvDestinations)
    ExpandableListView elvDestinations;

    private DatabaseHelper                        dbHelper;
    private DestinationDao                        destinationDao;
    private CategoryDao                           categoryDao;
    private CategoryAdapter<CategoryEntity>       categoryAdapter;
    private DestinationAdapter<DestinationEntity> destinationAdapter;
    private MainActivity                          mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_destinations_list, container, false);

        ButterKnife.inject(this, rootView);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        try {
            dbHelper = HelperFactory.getInstance().getHelper();
            destinationDao = dbHelper.getDestinationDAO();
            categoryDao = dbHelper.getCategoryDAO();
            categoryAdapter = new CategoryAdapter<CategoryEntity>(getSherlockActivity());
            destinationAdapter = new DestinationAdapter<DestinationEntity>(getSherlockActivity());

            fillList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        /*if (isVisibleToUser) {
        }*/
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
            case -1:
                break;
            case 0:
                break;
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
        inflater.inflate(R.menu.menu_destination, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getSherlockActivity(), CategoryFormActivity.class);
                intent.putExtra("isEdit", 0);
                startActivityForResult(intent, 1);
                break;
            case R.id.action_add_category:
                break;
        }

        return true;
    }

    private void fillList() throws SQLException
    {
        List<CategoryEntity> catsEntList = categoryDao.getAll();
        List<CategoryDestionations> categoryDestionationses = new ArrayList<CategoryDestionations>();

        for (CategoryEntity cat : catsEntList) {
            CategoryDestionations tmpCat = new CategoryDestionations(cat);

            for (DestinationEntity destination : cat.getDestinations()) {
                tmpCat.addDestination(destination);
            }

            categoryDestionationses.add(tmpCat);
        }

        ExpListAdapter adapter = new ExpListAdapter(getSherlockActivity(), categoryDestionationses);

        elvDestinations.setAdapter(adapter);
        elvDestinations.setOnGroupClickListener(this);
        elvDestinations.setOnItemLongClickListener(this);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            elvDestinations.expandGroup(i);
        }
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int position,
                                long l)
    {
        elvDestinations.expandGroup(position);
        return true;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        int itemType = ExpandableListView.getPackedPositionType(id);

        int childPosition;
        int groupPosition;
        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            childPosition = ExpandableListView.getPackedPositionChild(id);
            groupPosition = ExpandableListView.getPackedPositionGroup(id);

            Log.i(TAG, "groupPosition=" + groupPosition);
            Log.i(TAG, "childPosition=" + childPosition);
            return true;

        } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            groupPosition = ExpandableListView.getPackedPositionGroup(id);

            Log.i(TAG, "groupPosition=" + groupPosition);
            return true;
        } else {

            return false;
        }
    }
}
