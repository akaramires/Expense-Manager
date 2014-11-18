package org.eatech.expense;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.eatech.expense.adapter.SourceAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SourceActivity extends SherlockListActivity
{

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        getSupportActionBar().setTitle(getString(R.string.screen_sources));

        dbHelper = HelperFactory.getInstance().getHelper();

        try {
            SourceAdapter<SourceEntity> sourceAdapter = new SourceAdapter<SourceEntity>(this);
            List<SourceEntity> sources = dbHelper.getSourceDAO().getAll();

            if (sources.size() > 0) {
                for (SourceEntity source : sources) {
                    sourceAdapter.add(source);
                }

                setListAdapter(sourceAdapter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportMenuInflater().inflate(R.menu.menu_source_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_add:

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
