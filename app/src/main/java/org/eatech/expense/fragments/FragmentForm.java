/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.Select;

import org.eatech.expense.R;
import org.eatech.expense.SourceActivity;
import org.eatech.expense.adapter.RangeDatePickerDialog;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FragmentForm extends SherlockFragment implements Validator.ValidationListener
{
    private static final String TAG = "Expense-" + FragmentForm.class.getSimpleName();

    @InjectView(R.id.spinType)
    Spinner spinType;

    @InjectView(R.id.etDate)
    EditText etDate;

    @InjectView(R.id.spinSource)
    @Select(order = 1, defaultSelection = 0, messageResId = R.string.msgValidationSource)
    Spinner spinSource;

    @InjectView(R.id.spinDestination)
    @Select(order = 2, defaultSelection = 0, messageResId = R.string.msgValidationDestination)
    Spinner spinDestination;

    @InjectView(R.id.etCount)
    @Required(order = 3)
    @NumberRule(order = 4, type = NumberRule.NumberType.INTEGER, gt = 1, messageResId = R.string.msgValidationCount)
    EditText etCount;

    @InjectView(R.id.etCost)
    @Required(order = 5)
    @NumberRule(order = 6, type = NumberRule.NumberType.DOUBLE, gt = 0.01, messageResId = R.string.msgValidationCost)
    EditText etCost;

    @InjectView(R.id.etComment)
    EditText etComment;

    @InjectView(R.id.ibSource)
    ImageButton ibSource;

    @InjectView(R.id.ibDestination)
    ImageButton ibDestination;

    private SimpleDateFormat dateFormatter;
    private Calendar         maxDate;
    private Calendar         current;

    private DatePickerDialog datePickerDialog;
    private Validator        validator;
    private DatabaseHelper   dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_form, container, false);
        ButterKnife.inject(this, rootView);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        validator = new Validator(this);
        validator.setValidationListener(this);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        current = maxDate = Calendar.getInstance();

        dbHelper = HelperFactory.getInstance().getHelper();

        try {
            setupForm();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_form, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                break;
        }

        return true;
    }

    @OnClick({ R.id.etDate, R.id.ibSource })
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.etDate:
                datePickerDialog.show();
                break;
            case R.id.ibSource:
                Intent intent = new Intent(getSherlockActivity(), SourceActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.ibDestination:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            setupSourceAdapter();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupForm() throws SQLException
    {
        setupTypeAdapter();
        setupSourceAdapter();
        setupDestinationAdapter();
        setupDate();
    }

    private void clearForm()
    {
        spinType.setSelection(0);
        spinSource.setSelection(0);
        spinDestination.setSelection(0);
        etDate.setText(dateFormatter.format(new Date().getTime()));
        etCount.setText("");
        etCost.setText("");
        etComment.setText("");
    }

    private void setupSourceAdapter() throws SQLException
    {
        ArrayList<String> sourceList = new ArrayList<String>();
        sourceList.add(getString(R.string.msgValidationSource));

        List<SourceEntity> sources = dbHelper.getSourceDAO().getAll();
        for (SourceEntity source : sources) {
            sourceList.add(source.getTitle());
        }

        ArrayAdapter<String> adapterSource = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, sourceList);
        adapterSource.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        spinSource.setAdapter(null);
        spinSource.setAdapter(adapterSource);
        spinSource.setSelection(0);

    }

    private void setupTypeAdapter()
    {
        String[] typesList = getResources().getStringArray(R.array.lblTypes);

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, typesList);
        adapterType.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        spinType.setAdapter(adapterType);
        spinType.setSelection(0);
    }

    private void setupDestinationAdapter() throws SQLException
    {
        ArrayList<String> destinationList = new ArrayList<String>();
        destinationList.add(getString(R.string.msgValidationDestination));

        List<CategoryEntity> categories = dbHelper.getCategoryDAO().getAll();
        for (CategoryEntity category : categories) {
            destinationList.add(category.getTitle());
        }

        ArrayAdapter<String> adapterDestination = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, destinationList);
        adapterDestination.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        spinDestination.setAdapter(adapterDestination);
        spinDestination.setSelection(0);
    }

    private void setupDate()
    {
        etDate.setText(dateFormatter.format(new Date().getTime()));
        datePickerDialog = new RangeDatePickerDialog(getSherlockActivity(), new DatePickerDialog.OnDateSetListener()
        {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etDate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, current, null, maxDate);
    }

    @Override
    public void onValidationSucceeded()
    {
        Log.i(TAG, "YES!!!");
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
                case R.id.spinSource:
                    EditText etSource = (EditText) parent.findViewById(R.id.etSource);
                    etSource.requestFocus();
                    etSource.setError(message);
                    break;
                case R.id.spinDestination:
                    EditText etDestination = (EditText) parent.findViewById(R.id.etDestination);
                    etDestination.requestFocus();
                    etDestination.setError(message);
                    break;
            }
            Log.e(TAG, message);
        }
    }
}
