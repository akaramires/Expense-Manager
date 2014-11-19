package org.eatech.expense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
    private static final String TAG = "Expense-" + SourceActivity.class.getSimpleName();

    @InjectView(android.R.id.list)
    ListView listView;

    private DatabaseHelper              dbHelper;
    private SourceAdapter<SourceEntity> sourceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        ButterKnife.inject(this);

        getSupportActionBar().setTitle(getString(R.string.screen_sources));

        dbHelper = HelperFactory.getInstance().getHelper();

        fillList();

        registerForContextMenu(listView);
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
                intent.putExtra("isEdit", 0);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listview_actions, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo source = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        SourceEntity sourceEntity = sourceAdapter.getItem(source.position);

        switch (item.getItemId()) {
            case R.id.action_edit:
                Log.i(TAG, "edit");

                Intent intent = new Intent(this, SourceFormActivity.class);
                intent.putExtra("isEdit", sourceEntity.getId());
                startActivityForResult(intent, 1);

                return true;
            case R.id.action_delete:
                try {
                    if (dbHelper.getSourceDAO().countOf() < 2) {
                        Toast.makeText(this, getString(R.string.msgValidationRemoveLastSource), Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.getSourceDAO().delete(sourceEntity);

                        fillList();

                        Toast.makeText(this, getString(R.string.msgSuccessRemoveSource), Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void fillList()
    {
        try {
            sourceAdapter = new SourceAdapter<SourceEntity>(this);
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
