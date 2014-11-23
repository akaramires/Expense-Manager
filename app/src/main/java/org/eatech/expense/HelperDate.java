/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/23/14 3:10 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HelperDate
{
    private static Calendar getCalendar()
    {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    public static Calendar getStartCurrentMonthCal()
    {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    public static long getStartCurrentMonth()
    {
        return getStartCurrentMonthCal().getTimeInMillis();
    }

    public static Calendar getEndCurrentMonthCal()
    {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar;
    }

    public static long getEndCurrentMonth()
    {
        return getEndCurrentMonthCal().getTimeInMillis();
    }
}
