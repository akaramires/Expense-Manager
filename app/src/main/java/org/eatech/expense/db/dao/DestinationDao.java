/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:44 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.DestinationEntity;

import java.sql.SQLException;
import java.util.List;

public class DestinationDao extends BaseDaoImpl<DestinationEntity, Integer>
{
    private static final String TAG = "Expense-" + DestinationDao.class.getSimpleName();

    public DestinationDao(ConnectionSource connectionSource,
                          Class<DestinationEntity> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }

    public List<DestinationEntity> getAll() throws SQLException
    {
        Log.i(TAG, "getAll()");

        return this.queryForAll();
    }

    public List<DestinationEntity> getAllIn() throws SQLException
    {
        Log.i(TAG, "getAllIn()");

        return this.queryBuilder().where().eq(DestinationEntity.COL_TITLE, "in").query();
    }

    public List<DestinationEntity> getAllOut() throws SQLException
    {
        Log.i(TAG, "getAllOut()");

        return this.queryBuilder().where().eq(DestinationEntity.COL_TITLE, "out").query();
    }
}
