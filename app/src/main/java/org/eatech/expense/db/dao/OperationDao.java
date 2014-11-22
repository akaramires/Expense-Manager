/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:44 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.OperationEntity;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class OperationDao extends BaseDaoImpl<OperationEntity, Integer>
{
    private static final String TAG = "Expense-" + OperationDao.class.getSimpleName();

    public OperationDao(ConnectionSource connectionSource,
                        Class<OperationEntity> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }

    public List<OperationEntity> getAll() throws SQLException
    {
        Log.i(TAG, "getAll()");

        return this.queryForAll();
    }

    public List<OperationEntity> getCurrentMonth() throws SQLException
    {
        Log.i(TAG, "getCurrentMonth()");

        Calendar calendarMin = GregorianCalendar.getInstance();
        calendarMin.setTime(new Date());
        calendarMin.set(Calendar.DAY_OF_MONTH, calendarMin.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendarMin.set(Calendar.HOUR_OF_DAY, 0);
        calendarMin.set(Calendar.MINUTE, 0);
        calendarMin.set(Calendar.SECOND, 0);
        calendarMin.set(Calendar.MILLISECOND, 0);
        long min_date = calendarMin.getTimeInMillis();

        Calendar calendarMax = GregorianCalendar.getInstance();
        calendarMax.setTime(new Date());
        calendarMax.set(Calendar.DAY_OF_MONTH, calendarMax.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendarMax.set(Calendar.HOUR_OF_DAY, 23);
        calendarMax.set(Calendar.MINUTE, 59);
        calendarMax.set(Calendar.SECOND, 59);
        calendarMax.set(Calendar.MILLISECOND, 999);
        long max_date = calendarMax.getTimeInMillis();

        return this.queryBuilder()
            .orderBy(OperationEntity.COL_DATE, false)
            .where()
            .ge(OperationEntity.COL_DATE, min_date)
            .and()
            .le(OperationEntity.COL_DATE, max_date)
            .query();
    }

    public int createOperation(OperationEntity operationEntity) throws SQLException
    {
        Log.i(TAG, "createOperation(" + operationEntity.toString() + ")");

        return this.create(operationEntity);
    }
}
