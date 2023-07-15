package com.licenta.backend.service;

import com.licenta.backend.model.Ingredient;
import com.licenta.backend.repository.IngredientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class IngredientService {

    private IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public ResponseEntity saveIngredient(Ingredient ingredient) {
        if (ingredient.getId() != null) {
            Ingredient updateIngredient = ingredientRepository.findById(ingredient.getId()).get();
            updateIngredient.setName(ingredient.getName());
            updateIngredient.setQuantity(ingredient.getQuantity());
            ingredientRepository.save(updateIngredient);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        ingredientRepository.save(ingredient);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity deleteIngredient(Long id) {
        try {
            Ingredient ingredient = ingredientRepository.getReferenceById(id);
            ingredientRepository.delete(ingredient);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    public List<Ingredient> saveIngredients(List<Ingredient> ingredients) {
        return ingredientRepository.saveAll(ingredients);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public List<Ingredient> getIngredientByIds(Set<Long> ids) {
        return ingredientRepository.findAllById(ids);
    }
}
