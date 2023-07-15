package com.licenta.backend.dto;

import java.util.List;

public class ProductDTO {

    private Long id;
    private String name;
    private double price;
    private String category;
    private String imageName;
    private List<ProductIngredientDTO> ingredients;
    private String ingredientsDescription;
    private byte[] picture;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public List<ProductIngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ProductIngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public String getIngredientsDescription() {
        return ingredientsDescription;
    }

    public void setIngredientsDescription(String ingredientsDescription) {
        this.ingredientsDescription = ingredientsDescription;
    }
}
