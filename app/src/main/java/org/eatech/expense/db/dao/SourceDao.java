/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:44 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.util.List;

public class SourceDao extends BaseDaoImpl<SourceEntity, Integer>
{
    private static final String TAG = "Expense-" + SourceDao.class.getSimpleName();

    public SourceDao(ConnectionSource connectionSource,
                     Class<SourceEntity> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }

    public List<SourceEntity> getAll() throws SQLException
    {
        Log.i(TAG, "getAll()");

        return this.queryForAll();
    }
}
