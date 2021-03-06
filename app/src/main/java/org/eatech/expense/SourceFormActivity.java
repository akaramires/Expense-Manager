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
import org.eatech.expense.db.entities.CurrencyEntity;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SourceFormActivity extends SherlockFragmentActivity implements
                                                                 Validator.ValidationListener
{
    private static final String TAG = "Expense-" + SourceFormActivity.class.getSimpleName();

    @InjectView(R.id.etTitle)
    @Required(order = 1, messageResId = R.string.msgValidationTitle)
    TextView etTitle;

    @InjectView(R.id.spinCurrency)
    Spinner spinCurrency;

    @InjectView(R.id.etSum)
    @Required(order = 2, messageResId = R.string.msgValidationSum)
    @NumberRule(order = 3, type = NumberRule.NumberType.DOUBLE, gt = 0.01, messageResId = R.string.msgValidationSum)
    EditText etSum;

    private Validator                    validator;
    private DatabaseHelper               dbHelper;
    private ArrayAdapter<CurrencyEntity> adapterCurrency;
    private int                          edit_id;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_form);
        getSupportActionBar().setTitle(R.string.screen_source_new);
        ButterKnife.inject(this);

        dbHelper = HelperFactory.getInstance().getHelper();

        Intent intent = getIntent();

        edit_id = intent.getIntExtra("isEdit", -1);

        try {
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
            SourceEntity sourceEntity = dbHelper.getSourceDAO().queryForId(edit_id);
            etTitle.setText(sourceEntity.getTitle());
            etSum.setText(sourceEntity.getSum_current());
            id = sourceEntity.getCurrency().getId();
        }
        setupCurrencyAdapter(id);
    }

    private void clearForm()
    {
        etTitle.setText("");
        etSum.setText("");
        spinCurrency.setSelection(0);
    }

    private void setupCurrencyAdapter(int id) throws SQLException
    {
        int selected = 0;

        ArrayList<CurrencyEntity> currencyList = new ArrayList<CurrencyEntity>();

        int index = 0;
        List<CurrencyEntity> currencies = dbHelper.getCurrencyDAO().getAll();
        for (CurrencyEntity currency : currencies) {
            if (id > 0 && currency.getId() == id) {
                selected = index;
            }
            currencyList.add(currency);
            index++;
        }

        adapterCurrency = new ArrayAdapter<CurrencyEntity>(this, android.R.layout.simple_spinner_item, currencyList);
        adapterCurrency.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        spinCurrency.setAdapter(adapterCurrency);
        spinCurrency.setSelection(selected);

    }

    @Override
    public void onValidationSucceeded()
    {
        int pos = spinCurrency.getSelectedItemPosition();
        int currencyId = adapterCurrency.getItem(pos).getId();
        String title = etTitle.getText().toString();
        String sum = etSum.getText().toString();

        CurrencyEntity currencyEntity = new CurrencyEntity();
        currencyEntity.setId(currencyId);

        try {
            if (edit_id > 0) {
                dbHelper.getSourceDAO().update(new SourceEntity(edit_id, title, sum, sum, currencyEntity));
                Toast.makeText(this, getString(R.string.msgSuccessUpdateSource), Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.getSourceDAO().create(new SourceEntity(title, sum, sum, currencyEntity));
                Toast.makeText(this, getString(R.string.msgSuccessAddSource), Toast.LENGTH_SHORT).show();
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
                case R.id.spinCurrency:
                    EditText etCurrency = (EditText) parent.findViewById(R.id.etCurrency);
                    etCurrency.requestFocus();
                    etCurrency.setError(message);
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
