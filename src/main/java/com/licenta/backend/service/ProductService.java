package com.licenta.backend.service;

import com.licenta.backend.dto.ProductDTO;
import com.licenta.backend.dto.ProductIngredientDTO;
import com.licenta.backend.dto.ProductMapper;
import com.licenta.backend.model.Ingredient;
import com.licenta.backend.model.Product;
import com.licenta.backend.model.ProductIngredient;
import com.licenta.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static String imageLocation = System.getProperty("user.dir") + "/images/";

    private ProductRepository productRepository;

    private IngredientService ingredientService;

    private ProductIngredientService productIngredientService;

    public ProductService(ProductRepository productRepository, IngredientService ingredientService, ProductIngredientService productIngredientService) {
        this.productRepository = productRepository;
        this.ingredientService = ingredientService;
        this.productIngredientService = productIngredientService;
    }

    public List<ProductDTO> getAllProductResponses() {
        ProductMapper mapper = new ProductMapper();
        List<Product> products = productRepository.findAll();
        Map<String, byte[]> imageMap = getImageMap(products);
        List<ProductDTO> responses = mapper.mapResponses(products, imageMap);
        return responses;
    }

    public List<Product> getProductsById(Set<Long> ids) {
        return productRepository.findAllById(ids);
    }

    public ResponseEntity delete(Long id) {
        Product product = productRepository.getReferenceById(id);
        List<Long> ingredientsIds = product.getIngredients().stream().map(productIngredient -> productIngredient.getId()).collect(Collectors.toList());
        try {
            productRepository.delete(product);
            if (ingredientsIds != null && !ingredientsIds.isEmpty()) {
                productIngredientService.deleteAll(ingredientsIds);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Path imagePath = Paths.get(imageLocation, product.getImg());
        try {
            if (Files.isReadable(imagePath)) {
                Files.delete(imagePath);
            }
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity saveProduct(ProductDTO productDTO) {
        Product product = getProductToSave(productDTO.getId());
        List<Long> productIngredientsToDelete = new ArrayList<>();
        List<ProductIngredient> savedProductIngredients = saveProductIngredients(product, productDTO.getIngredients(), productIngredientsToDelete);
        if (!savedProductIngredients.isEmpty()) {
            product.setIngredients(savedProductIngredients);
        }
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        if (productDTO.getImageName() != null) {
            product.setImg(productDTO.getImageName());
        }
        Product response = productRepository.save(product);
        // deleting existing productIngredient with quantity = 0
        if (!productIngredientsToDelete.isEmpty()) {
            productIngredientService.deleteAll(productIngredientsToDelete);
        }
        return ResponseEntity.ok(response);
    }

    public ResponseEntity saveImage(MultipartFile image, String imageName) throws IOException {
        createDirectory();
        Path imagePath = Paths.get(imageLocation, imageName);
        try {
            Files.write(imagePath, image.getBytes());
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private List<ProductIngredient> saveProductIngredients(Product product, List<ProductIngredientDTO> ingredientDTOS, List<Long> productIngredientsToDelete) {
        List<ProductIngredient> productIngredients = new ArrayList<>();
        Map<Long, Double> productIngredientMap = getIngredientQuantityMap(ingredientDTOS);
        prepareProductIngredientsForUpdate(productIngredients, productIngredientsToDelete, product, productIngredientMap);

        List<Ingredient> ingredients = ingredientService.getIngredientByIds(productIngredientMap.keySet());
        List<Long> updateIds = productIngredients.stream().map(productIngredient -> productIngredient.getIngredient().getId()).collect(Collectors.toList());
        for (Ingredient ingredient : ingredients) {
            if (!updateIds.contains(ingredient.getId())) {
                ProductIngredient productIngredient = new ProductIngredient(ingredient, productIngredientMap.get(ingredient.getId()));
                productIngredients.add(productIngredient);
            }
        }

        if (!productIngredients.isEmpty()) {
            return productIngredientService.saveAll(productIngredients);
        }
        return new ArrayList<>();
    }

    private void prepareProductIngredientsForUpdate(List<ProductIngredient> productIngredients, List<Long> productIngredientsToDelete,
                                                    Product product, Map<Long, Double> productIngredientMap) {
        if (product.getIngredients() != null && !product.getIngredients().isEmpty()) {
            for (ProductIngredient productIngredient : product.getIngredients()) {
                if (productIngredientMap.containsKey(productIngredient.getIngredient().getId())) {
                    Double quantity = productIngredientMap.get(productIngredient.getIngredient().getId());
                    productIngredient.setQuantity(quantity);
                    productIngredients.add(productIngredient);
                } else {
                    productIngredientsToDelete.add(productIngredient.getId());
                }
            }
        }
    }

    private Product getProductToSave(Long id) {
        if (id != null) {
            return productRepository.findById(id).orElse(new Product());
        }
        return new Product();
    }

    private Map<String, byte[]> getImageMap(List<Product> products) {
        Map<String, byte[]> imageMap = new HashMap<>();
        for (Product product : products) {
            imageMap.put(product.getImg(), getImage(product.getImg()));
        }
        return imageMap;
    }

    private Map<Long, Double> getIngredientQuantityMap(List<ProductIngredientDTO> ingredientDTOS) {
        Map<Long, Double> resultMap = new HashMap<>();
        for (ProductIngredientDTO productIngredientDTO : ingredientDTOS) {
            if (productIngredientDTO.getQuantity() > 0) {
                resultMap.put(productIngredientDTO.getId(), productIngredientDTO.getQuantity());
            }
        }
        return resultMap;
    }

    private byte[] getImage(String image) {
        Path imagePath = Paths.get(imageLocation, image);
        try {
            return Files.readAllBytes(imagePath);
        } catch (IOException e) {
            return null;
        }
    }

    private String getExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1))
                .orElse("jpeg");
    }

    private void createDirectory() {
        File directory = new File(imageLocation);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
}
