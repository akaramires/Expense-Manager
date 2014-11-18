package org.eatech.expense;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.Select;

import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.entities.CurrencyEntity;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SourceFormActivity extends SherlockFragmentActivity
{
    @InjectView(R.id.etTitle)
    @Required(order = 1, messageResId = R.string.msgValidationTitle)
    TextView etTitle;

    @InjectView(R.id.spinCurrency)
    Spinner spinCurrency;

    @InjectView(R.id.etSum)
    @Required(order = 2)
    @NumberRule(order = 3, type = NumberRule.NumberType.DOUBLE, gt = 0.01, messageResId = R.string.msgValidationSum)
    EditText etSum;

    private Validator      validator;
    private DatabaseHelper dbHelper;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_form);
        getSupportActionBar().setTitle(R.string.screen_source_new);
        ButterKnife.inject(this);

        dbHelper = HelperFactory.getInstance().getHelper();

        try {
            setupForm();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        validator = new Validator(this);
        validator.setValidationListener(new SourceFormActivityValidator());
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
                clearForm();

                Intent intent = new Intent();
                intent.putExtra("status", 0);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }

        return true;
    }

    private void setupForm() throws SQLException
    {
        setupCurrencyAdapter();
    }

    private void clearForm()
    {
        etTitle.setText("");
        etSum.setText("");
        spinCurrency.setSelection(0);
    }

    private void setupCurrencyAdapter() throws SQLException
    {
        ArrayList<String> currencyList = new ArrayList<String>();

        List<CurrencyEntity> currencies = dbHelper.getCurrencyDAO().getAll();
        for (CurrencyEntity currency : currencies) {
            currencyList.add(currency.getTitle() + " - " + currency.getCode());
        }

        ArrayAdapter<String> adapterSource = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyList);
        adapterSource.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        spinCurrency.setAdapter(adapterSource);
        spinCurrency.setSelection(0);

    }
}
