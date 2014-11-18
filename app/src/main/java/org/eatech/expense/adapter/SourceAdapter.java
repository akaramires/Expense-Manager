/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/18/14 9:12 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.eatech.expense.db.entities.SourceEntity;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SourceAdapter<S> extends ArrayAdapter<SourceEntity>
{
    private static final String TAG = "Expense-" + SourceAdapter.class.getSimpleName();

    private Context context;

    public SourceAdapter(Context c)
    {
        super(c, android.R.layout.simple_list_item_1);
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        SourceEntity sourceEntity = getItem(position);
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        try {
            holder.text.setText(sourceEntity.getTitle() + " (" + sourceEntity.getCurrency().getCode() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return row;
    }

    static class ViewHolder
    {
        @InjectView(android.R.id.text1)
        TextView text;

        public ViewHolder(View view)
        {
            ButterKnife.inject(this, view);
        }
    }
}