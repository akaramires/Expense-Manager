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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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

    @InjectView(R.id.spinExpenseType)
    Spinner spinExpenseType;

    @InjectView(R.id.etDate)
    EditText etDate;

    private SimpleDateFormat dateFormatter;

    private DatePickerDialog datePickerDialog;

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
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        String[] data = getResources().getStringArray(R.array.lblExpenseTypes);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinExpenseType.setAdapter(adapter);
        spinExpenseType.setPrompt(getString(R.string.lblExpenseType));
        spinExpenseType.setSelection(0);

        etDate.setInputType(InputType.TYPE_NULL);
        etDate.setText(dateFormatter.format(new Date().getTime()));

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.add(getString(R.string.action_save))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(getString(R.string.action_cancel))
            .setIcon(R.drawable.ic_action_cancel)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
        }
        return true;

    }

    @OnClick(R.id.etDate)
    public void onClick(View view)
    {
        datePickerDialog.show();
    }
}
