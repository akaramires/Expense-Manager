package org.eatech.expense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.Required;

import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.dao.CategoryDao;
import org.eatech.expense.db.dao.DestinationDao;
import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.CurrencyEntity;
import org.eatech.expense.db.entities.DestinationEntity;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DestinationFormActivity extends SherlockFragmentActivity implements
                                                                      Validator.ValidationListener
{
    private static final String TAG = "Expense-" + DestinationFormActivity.class.getSimpleName();

    @InjectView(R.id.spinCategory)
    Spinner spinCategory;

    @InjectView(R.id.spinType)
    Spinner spinType;

    @InjectView(R.id.etTitle)
    @Required(order = 1, messageResId = R.string.msgValidationTitle)
    TextView etTitle;

    private Validator                    validator;
    private DatabaseHelper               dbHelper;
    private CategoryDao                  categoryDao;
    private DestinationDao               destinationDao;
    private ArrayAdapter<CategoryEntity> adapterCategory;
    private int                          edit_id;
    private ArrayAdapter<String>         adapterType;
    private String current_type = "out";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_form);
        getSupportActionBar().setTitle(R.string.screen_source_new);
        ButterKnife.inject(this);

        try {
            dbHelper = HelperFactory.getInstance().getHelper();
            categoryDao = dbHelper.getCategoryDAO();
            destinationDao = dbHelper.getDestinationDAO();

            Intent intent = getIntent();
            edit_id = intent.getIntExtra("isEdit", -1);

            setupForm();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportMenuInflater().inflate(R.menu.menu_form, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menuItemSave:
                validator.validateAsync();
                break;
            case R.id.menuItemCancel:
                closeForm(0);
                break;
        }

        return true;
    }

    private void setupForm() throws SQLException
    {
        int id = -1;
        if (edit_id > 0) {
            DestinationEntity destinationEntity = destinationDao.queryForId(edit_id);
            etTitle.setText(destinationEntity.getTitle());
            id = destinationEntity.getCategory().getId();
        }
        setupTypeAdapter();
        setupCategoryAdapter(id);
    }

    private void clearForm()
    {
        spinCategory.setSelection(0);
        spinType.setSelection(0);
        etTitle.setText("");
    }

    private void setupTypeAdapter()
    {
        adapterType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        final String[] types = getResources().getStringArray(R.array.lblTypes);
        for (String type : types) {
            adapterType.add(type);
        }

        spinType.setAdapter(adapterType);
        spinType.setSelection(0);
        spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id)
            {
                String curType = spinType.getAdapter().getItem(position).toString();
                if (types[0].equals(curType)) {
                    current_type = "out";
                } else if (types[1].equals(curType)) {
                    current_type = "in";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }
        });
    }

    private void setupCategoryAdapter(int id) throws SQLException
    {
        int selected = 0;

        ArrayList<CategoryEntity> catsList = new ArrayList<CategoryEntity>();

        int index = 0;
        List<CategoryEntity> cats = categoryDao.getAll();
        for (CategoryEntity category : cats) {
            if (id > 0 && category.getId() == id) {
                selected = index;
            }
            catsList.add(category);
            index++;
        }

        adapterCategory = new ArrayAdapter<CategoryEntity>(this, android.R.layout.simple_spinner_item, catsList);
        adapterCategory.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        spinCategory.setAdapter(adapterCategory);
        spinCategory.setSelection(selected);

    }

    @Override
    public void onValidationSucceeded()
    {
        int pos = spinCategory.getSelectedItemPosition();
        int catgeoryId = adapterCategory.getItem(pos).getId();
        String title = etTitle.getText().toString();

        CategoryEntity category = new CategoryEntity();
        category.setId(catgeoryId);

        try {
            if (edit_id > 0) {
                //                destinationDao.update(new DestinationEntity(edit_id, title, sum, sum, currencyEntity));
                Toast.makeText(this, getString(R.string.msgSuccessUpdateSource), Toast.LENGTH_SHORT).show();
            } else {
                DestinationEntity destination = new DestinationEntity();
                destination.setTitle(title);
                destination.setCategory(category);
                destination.setEditable();
                destination.setCreated_at();
                destination.setType(current_type);

                destinationDao.create(destination);
                Toast.makeText(this, getString(R.string.msgSuccessAddDestination), Toast.LENGTH_SHORT).show();
            }

            closeForm(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule)
    {
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else if (failedView instanceof Spinner) {
            ViewGroup parent = (ViewGroup) failedView.getParent();

            switch (failedView.getId()) {
                case R.id.spinDestination:
                    EditText etDestination = (EditText) parent.findViewById(R.id.etDestination);
                    etDestination.requestFocus();
                    etDestination.setError(message);
                    break;
            }
            Log.e(TAG, message);
        }
    }

    private void closeForm(int status)
    {
        clearForm();

        Intent intent = new Intent();
        intent.putExtra("status", status);
        setResult(RESULT_OK, intent);
        finish();
    }
}
