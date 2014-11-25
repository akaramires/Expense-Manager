package org.eatech.expense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.CurrencyEntity;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CategoryFormActivity extends SherlockFragmentActivity implements
                                                                   Validator.ValidationListener
{
    private static final String TAG = "Expense-" + CategoryFormActivity.class.getSimpleName();

    @InjectView(R.id.etTitle)
    @Required(order = 1, messageResId = R.string.msgValidationTitle)
    TextView etTitle;

    private Validator      validator;
    private DatabaseHelper dbHelper;
    private CategoryDao    categoryDao;
    private int            edit_id;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_form);
        getSupportActionBar().setTitle(R.string.screen_category_new);
        ButterKnife.inject(this);

        try {
            dbHelper = HelperFactory.getInstance().getHelper();
            categoryDao = dbHelper.getCategoryDAO();

            Intent intent = getIntent();
            edit_id = intent.getIntExtra("isEdit", -1);

            setupForm();

            validator = new Validator(this);
            validator.setValidationListener(this);

        } catch (SQLException e) {
            e.printStackTrace();
        }

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
        if (edit_id > 0) {
            CategoryEntity categoryEntity = categoryDao.queryForId(edit_id);
            etTitle.setText(categoryEntity.getTitle());
        }
    }

    private void clearForm()
    {
        etTitle.setText("");
    }

    @Override
    public void onValidationSucceeded()
    {
        String title = etTitle.getText().toString();

        try {
            if (edit_id > 0) {
                CategoryEntity categoryToUpdate = new CategoryEntity();
                categoryToUpdate.setTitle(title);

                categoryDao.update(categoryToUpdate);
                Toast.makeText(this, getString(R.string.msgSuccessUpdateCategory), Toast.LENGTH_SHORT).show();
            } else {
                CategoryEntity categoryToAdd = new CategoryEntity();
                categoryToAdd.setTitle(title);
                categoryToAdd.setEditable();
                categoryToAdd.setCreated_at();

                categoryDao.create(categoryToAdd);
                Toast.makeText(this, getString(R.string.msgSuccessAddCategory), Toast.LENGTH_SHORT).show();
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
        } else {
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
