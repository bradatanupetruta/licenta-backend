package com.licenta.backend.controller;

import com.licenta.backend.model.Ingredient;
import com.licenta.backend.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {

    IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/all")
    public List<Ingredient> getIngredients() {
        return ingredientService.getAllIngredients();
    }

    @PostMapping("/save")
    public ResponseEntity saveIngredient(@RequestBody Ingredient ingredient) {
        return ingredientService.saveIngredient(ingredient);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteIngredient(@PathVariable Long id) {
        return ingredientService.deleteIngredient(id);
    }
}
