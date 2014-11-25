package org.eatech.expense;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.eatech.expense.adapter.PagerAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends SherlockFragmentActivity
{
    @InjectView(R.id.pager)
    public ViewPager mViewPager;

    PagerAdapter mDemoCollectionPagerAdapter;

    public int tmp_oper_id = 0;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mDemoCollectionPagerAdapter = new PagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setCurrentItem(2);
    }
}
