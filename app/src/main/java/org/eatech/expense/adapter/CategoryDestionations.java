/**
 * Created by Elmar <a.k.a. Ramires> Abdurayimov on 11/25/14 11:31 PM
 * @copyright (C)Copyright 2014 e.abdurayimov@gmail.com
 */

package org.eatech.expense.adapter;

import org.eatech.expense.db.entities.CategoryEntity;
import org.eatech.expense.db.entities.DestinationEntity;

import java.util.ArrayList;
import java.util.List;

public class CategoryDestionations
{
    private CategoryEntity          category     = null;
    private List<DestinationEntity> destinations = null;

    public CategoryDestionations(CategoryEntity category)
    {
        this.category = category;
        this.destinations = new ArrayList<DestinationEntity>();
    }

    public CategoryEntity getCategory()
    {
        return category;
    }

    public List<DestinationEntity> getDestinations()
    {
        return destinations;
    }

    public void addDestination(DestinationEntity destination)
    {
        this.destinations.add(destination);
    }
}
