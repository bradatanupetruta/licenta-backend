package com.licenta.backend.dto;

import com.licenta.backend.model.Product;
import com.licenta.backend.model.ProductIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class ProductMapper {

    public ProductDTO mapResponse(Product product) {
        ProductDTO response = new ProductDTO();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setCategory(product.getCategory());
        response.setImageName(product.getImg());
        if (!product.getIngredients().isEmpty()) {
            List<ProductIngredientDTO> ingredientDTOS = mapIngredients(product.getIngredients());
            response.setIngredients(ingredientDTOS);
            response.setIngredientsDescription(getIngredientsDescription(ingredientDTOS));
        }
        return response;
    }

    public List<ProductDTO> mapResponses(List<Product> products, Map<String, byte[]> imageMap) {
        List<ProductDTO> responses = new ArrayList<>();
        for (Product product : products) {
            ProductDTO response = mapResponse(product);
            response.setPicture(imageMap.get(product.getImg()));
            responses.add(response);
        }
        return responses;
    }

    public ProductIngredientDTO mapProductIngredient(ProductIngredient productIngredient) {
        ProductIngredientDTO productIngredientDTO = new ProductIngredientDTO();
        productIngredientDTO.setId(productIngredient.getIngredient().getId());
        productIngredientDTO.setQuantity(productIngredient.getQuantity());
        productIngredientDTO.setName(productIngredient.getIngredient().getName());
        return productIngredientDTO;
    }

    public List<ProductIngredientDTO> mapIngredients(List<ProductIngredient> productIngredients) {
        List<ProductIngredientDTO> productIngredientDTOList = new ArrayList<>();
        for (ProductIngredient productIngredient : productIngredients) {
            ProductIngredientDTO productIngredientDTO = mapProductIngredient(productIngredient);
            productIngredientDTOList.add(productIngredientDTO);
        }
        return productIngredientDTOList;
    }

    private String getIngredientsDescription(List<ProductIngredientDTO> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return "";
        }

        StringJoiner ingredientDisplay = new StringJoiner(", ");
        for (ProductIngredientDTO ingredient : ingredients) {
            ingredientDisplay.add(ingredient.getName());
        }
        return ingredientDisplay.toString();
    }
}
