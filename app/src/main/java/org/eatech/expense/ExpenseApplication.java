/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:58 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense;

import android.app.Application;

import org.eatech.expense.db.HelperFactory;

public class ExpenseApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        HelperFactory.init(getApplicationContext());
    }

    @Override
    public void onTerminate()
    {
        HelperFactory.getInstance().releaseHelper();
        super.onTerminate();
    }
}