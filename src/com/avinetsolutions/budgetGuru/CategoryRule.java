package com.avinetsolutions.budgetGuru;

/**
 * Created by Avishkar on 2013/12/23.
 */
public class CategoryRule {

    private int id, order;
    private Category category;
    private String containsText;

    public CategoryRule(int order, Category category, String containsText) {
        this.order = order;
        this.category = category;
        this.containsText = containsText;
        this.id = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getContainsText() {
        return containsText;
    }

    public void setContainsText(String containsText) {
        this.containsText = containsText;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
