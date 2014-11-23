/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.Select;

import org.eatech.expense.MainActivity;
import org.eatech.expense.R;
import org.eatech.expense.SourceActivity;
import org.eatech.expense.adapter.DestinationAdapter;
import org.eatech.expense.adapter.RangeDatePickerDialog;
import org.eatech.expense.adapter.SourceAdapter;
import org.eatech.expense.db.DatabaseHelper;
import org.eatech.expense.db.HelperFactory;
import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.DestinationEntity;
import org.eatech.expense.db.entities.OperationEntity;
import org.eatech.expense.db.entities.SourceEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
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
    //    @Select(order = 1, defaultSelection = 0, messageResId = R.string.msgValidationSource)
        Spinner spinSource;

    @InjectView(R.id.spinDestination)
    @Select(order = 1, defaultSelection = 0, messageResId = R.string.msgValidationDestination)
    Spinner spinDestination;

    @InjectView(R.id.etCount)
    @NumberRule(order = 2, type = NumberRule.NumberType.INTEGER, gt = 0.99, messageResId = R.string.msgValidationCount)
    EditText etCount;

    @InjectView(R.id.etCost)
    @NumberRule(order = 3, type = NumberRule.NumberType.DOUBLE, gt = 0.01, messageResId = R.string.msgValidationCost)
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
    private String current_type = "out";

    private Validator                             validator;
    private DatabaseHelper                        dbHelper;
    private Dialog                                dialogDestionation;
    private DatePickerDialog                      dialogDatePicker;
    private SourceAdapter<SourceEntity>           adapterSource;
    private ArrayAdapter<String>                  adapterType;
    private DestinationAdapter<DestinationEntity> adapterDestination;
    private SimpleExpandableListAdapter           adapterDestinationExp;
    private List<DestinationEntity>               listDestination;
    private MainActivity                          mainActivity;

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
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
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
                dialogDatePicker.show();
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
            initAdapterSource();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupForm() throws SQLException
    {
        initAdapterType();
        initAdapterSource();
        initAdapterDestination();
        initDate();

        outTotal.setText("0");
        etCount.setText("1");
        etCount.addTextChangedListener(new AmountTotal());
        etCost.addTextChangedListener(new AmountTotal());
        etCost.requestFocus();
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
        etCount.setText("1");
        etCost.setText("");
        etComment.setText("");
        etCost.requestFocus();
        outTotal.setText("0");
    }

    private void initAdapterSource() throws SQLException
    {
        adapterSource = new SourceAdapter<SourceEntity>(getSherlockActivity());
        adapterSource.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        adapterSource.add(new SourceEntity(0, getString(R.string.msgValidationSource), "0", "0", null));

        List<SourceEntity> sourceEntityList = dbHelper.getSourceDAO().getAll();
        for (SourceEntity srcEntity : sourceEntityList) {
            adapterSource.add(srcEntity);
        }

        spinSource.setAdapter(adapterSource);
        spinSource.setSelection(0);
    }

    private void initAdapterType()
    {
        adapterType = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item);
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
                spinDestination.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }
        });
    }

    private void initAdapterDestination() throws SQLException
    {
        adapterDestination = new DestinationAdapter<DestinationEntity>(getSherlockActivity());
        adapterDestination.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        adapterDestination.add(new DestinationEntity(0, null, getString(R.string.msgValidationDestination), null, 0));

        listDestination = dbHelper.getDestinationDAO().getAll();
        for (DestinationEntity destinationEntity : listDestination) {
            adapterDestination.add(destinationEntity);
        }

        spinDestination.setAdapter(adapterDestination);
        spinDestination.setSelection(0);
        spinDestination.setOnTouchListener(this);

    }

    private void initDate()
    {
        etDate.setText(dateFormatter.format(new Date().getTime()));
        etDate.setFocusable(false);
        dialogDatePicker = new RangeDatePickerDialog(getSherlockActivity(), new DatePickerDialog.OnDateSetListener()
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
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            try {
                dialogDestionation = new Dialog(getSherlockActivity());
                dialogDestionation.setContentView(android.R.layout.expandable_list_content);
                dialogDestionation.setTitle(getString(R.string.lblDestination));

                ExpandableListView expListView = (ExpandableListView) dialogDestionation.findViewById(android.R.id.list);

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
                        if (dest.getType().equals(current_type)) {
                            tmp = new HashMap<String, String>();
                            tmp.put("id", String.valueOf(dest.getId()));
                            tmp.put("title", dest.getTitle());
                            destDataItem.add(tmp);
                        }
                    }
                    destData.add(destDataItem);
                }

                adapterDestinationExp = new SimpleExpandableListAdapter(
                    getSherlockActivity(),
                    catsData,
                    android.R.layout.simple_expandable_list_item_1,
                    new String[] { "title" },
                    new int[] { android.R.id.text1 },
                    destData,
                    android.R.layout.simple_list_item_1,
                    new String[] { "title" },
                    new int[] { android.R.id.text1 });

                expListView.setAdapter(adapterDestinationExp);
                expListView.setOnChildClickListener(this);

                dialogDestionation.show();
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
            JSONObject cat = new JSONObject(adapterDestinationExp.getChild(groupPosition, childPosition).toString());
            int destination_id = cat.getInt("id");

            int index = 1;
            for (DestinationEntity destinationEntity : listDestination) {
                if (destination_id == destinationEntity.getId()) {
                    spinDestination.setSelection(index);
                    break;
                }

                index++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialogDestionation.hide();
        return false;
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
    public void onValidationSucceeded()
    {
        try {
            int type = spinType.getSelectedItemPosition();

            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(etDate.getText().toString());
            long date = d.getTime();

            // SourceEntity source = adapterSource.getItem(spinSource.getSelectedItemPosition());
            SourceEntity source = dbHelper.getSourceDAO().queryForId(1);

            DestinationEntity destination = adapterDestination.getItem(spinDestination.getSelectedItemPosition());

            int count = Integer.parseInt(etCount.getText().toString());
            double cost = Double.parseDouble(etCost.getText().toString());
            String comment = etComment.getText().toString();

            OperationEntity operationEntity = new OperationEntity(date, OperationEntity.pos2type(type), source, destination, count, cost, comment);

            String msg = "";
            if (mainActivity.tmp_oper_id > 0) {
                operationEntity.setId(mainActivity.tmp_oper_id);

                if (dbHelper.getOperationDAO().updateOperation(operationEntity) > 0) {
                    clearForm();
                    msg = getString(R.string.msgSuccessUpdateOperation);
                } else {
                    msg = getString(R.string.msgErrorUpdateOperation);
                }
            } else {
                if (dbHelper.getOperationDAO().createOperation(operationEntity) > 0) {
                    clearForm();
                    msg = getString(R.string.msgSuccessAddOperation);
                } else {
                    msg = getString(R.string.msgErrorAddOperation);
                }
            }

            Toast.makeText(getSherlockActivity(), msg, Toast.LENGTH_SHORT).show();

            if (mainActivity.tmp_oper_id != 0) {
                mainActivity.tmp_oper_id = 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mainActivity.tmp_oper_id != 0) {
                try {
                    final OperationEntity operation = dbHelper.getOperationDAO().queryForId(mainActivity.tmp_oper_id);

                    etDate.setText(dateFormatter.format(operation.getDate()));
                    etCost.setText(String.valueOf(operation.getCost()));
                    etCount.setText(String.valueOf(operation.getCount()));
                    etComment.setText(String.valueOf(operation.getComment()));

                    for (int index = 0, count = adapterDestination.getCount(); index < count; ++index)
                    {
                        if (adapterDestination.getItem(index).getId() == operation.getDestination().getId()) {
                            final int finalIndex = index;
                            new Handler().postDelayed(new Runnable()
                            {

                                public void run()
                                {
                                    if (operation.getType().equals("in")) {
                                        spinType.setSelection(1);
                                    } else if (operation.getType().equals("out")) {
                                        spinType.setSelection(0);
                                    }
                                }
                            }, 100);
                            new Handler().postDelayed(new Runnable()
                            {

                                public void run()
                                {
                                    spinDestination.setSelection(finalIndex, false);
                                }
                            }, 300);
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
