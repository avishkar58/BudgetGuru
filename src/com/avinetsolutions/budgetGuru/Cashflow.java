package com.avinetsolutions.budgetGuru;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Avishkar on 2013/12/20.
 */
public class Cashflow implements Serializable{
    private int id;
    private Date date;
    private double amount;
    private String description;
    private Category category;

    public Cashflow(Date date, double amount, String description, Category category) {
        this.id = -1;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
