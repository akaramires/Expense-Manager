/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:44 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import org.eatech.expense.HelperDate;
import org.eatech.expense.db.entities.OperationEntity;

import java.sql.SQLException;
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

    public com.j256.ormlite.stmt.Where<OperationEntity, Integer> getCurrentMonthBase() throws SQLException
    {
        Log.i(TAG, "getCurrentMonthBase()");

        return this.queryBuilder().orderBy(OperationEntity.COL_DATE, false)
            .where()
            .ge(OperationEntity.COL_DATE, HelperDate.getStartCurrentMonth())
            .and()
            .le(OperationEntity.COL_DATE, HelperDate.getEndCurrentMonth());
    }

    public List<OperationEntity> getCurrentMonth() throws SQLException
    {
        Log.i(TAG, "getCurrentMonth()");

        return this.getCurrentMonthBase().query();
    }

    public int createOperation(OperationEntity operationEntity) throws SQLException
    {
        Log.i(TAG, "createOperation(" + operationEntity.toString() + ")");

        return this.create(operationEntity);
    }

    public double getInAtCurMonth() throws SQLException
    {
        Log.i(TAG, "getInAtCurMonth()");

        List<OperationEntity> oprtns = this
            .getCurrentMonthBase()
            .and()
            .eq(OperationEntity.COL_TYPE_ID, 1)
            .query();

        double in = 0;
        for (OperationEntity oprtn : oprtns) {
            in += (oprtn.getCost() * oprtn.getCount());
        }

        return in;
    }

    public double getOutAtCurMonth() throws SQLException
    {
        Log.i(TAG, "getInAtCurMonth()");

        List<OperationEntity> oprtns = this
            .getCurrentMonthBase()
            .and()
            .eq(OperationEntity.COL_TYPE_ID, 0)
            .query();

        double in = 0;
        for (OperationEntity oprtn : oprtns) {
            in += (oprtn.getCost() * oprtn.getCount());
        }

        return in;
    }
}
