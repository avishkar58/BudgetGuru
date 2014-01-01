package com.avinetsolutions.budgetGuru;

import java.io.Serializable;

/**
 * Created by Avishkar on 2013/12/20.
 */
public class CategoryType implements Serializable{

    public static CategoryType UNKNOWN = new CategoryType(-2, "UNKNOWN");

    private int id;
    private String name;

    public CategoryType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
