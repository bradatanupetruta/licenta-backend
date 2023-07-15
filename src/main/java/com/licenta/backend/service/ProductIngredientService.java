package com.licenta.backend.service;

import com.licenta.backend.model.ProductIngredient;
import com.licenta.backend.repository.ProductIngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductIngredientService {

    private ProductIngredientRepository productIngredientRepository;

    public ProductIngredientService(ProductIngredientRepository productIngredientRepository) {
        this.productIngredientRepository = productIngredientRepository;
    }

    public List<ProductIngredient> saveAll(List<ProductIngredient> productIngredients) {
        return productIngredientRepository.saveAll(productIngredients);
    }

    public void deleteAll(List<Long> ids) {
        List<ProductIngredient> productIngredients = productIngredientRepository.findAllById(ids);
        productIngredientRepository.deleteAll(productIngredients);
    }
}
