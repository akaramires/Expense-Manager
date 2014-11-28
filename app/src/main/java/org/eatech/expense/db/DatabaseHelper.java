/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:13 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.eatech.expense.db.dao.CategoryDao;
import org.eatech.expense.db.dao.CurrencyDao;
import org.eatech.expense.db.dao.DestinationDao;
import org.eatech.expense.db.dao.OperationDao;
import org.eatech.expense.db.dao.SourceDao;
import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.CurrencyEntity;
import org.eatech.expense.db.entities.DestinationEntity;
import org.eatech.expense.db.entities.OperationEntity;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.util.Date;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{

    private static final String TAG = "Expense-" + DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME    = "expense.sqlite";
    private static final int    DATABASE_VERSION = 10;
    private CurrencyDao    currencyDao;
    private SourceDao      sourceDao;
    private CategoryDao    categoryDao;
    private DestinationDao destinationDao;
    private OperationDao   operationDao;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource)
    {
        long curDate = new Date().getTime();
        try {

            TableUtils.createTable(connectionSource, CurrencyEntity.class);
            database.execSQL("INSERT INTO `" + CurrencyEntity.TABLE_NAME + "` (`title`, `code`, `symbol_left`, `symbol_right`, `editable`, `created_at`) VALUES ('Доллар США',        'USD', '$', '',     0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + CurrencyEntity.TABLE_NAME + "` (`title`, `code`, `symbol_left`, `symbol_right`, `editable`, `created_at`) VALUES ('Евро',              'EUR', '',  '€',    0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + CurrencyEntity.TABLE_NAME + "` (`title`, `code`, `symbol_left`, `symbol_right`, `editable`, `created_at`) VALUES ('Российский рубль',  'RUB', '',  'руб.', 0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + CurrencyEntity.TABLE_NAME + "` (`title`, `code`, `symbol_left`, `symbol_right`, `editable`, `created_at`) VALUES ('Украинская гривна', 'UAH', '',  '₴',    0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + CurrencyEntity.TABLE_NAME + "` (`title`, `code`, `symbol_left`, `symbol_right`, `editable`, `created_at`) VALUES ('Фунт стерлингов',   'GBP', '£', '',     0, '" + curDate + "');");

            TableUtils.createTable(connectionSource, SourceEntity.class);
            database.execSQL("INSERT INTO `" + SourceEntity.TABLE_NAME + "` (`title`, `sum_start`, `sum_current`, `created_at`, `currency_id`) VALUES ('Master Card', '500', '500', '" + curDate + "', 1);");

            TableUtils.createTable(connectionSource, CategoryEntity.class);
            database.execSQL("INSERT INTO `" + CategoryEntity.TABLE_NAME + "` (`title`, `editable`, `created_at`) VALUES ('Автомобиль', 0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + CategoryEntity.TABLE_NAME + "` (`title`, `editable`, `created_at`) VALUES ('Еда',        0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + CategoryEntity.TABLE_NAME + "` (`title`, `editable`, `created_at`) VALUES ('Работа',     0, '" + curDate + "');");

            TableUtils.createTable(connectionSource, DestinationEntity.class);
            database.execSQL("INSERT INTO `" + DestinationEntity.TABLE_NAME + "` (`title`, `category_id`, `type`, `editable`, `created_at`) VALUES ('Бензин',   1, 'out', 0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + DestinationEntity.TABLE_NAME + "` (`title`, `category_id`, `type`, `editable`, `created_at`) VALUES ('Хлеб',     2, 'out', 0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + DestinationEntity.TABLE_NAME + "` (`title`, `category_id`, `type`, `editable`, `created_at`) VALUES ('Молоко',   2, 'out', 0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + DestinationEntity.TABLE_NAME + "` (`title`, `category_id`, `type`, `editable`, `created_at`) VALUES ('Сахар',    2, 'out', 0, '" + curDate + "');");
            database.execSQL("INSERT INTO `" + DestinationEntity.TABLE_NAME + "` (`title`, `category_id`, `type`, `editable`, `created_at`) VALUES ('Зарплата', 3, 'in',  0, '" + curDate + "');");

            TableUtils.createTable(connectionSource, OperationEntity.class);
            database.execSQL("INSERT INTO `" + OperationEntity.TABLE_NAME + "` (`date`, `type`, `source_id`, `destination_id`, `count`, `cost`,  `comment`, `created_at`) VALUES ('" + curDate + "', 'out', 1, 1, 20, '39',    '', '" + curDate + "');");
            database.execSQL("INSERT INTO `" + OperationEntity.TABLE_NAME + "` (`date`, `type`, `source_id`, `destination_id`, `count`, `cost`,  `comment`, `created_at`) VALUES ('" + curDate + "', 'out', 1, 2, 3,  '10',    '', '" + curDate + "');");
            database.execSQL("INSERT INTO `" + OperationEntity.TABLE_NAME + "` (`date`, `type`, `source_id`, `destination_id`, `count`, `cost`,  `comment`, `created_at`) VALUES ('" + curDate + "', 'out', 1, 3, 2,  '30.15', '', '" + curDate + "');");
            database.execSQL("INSERT INTO `" + OperationEntity.TABLE_NAME + "` (`date`, `type`, `source_id`, `destination_id`, `count`, `cost`,  `comment`, `created_at`) VALUES ('" + curDate + "', 'out', 1, 4, 1,  '41.44', '', '" + curDate + "');");
            database.execSQL("INSERT INTO `" + OperationEntity.TABLE_NAME + "` (`date`, `type`, `source_id`, `destination_id`, `count`, `cost`,  `comment`, `created_at`) VALUES ('" + curDate + "', 'in',  1, 5, 1,  '25000', '', '" + curDate + "');");

        } catch (SQLException e) {
            Log.e(TAG, "Error creating DB " + DATABASE_NAME);
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion)
    {
        try {
            TableUtils.dropTable(connectionSource, OperationEntity.class, true);
            TableUtils.dropTable(connectionSource, CurrencyEntity.class, true);
            TableUtils.dropTable(connectionSource, SourceEntity.class, true);
            TableUtils.dropTable(connectionSource, CategoryEntity.class, true);
            TableUtils.dropTable(connectionSource, DestinationEntity.class, true);

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Error upgrading db " + DATABASE_NAME + "from ver " + oldVersion);
            e.printStackTrace();
        }
    }

    public CurrencyDao getCurrencyDAO() throws SQLException
    {
        if (currencyDao == null) {
            currencyDao = new CurrencyDao(getConnectionSource(), CurrencyEntity.class);
        }

        return currencyDao;
    }

    public SourceDao getSourceDAO() throws SQLException
    {
        if (sourceDao == null) {
            sourceDao = new SourceDao(getConnectionSource(), SourceEntity.class);
        }

        return sourceDao;
    }

    public CategoryDao getCategoryDAO() throws SQLException
    {
        if (categoryDao == null) {
            categoryDao = new CategoryDao(getConnectionSource(), CategoryEntity.class);
        }

        return categoryDao;
    }

    public DestinationDao getDestinationDAO() throws SQLException
    {
        if (destinationDao == null) {
            destinationDao = new DestinationDao(getConnectionSource(), DestinationEntity.class);
        }

        return destinationDao;
    }

    public OperationDao getOperationDAO() throws SQLException
    {
        if (operationDao == null) {
            operationDao = new OperationDao(getConnectionSource(), OperationEntity.class);
        }

        return operationDao;
    }

    @Override
    public void close()
    {
        super.close();
        currencyDao = null;
        sourceDao = null;
        categoryDao = null;
        destinationDao = null;
        operationDao = null;
    }
}