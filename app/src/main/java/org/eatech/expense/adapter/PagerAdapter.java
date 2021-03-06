/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/12/14 9:23 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.eatech.expense.fragments.FragmentDestination;
import org.eatech.expense.fragments.FragmentForm;
import org.eatech.expense.fragments.FragmentOperations;
import org.eatech.expense.fragments.FragmentReportsIn;
import org.eatech.expense.fragments.FragmentReportsOut;
import org.eatech.expense.fragments.FragmentSource;
import org.eatech.expense.R;

public class PagerAdapter extends FragmentStatePagerAdapter
{
    private static String[] titles;

    public PagerAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        titles = new String[] {
            context.getString(R.string.screen_sources),
            context.getString(R.string.screen_destinations),
            context.getString(R.string.action_add),
            context.getString(R.string.screen_operations),
            context.getString(R.string.screen_outs),
            context.getString(R.string.screen_ins)
        };
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new FragmentSource();
                break;
            case 1:
                fragment = new FragmentDestination();
                break;
            case 2:
                fragment = new FragmentForm();
                break;
            case 3:
                fragment = new FragmentOperations();
                break;
            case 4:
                fragment = new FragmentReportsOut();
                break;
            case 5:
                fragment = new FragmentReportsIn();
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
