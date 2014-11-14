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
            switch (failedView.getId()) {
                case R.id.spinSource:
//                    ViewGroup row = (ViewGroup) failedView.getParent();
//                    for (int itemPos = 0; itemPos < row.getChildCount(); itemPos++) {
//                        View childView = row.getChildAt(itemPos);
//                        if (childView.getId() == R.id.etSource) {
//                            ((EditText) childView).setError(message);
//                            childView.requestFocus();
//                            break;
//                        }
//                    }
                    break;
                case R.id.spinDestination:
                    break;
            }
            Log.e(TAG, message);
        }
    }
}
