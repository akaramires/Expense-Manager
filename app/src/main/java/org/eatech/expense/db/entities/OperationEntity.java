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

@DatabaseTable(tableName = "operations")
public class OperationEntity
{
    public final static String TABLE_NAME = "operations";

    public final static String COL_DATE           = "date";
    public final static String COL_TYPE           = "type";
    public final static String COL_SOURCE_ID      = "source_id";
    public final static String COL_DESTINATION_ID = "destination_id";
    public final static String COL_COUNT          = "count";
    public final static String COL_COST           = "cost";
    public final static String COL_COMMENT        = "comment";
    public final static String COL_CREATED_AT     = "created_at";

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COL_DATE)
    private long date;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_TYPE)
    private String type;

    @DatabaseField(foreign = true, columnName = COL_SOURCE_ID)
    private SourceEntity source;

    @DatabaseField(foreign = true, columnName = COL_DESTINATION_ID)
    private DestinationEntity destination;

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER, columnName = COL_COUNT)
    private int count;

    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE, columnName = COL_COST)
    private double cost;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COL_COMMENT)
    private String comment;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COL_CREATED_AT)
    private long created_at;

    public OperationEntity()
    {
    }

    public OperationEntity(long date, String type, SourceEntity source,
                           DestinationEntity destination, int count, double cost, String comment)
    {
        this.date = date;
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.count = count;
        this.cost = cost;
        this.comment = comment;
        this.setCreated_at();
    }

    public OperationEntity(int id, long date, String type, SourceEntity source,
                           DestinationEntity destination, int count, double cost, String comment)
    {
        Id = id;
        this.date = date;
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.count = count;
        this.cost = cost;
        this.comment = comment;
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

    public long getDate()
    {
        return date;
    }

    public void setDate(long date)
    {
        this.date = date;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public SourceEntity getSource() throws SQLException
    {
        return HelperFactory.getInstance().getHelper().getSourceDAO().queryForId(source.getId());
    }

    public void setSource(SourceEntity source)
    {
        this.source = source;
    }

    public DestinationEntity getDestination() throws SQLException
    {
        return HelperFactory.getInstance().getHelper().getDestinationDAO().queryForId(destination.getId());
    }

    public void setDestination(DestinationEntity destination)
    {
        this.destination = destination;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
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
        return "OperationEntity{" +
            "Id=" + Id +
            ", date=" + date +
            ", type=" + type +
            ", source_id=" + source.getId() +
            ", destination_id=" + destination.getId() +
            ", count=" + count +
            ", cost=" + cost +
            ", comment=" + comment +
            ", created_at=" + created_at +
            '}';
    }

    public static String pos2type(int position) {
        if (position == 1) {
            return "int";
        }

        return "out";
    }
}