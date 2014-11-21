/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.Select;

import org.eatech.expense.R;
import org.eatech.expense.SourceActivity;
import org.eatech.expense.adapter.DestinationAdapter;
import org.eatech.expense.adapter.RangeDatePickerDialog;
import org.eatech.expense.adapter.SourceAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.DestinationEntity;
import org.eatech.expense.db.entities.SourceEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FragmentForm extends SherlockFragment implements Validator.ValidationListener,
                                                              View.OnTouchListener,
                                                              ExpandableListView.OnChildClickListener
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
    @NumberRule(order = 3, type = NumberRule.NumberType.INTEGER, gt = 0.99, messageResId = R.string.msgValidationCount)
    EditText etCount;

    @InjectView(R.id.etCost)
    @NumberRule(order = 4, type = NumberRule.NumberType.DOUBLE, gt = 0.01, messageResId = R.string.msgValidationCost)
    EditText etCost;

    @InjectView(R.id.outTotal)
    TextView outTotal;

    @InjectView(R.id.etComment)
    EditText etComment;

    @InjectView(R.id.ibSource)
    ImageButton ibSource;

    @InjectView(R.id.ibDestination)
    ImageButton ibDestination;

    private SimpleDateFormat dateFormatter;
    private Calendar         maxDate;
    private Calendar         current;

    private DatePickerDialog            datePickerDialog;
    private Validator                   validator;
    private DatabaseHelper              dbHelper;
    private SimpleExpandableListAdapter expAdpt;
    private Dialog                      dlgDestionation;
    private List<DestinationEntity>     destinationEntityList;

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

        outTotal.setText("0");
        etCount.setText("1");
        etCount.addTextChangedListener(new AmountTotal());
        etCost.addTextChangedListener(new AmountTotal());
    }

    private class AmountTotal implements TextWatcher
    {
        public void afterTextChanged(Editable s)
        {
            double cost = 0;
            if (etCost.getText().toString().length() > 0) {
                cost = Double.parseDouble(etCost.getText().toString());
            }

            int count = 0;
            if (etCount.getText().toString().length() > 0) {
                count = Integer.parseInt(etCount.getText().toString());
            }

            double total = new BigDecimal(count * cost).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            outTotal.setText(String.valueOf(total));
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
        SourceAdapter<SourceEntity> adptSrcDropdown = new SourceAdapter<SourceEntity>(getSherlockActivity());
        adptSrcDropdown.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        adptSrcDropdown.add(new SourceEntity(0, getString(R.string.msgValidationSource), "0", "0", null));

        List<SourceEntity> sourceEntityList = dbHelper.getSourceDAO().getAll();
        for (SourceEntity srcEntity : sourceEntityList) {
            adptSrcDropdown.add(srcEntity);
        }

        spinSource.setAdapter(adptSrcDropdown);
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
        DestinationAdapter<DestinationEntity> adptDstnDropdown = new DestinationAdapter<DestinationEntity>(getSherlockActivity());
        adptDstnDropdown.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        adptDstnDropdown.add(new DestinationEntity(0, null, getString(R.string.msgValidationDestination), null, 0));

        destinationEntityList = dbHelper.getDestinationDAO().getAll();
        for (DestinationEntity destinationEntity : destinationEntityList) {
            adptDstnDropdown.add(destinationEntity);
        }

        spinDestination.setAdapter(adptDstnDropdown);
        spinDestination.setSelection(0);
        spinDestination.setOnTouchListener(this);

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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            try {
                dlgDestionation = new Dialog(getSherlockActivity());
                dlgDestionation.setContentView(android.R.layout.expandable_list_content);
                dlgDestionation.setTitle(getString(R.string.lblDestination));

                ExpandableListView expListView = (ExpandableListView) dlgDestionation.findViewById(android.R.id.list);

                List<CategoryEntity> catsEntList = dbHelper.getCategoryDAO().getAll();
                ArrayList<Map<String, String>> catsData = new ArrayList<Map<String, String>>();
                ArrayList<ArrayList<Map<String, String>>> destData = new ArrayList<ArrayList<Map<String, String>>>();
                Map<String, String> tmp;

                for (CategoryEntity cat : catsEntList) {
                    tmp = new HashMap<String, String>();
                    tmp.put("id", String.valueOf(cat.getId()));
                    tmp.put("title", cat.getTitle());
                    catsData.add(tmp);

                    ArrayList<Map<String, String>> destDataItem = new ArrayList<Map<String, String>>();
                    for (DestinationEntity dest : cat.getDestinations()) {
                        tmp = new HashMap<String, String>();
                        tmp.put("id", String.valueOf(dest.getId()));
                        tmp.put("title", dest.getTitle());
                        destDataItem.add(tmp);
                    }
                    destData.add(destDataItem);
                }

                expAdpt = new SimpleExpandableListAdapter(
                    getSherlockActivity(),
                    catsData,
                    android.R.layout.simple_expandable_list_item_1,
                    new String[] { "title" },
                    new int[] { android.R.id.text1 },
                    destData,
                    android.R.layout.simple_list_item_1,
                    new String[] { "title" },
                    new int[] { android.R.id.text1 });

                expListView.setAdapter(expAdpt);
                expListView.setOnChildClickListener(this);

                dlgDestionation.show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition,
                                int childPosition, long id)
    {
        try {
            JSONObject cat = new JSONObject(expAdpt.getChild(groupPosition, childPosition).toString());
            int destination_id = cat.getInt("id");

            int index = 1;
            for (DestinationEntity destinationEntity : destinationEntityList) {
                if (destination_id == destinationEntity.getId()) {
                    spinDestination.setSelection(index);
                    break;
                }

                index++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dlgDestionation.hide();
        return false;
    }
}
