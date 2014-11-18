/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.eatech.expense.fragments.FragmentBudgets;
import org.eatech.expense.fragments.FragmentForm;
import org.eatech.expense.fragments.FragmentOperations;
import org.eatech.expense.fragments.FragmentOrders;
import org.eatech.expense.fragments.FragmentReports;
import org.eatech.expense.R;

public class PagerAdapter extends FragmentStatePagerAdapter
{
    private static String[] titles;

    public PagerAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        titles = new String[] {
            context.getString(R.string.action_add),
            context.getString(R.string.screen_operations),
            context.getString(R.string.screen_orders),
            context.getString(R.string.screen_budgets),
            context.getString(R.string.screen_reports)
        };
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new FragmentForm();
                break;
            case 1:
                fragment = new FragmentOperations();
                break;
            case 2:
                fragment = new FragmentOrders();
                break;
            case 3:
                fragment = new FragmentBudgets();
                break;
            case 4:
                fragment = new FragmentReports();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount()
    {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titles[position];
    }
}
