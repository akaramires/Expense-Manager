/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/17/14 11:15 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.db;

import android.content.Context;

public class HelperFactory
{
    static private HelperFactory instance;

    static public void init(Context ctx)
    {
        if (null == instance) {
            instance = new HelperFactory(ctx);
        }
    }

    static public HelperFactory getInstance()
    {
        return instance;
    }

    private DatabaseHelper helper;

    private HelperFactory(Context ctx)
    {
        helper = new DatabaseHelper(ctx);
    }

    public DatabaseHelper getHelper()
    {
        return helper;
    }

    public void releaseHelper()
    {
        helper = null;
    }
}