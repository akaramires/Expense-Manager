/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:44 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import org.eatech.expense.db.entities.CategoryEntity;
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
}
