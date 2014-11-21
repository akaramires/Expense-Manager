/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:16 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.eatech.expense.db.HelperFactory;

import java.sql.SQLException;
import java.util.Date;

@DatabaseTable(tableName = "sources")
public class SourceEntity
{
    public final static String TABLE_NAME = "sources";

    public final static String COL_TITLE       = "title";
    public final static String COL_SUM_START   = "sum_start";
    public final static String COL_SUM_CURRENT = "sum_current";
    public final static String COL_CREATED_AT  = "created_at";
    public final static String COL_CURRENCY    = "currency_id";

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_TITLE)
    private String title;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_SUM_START)
    private String sum_start;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_SUM_CURRENT)
    private String sum_current;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COL_CREATED_AT)
    private long created_at;

    @DatabaseField(foreign = true, columnName = COL_CURRENCY)
    private CurrencyEntity currency;

    public SourceEntity()
    {
    }

    public SourceEntity(String title, String sum_start, String sum_current, CurrencyEntity currency)
    {
        this.title = title;
        this.sum_start = sum_start;
        this.sum_current = sum_current;
        this.currency = currency;
        this.setCreated_at();
    }

    public SourceEntity(int id, String title, String sum_start, String sum_current,
                        CurrencyEntity currency)
    {
        Id = id;
        this.title = title;
        this.sum_start = sum_start;
        this.sum_current = sum_current;
        this.currency = currency;
        this.setCreated_at();
    }

    public int getId()
    {
        return Id;
    }

    public void setId(int id)
    {
        Id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSum_start()
    {
        return sum_start;
    }

    public void setSum_start(String sum_start)
    {
        this.sum_start = sum_start;
    }

    public String getSum_current()
    {
        return sum_current;
    }

    public void setSum_current(String sum_current)
    {
        this.sum_current = sum_current;
    }

    public long getCreated_at()
    {
        return created_at;
    }

    public void setCreated_at()
    {
        this.created_at = new Date().getTime();
    }

    public CurrencyEntity getCurrency() throws SQLException
    {
        return HelperFactory.getInstance().getHelper().getCurrencyDAO().queryForId(currency.getId());
    }

    public void setCurrency(CurrencyEntity currency)
    {
        this.currency = currency;
    }

    @Override
    public String toString()
    {
        return title;
    }

    public String toStringFull()
    {
        return "SourceEntity{" +
            "Id=" + Id +
            ", title=" + title +
            ", sum_start='" + sum_start + '\'' +
            ", sum_current='" + sum_current + '\'' +
            ", created_at=" + created_at +
            ", currency=" + currency +
            '}';
    }
}