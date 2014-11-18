package org.eatech.expense;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.eatech.expense.adapter.SourceAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SourceActivity extends SherlockFragmentActivity
{
    @InjectView(android.R.id.list)
    ListView listView;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        ButterKnife.inject(this);

        getSupportActionBar().setTitle(getString(R.string.screen_sources));

        dbHelper = HelperFactory.getInstance().getHelper();

        fillList();
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
                Intent intent = new Intent(this, SourceFormActivity.class);
                startActivityForResult(intent, 1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
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
                fillList();
                break;
        }
    }

    private void fillList()
    {
        try {
            SourceAdapter<SourceEntity> sourceAdapter = new SourceAdapter<SourceEntity>(this);
            List<SourceEntity> sources = dbHelper.getSourceDAO().getAll();

            if (sources.size() > 0) {
                for (SourceEntity source : sources) {
                    sourceAdapter.add(source);
                }

                listView.setAdapter(null);
                listView.setAdapter(sourceAdapter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
