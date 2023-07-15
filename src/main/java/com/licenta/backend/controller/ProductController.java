package com.licenta.backend.controller;

import com.licenta.backend.dto.ProductDTO;
import com.licenta.backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public List<ProductDTO> getProducts() {
        return productService.getAllProductResponses();
    }

    @PostMapping("/save")
    public ResponseEntity save(@RequestBody ProductDTO product) {
        return productService.saveProduct(product);
    }

    @PostMapping("/save-image")
    public ResponseEntity saveImageByName(@RequestParam("image") MultipartFile image, @RequestParam("name") String name) {
        try {
            return productService.saveImage(image, name);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteProduct(@PathVariable Long id) {
        return productService.delete(id);
    }

}
