/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:44 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

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

    public int createOperation(OperationEntity operationEntity) throws SQLException
    {
        Log.i(TAG, "createOperation(" + operationEntity.toString() + ")");

        return this.create(operationEntity);
    }

    public Where<OperationEntity, Integer> getAllByPeriodBuilder(long date_start,
                                                                 long date_end) throws SQLException
    {
        Log.i(TAG, "getAllByPeriodBuilder()");

        return this.queryBuilder().orderBy(OperationEntity.COL_DATE, false)
            .where()
            .ge(OperationEntity.COL_DATE, date_start)
            .and()
            .le(OperationEntity.COL_DATE, date_end);
    }

    public List<OperationEntity> getAllByPeriod(long date_start, long date_end) throws SQLException
    {
        Log.i(TAG, "getAllByPeriod()");

        return this.getAllByPeriodBuilder(date_start, date_end)
            .and()
            .le(OperationEntity.COL_DATE, date_end)
            .query();
    }

    public double getInByPeriod(long date_start, long date_end) throws SQLException
    {
        Log.i(TAG, "getInByPeriod()");

        List<OperationEntity> oprtns = this.getAllByPeriodBuilder(date_start, date_end)
            .and().eq(OperationEntity.COL_TYPE, "in")
            .query();

        double in = 0;
        for (OperationEntity oprtn : oprtns) {
            in += (oprtn.getCost() * oprtn.getCount());
        }

        return in;
    }

    public double getOutByPeriod(long date_start, long date_end) throws SQLException
    {
        Log.i(TAG, "getOutByPeriod()");

        List<OperationEntity> oprtns = this.getAllByPeriodBuilder(date_start, date_end)
            .and().eq(OperationEntity.COL_TYPE, "out")
            .query();

        double in = 0;
        for (OperationEntity oprtn : oprtns) {
            in += (oprtn.getCost() * oprtn.getCount());
        }

        return in;
    }
}
