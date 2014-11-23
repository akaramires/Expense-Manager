/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/13/14 8:04 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

public class RangeDatePickerDialog extends DatePickerDialog
{

    public  Calendar             minDate;
    public  Calendar             maxDate;
    private java.text.DateFormat mTitleDateFormat;

    public RangeDatePickerDialog(Context context, OnDateSetListener callBack, Calendar date,
                                 Calendar minDate, Calendar maxDate)
    {
        super(context, callBack, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        this.minDate = minDate;
        this.maxDate = maxDate;
        mTitleDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL);
    }

    public RangeDatePickerDialog(Context context,
                                 DatePickerDialog.OnDateSetListener callBack,
                                 int year, int monthOfYear, int dayOfMonth, Calendar minDate,
                                 Calendar maxDate)
    {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        this.minDate = minDate;
        this.maxDate = maxDate;
        mTitleDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL);
    }

    public void onDateChanged(DatePicker view, int year, int month, int day)
    {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, month, day);

        if (minDate != null && minDate.after(newDate)) {
            view.init(minDate.get(Calendar.YEAR), minDate.get(Calendar.MONTH),
                minDate.get(Calendar.DAY_OF_MONTH), this);
            setTitle(mTitleDateFormat.format(minDate.getTime()));
        } else if (maxDate != null && maxDate.before(newDate)) {
            view.init(maxDate.get(Calendar.YEAR), maxDate.get(Calendar.MONTH),
                maxDate.get(Calendar.DAY_OF_MONTH), this);
            setTitle(mTitleDateFormat.format(maxDate.getTime()));
        } else {
            view.init(year, month, day, this);
            setTitle(mTitleDateFormat.format(newDate.getTime()));
        }
    }
}