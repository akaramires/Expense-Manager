/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:16 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "destinations")
public class DestinationEntity
{
    public final static String TABLE_NAME = "destinations";

    public final static String COL_TITLE      = "title";
    public final static String COL_TYPE       = "type";
    public final static String COL_EDITABLE   = "editable";
    public final static String COL_CREATED_AT = "created_at";

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private CategoryEntity category;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_TITLE)
    private String title;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_TYPE)
    private String type;

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER, columnName = COL_EDITABLE, defaultValue = "1")
    private int editable;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COL_CREATED_AT)
    private long created_at;

    public DestinationEntity()
    {
    }

    public DestinationEntity(CategoryEntity category, String title, String type, long created_at)
    {
        this.category = category;
        this.title = title;
        this.type = type;
        this.created_at = created_at;
        this.editable = 1;
    }

    public DestinationEntity(int id, CategoryEntity category, String title, String type,
                             long created_at)
    {
        Id = id;
        this.category = category;
        this.title = title;
        this.type = type;
        this.created_at = created_at;
        this.editable = 1;
    }

    public int getId()
    {
        return Id;
    }

    public void setId(int id)
    {
        Id = id;
    }

    public CategoryEntity getCategory()
    {
        return category;
    }

    public void setCategory(CategoryEntity category)
    {
        this.category = category;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        if (type.equals("in") || type.equals("out")) {
            this.type = type;
        } else {
            this.type = "out";
        }
    }

    public long getCreated_at()
    {
        return created_at;
    }

    public void setCreated_at()
    {
        this.created_at = new Date().getTime();
    }

    public int getEditable()
    {
        return editable;
    }

    public void setEditable()
    {
        this.editable = 1;
    }

    @Override
    public String toString()
    {
        return "DestinationEntity{" +
            "Id=" + Id +
            ", category=" + category +
            ", title='" + title + '\'' +
            ", type=" + type +
            ", editable=" + editable +
            ", created_at=" + created_at +
            '}';
    }
}