/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/18/14 11:40 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

public class SourceFormActivityValidator implements Validator.ValidationListener
{
    private static final String TAG = "Expense-" + SourceFormActivityValidator.class.getSimpleName();

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
                case R.id.spinCurrency:
                    EditText etCurrency = (EditText) parent.findViewById(R.id.etCurrency);
                    etCurrency.requestFocus();
                    etCurrency.setError(message);
                    break;
            }
            Log.e(TAG, message);
        }
    }
}
