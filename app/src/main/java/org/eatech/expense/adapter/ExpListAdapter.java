/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/25/14 10:56 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.eatech.expense.R;
import org.eatech.expense.db.entities.DestinationEntity;

import java.util.List;

public class ExpListAdapter extends BaseExpandableListAdapter
{

    private List<CategoryDestionations> mCats;
    private Context                     mContext;

    public ExpListAdapter(Context context, List<CategoryDestionations> cats)
    {
        mContext = context;
        mCats = cats;
    }

    @Override
    public int getGroupCount()
    {
        return mCats.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return mCats.get(groupPosition).getDestinations().size();
    }

    @Override
    public CategoryDestionations getGroup(int groupPosition)
    {
        return mCats.get(groupPosition);
    }

    @Override
    public DestinationEntity getChild(int groupPosition, int childPosition)
    {
        return mCats.get(groupPosition).getDestinations().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent)
    {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.explistview_group_view, null);
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.groupTitle);
        textGroup.setText(getGroup(groupPosition).getCategory().getTitle());

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent)
    {
        DestinationEntity child = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.explistview_child_view, null);
        }

        TextView textChild = (TextView) convertView.findViewById(R.id.childTitle);
        LinearLayout in = (LinearLayout) convertView.findViewById(R.id.in);
        LinearLayout out = (LinearLayout) convertView.findViewById(R.id.out);

        textChild.setText(child.getTitle());

        if (child.getType().equals("in")) {
            in.setVisibility(View.VISIBLE);
            out.setVisibility(View.GONE);
        } else if (child.getType().equals("out")) {
            in.setVisibility(View.GONE);
            out.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
