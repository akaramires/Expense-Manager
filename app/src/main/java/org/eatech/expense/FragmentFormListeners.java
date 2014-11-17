/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/13/14 10:09 PM
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

public class FragmentFormListeners implements Validator.ValidationListener
{
    private static final String TAG = "EXPENSE-" + FragmentFormListeners.class.getSimpleName();

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
