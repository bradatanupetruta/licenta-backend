package com.licenta.backend.dto;

public class SalesDTO {

    private String orderDate;
    private double priceOrder;
    private String category;
    private int categoryQuantity;
    private double ingredientQuantity;

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getPriceOrder() {
        return priceOrder;
    }

    public void setPriceOrder(double priceOrder) {
        this.priceOrder = priceOrder;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCategoryQuantity() {
        return categoryQuantity;
    }

    public void setCategoryQuantity(int categoryQuantity) {
        this.categoryQuantity = categoryQuantity;
    }

    public double getIngredientQuantity() {
        return ingredientQuantity;
    }

    public void setIngredientQuantity(double ingredientQuantity) {
        this.ingredientQuantity = ingredientQuantity;
    }
}
