/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:16 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db.entities;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "categories")
public class CategoryEntity
{
    public final static String TABLE_NAME = "categories";

    public final static String COL_TITLE      = "title";
    public final static String COL_EDITABLE   = "editable";
    public final static String COL_CREATED_AT = "created_at";

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_TITLE)
    private String title;

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER, columnName = COL_EDITABLE, defaultValue = "1")
    private int editable;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COL_CREATED_AT)
    private long created_at;

    @ForeignCollectionField
    private ForeignCollection<DestinationEntity> destinations;

    public CategoryEntity()
    {
    }

    public CategoryEntity(String title, long created_at,
                          ForeignCollection<DestinationEntity> destinations)
    {
        this.title = title;
        this.editable = 1;
        this.created_at = created_at;
        this.destinations = destinations;
    }

    public CategoryEntity(int id, String title, long created_at,
                          ForeignCollection<DestinationEntity> destinations)
    {
        Id = id;
        this.title = title;
        this.editable = 1;
        this.created_at = created_at;
        this.destinations = destinations;
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

    public int getEditable()
    {
        return editable;
    }

    public void setEditable()
    {
        this.editable = 1;
    }

    public long getCreated_at()
    {
        return created_at;
    }

    public void setCreated_at()
    {
        this.created_at = new Date().getTime();
    }

    public ForeignCollection<DestinationEntity> getDestinations()
    {
        return destinations;
    }

    public void setDestinations(ForeignCollection<DestinationEntity> destinations)
    {
        this.destinations = destinations;
    }

    @Override
    public String toString()
    {
        return "CategoryEntity{" +
            "Id=" + Id +
            ", title='" + title + '\'' +
            ", editable=" + editable +
            ", created_at=" + created_at +
            ", destinations=" + destinations +
            '}';
    }
}