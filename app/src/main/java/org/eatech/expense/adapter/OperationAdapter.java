/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/18/14 9:12 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.eatech.expense.R;
import org.eatech.expense.db.entities.OperationEntity;
import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OperationAdapter extends ArrayAdapter<OperationEntity>
{
    private static final String TAG = "Expense-" + OperationAdapter.class.getSimpleName();
    private final int layout;

    private Context context;

    public OperationAdapter(Context context)
    {
        super(context, R.layout.fragment_operations_list_item);
        this.context = context;
        this.layout = R.layout.fragment_operations_list_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        OperationEntity operationEntity = getItem(position);
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(this.layout, parent, false);

            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        try {
            holder.tvTitle.setText(operationEntity.getDestination().getTitle());

            DateFormat dfDay = new SimpleDateFormat("d");
            String dateDay = dfDay.format(new Date(operationEntity.getDate()));
            holder.tvDateDay.setText(dateDay);

            DateFormat dfMonth = new SimpleDateFormat("MMM");
            String dateMonth = dfMonth.format(new Date(operationEntity.getDate()));
            holder.tvDateMonth.setText(dateMonth);

            if (operationEntity.getType().equals("out")) {
                holder.tvSum.setTextColor(Color.parseColor("red"));
                holder.tvSum.setText("-" + String.valueOf(operationEntity.getCost() * operationEntity.getCount()));
            } else if (operationEntity.getType().equals("in")) {
                holder.tvSum.setTextColor(context.getResources().getColor(R.color.blue));
                holder.tvSum.setText(String.valueOf(operationEntity.getCost() * operationEntity.getCount()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return row;
    }

    static class ViewHolder
    {
        @InjectView(R.id.tvTitle)
        TextView tvTitle;

        @InjectView(R.id.tvSum)
        TextView tvSum;

        @InjectView(R.id.tvDateDay)
        TextView tvDateDay;

        @InjectView(R.id.tvDateMonth)
        TextView tvDateMonth;

        public ViewHolder(View view)
        {
            ButterKnife.inject(this, view);
        }
    }
}