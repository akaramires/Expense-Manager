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

import org.eatech.expense.R;
import org.eatech.expense.db.entities.SourceEntity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SourceAdapter<S> extends ArrayAdapter<SourceEntity>
{
    private static final String TAG = "Expense-" + SourceAdapter.class.getSimpleName();
    private final int layout;

    private Context context;

    public SourceAdapter(Context context)
    {
        super(context, R.layout.sherlock_spinner_dropdown_item);
        this.context = context;
        this.layout = R.layout.listview_item;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        SourceEntity sourceEntity = getItem(position);
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

        holder.text1.setText(sourceEntity.getTitle());

        return row;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        SourceEntity sourceEntity = getItem(position);
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

        holder.text1.setText(sourceEntity.getTitle());
        if (sourceEntity.getId() > 0) {
            holder.text2.setText(sourceEntity.getSum_current());
        }

        return row;
    }

    static class ViewHolder
    {
        @InjectView(R.id.text1)
        TextView text1;

        @InjectView(R.id.text2)
        TextView text2;

        public ViewHolder(View view)
        {
            ButterKnife.inject(this, view);
        }
    }
}