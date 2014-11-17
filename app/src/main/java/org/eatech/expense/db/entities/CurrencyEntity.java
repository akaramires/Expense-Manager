/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:16 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "currencies")
public class CurrencyEntity
{
    public final static String TABLE_NAME = "currencies";

    public final static String COL_TITLE        = "title";
    public final static String COL_CODE         = "code";
    public final static String COL_SYMBOL_LEFT  = "symbol_left";
    public final static String COL_SYMBOL_RIGHT = "symbol_right";
    public final static String COL_EDITABLE     = "editable";
    public final static String COL_CREATED_AT   = "created_at";

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_TITLE)
    private String title;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_CODE)
    private String code;

    @DatabaseField(canBeNull = true, dataType = DataType.STRING, columnName = COL_SYMBOL_LEFT)
    private String symbol_left;

    @DatabaseField(canBeNull = true, dataType = DataType.STRING, columnName = COL_SYMBOL_RIGHT)
    private String symbol_right;

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER, columnName = COL_EDITABLE)
    private int editable;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COL_CREATED_AT)
    private long created_at;

    public CurrencyEntity()
    {
    }

    public CurrencyEntity(String title, String code, String symbol_left, String symbol_right,
                          int editable, long created_at)
    {
        this.title = title;
        this.code = code;
        this.symbol_left = symbol_left;
        this.symbol_right = symbol_right;
        this.editable = editable;
        this.created_at = created_at;
    }

    public CurrencyEntity(int id, String title, String code, String symbol_left,
                          String symbol_right, int editable, long created_at)
    {
        Id = id;
        this.title = title;
        this.code = code;
        this.symbol_left = symbol_left;
        this.symbol_right = symbol_right;
        this.editable = editable;
        this.created_at = created_at;
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

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getSymbol_left()
    {
        return symbol_left;
    }

    public void setSymbol_left(String symbol_left)
    {
        this.symbol_left = symbol_left;
    }

    public String getSymbol_right()
    {
        return symbol_right;
    }

    public void setSymbol_right(String symbol_right)
    {
        this.symbol_right = symbol_right;
    }

    public int getEditable()
    {
        return editable;
    }

    public void setEditable(int editable)
    {
        this.editable = editable;
    }

    public long getCreated_at()
    {
        return created_at;
    }

    public void setCreated_at()
    {
        this.created_at = new Date().getTime();
    }

    @Override
    public String toString()
    {
        return "CurrencyEntity{" +
            "Id=" + Id +
            ", title='" + title + '\'' +
            ", code='" + code + '\'' +
            ", symbol_left='" + symbol_left + '\'' +
            ", symbol_right='" + symbol_right + '\'' +
            ", editable=" + editable +
            ", created_at=" + created_at +
            '}';
    }
}