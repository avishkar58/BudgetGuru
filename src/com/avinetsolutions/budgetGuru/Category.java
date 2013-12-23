package com.avinetsolutions.budgetGuru;

import java.io.Serializable;

/**
 * Created by Avishkar on 2013/12/20.
 */
public class Category implements Serializable {
    private int id;
    private String name;
    private CategoryType categoryType;

    public Category(String name, CategoryType categoryType) {
        this.id = -1;
        this.name = name;
        this.categoryType = categoryType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
