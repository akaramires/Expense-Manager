/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.Select;

import org.eatech.expense.adapter.RangeDatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FragmentForm extends SherlockFragment
{
    private static final String TAG = "EXPENSE-" + FragmentForm.class.getSimpleName();

    @InjectView(R.id.spinType)
    Spinner spinExpenseType;

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

    private SimpleDateFormat dateFormatter;

    private DatePickerDialog datePickerDialog;
    private Validator        validator;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_form, container, false);
        ButterKnife.inject(this, rootView);

        validator = new Validator(this);
        validator.setValidationListener(new FragmentFormListeners());

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        setupForm();
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

    private void setupForm()
    {
        String[] data = getResources().getStringArray(R.array.lblTypes);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinExpenseType.setAdapter(adapter);
        spinExpenseType.setSelection(0);

        etDate.setInputType(InputType.TYPE_NULL);
        etDate.setText(dateFormatter.format(new Date().getTime()));

        ArrayAdapter<String> adapterSource = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, new String[] { getString(R.string.msgValidationSource) });
        adapterSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSource.setAdapter(adapterSource);
        spinSource.setSelection(0);

        ArrayAdapter<String> adapterDestination = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, new String[] { getString(R.string.msgValidationDestination) });
        adapterDestination.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDestination.setAdapter(adapterDestination);
        spinDestination.setSelection(0);

        Calendar maxDate;
        Calendar current = maxDate = Calendar.getInstance();
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

    private void clearForm()
    {
        spinExpenseType.setSelection(0);
        etDate.setText(dateFormatter.format(new Date().getTime()));
        spinSource.setSelection(0);
        spinDestination.setSelection(0);
        etCount.setText("");
        etCost.setText("");
        etComment.setText("");
    }

    @OnClick(R.id.etDate)
    public void onClick(View view)
    {
        datePickerDialog.show();
    }
}
