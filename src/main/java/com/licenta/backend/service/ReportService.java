package com.licenta.backend.service;

import com.licenta.backend.dto.SalesDTO;
import com.licenta.backend.model.Order;
import com.licenta.backend.model.OrderProduct;
import com.licenta.backend.model.ProductIngredient;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReportService {

    public List<SalesDTO> getSales(List<Order> orders) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Map<String, Double> priceByDay = new HashMap<>();
        for (Order order : orders) {
            String date = dateFormat.format(order.getDate());
            if (priceByDay.containsKey(date)) {
                double price = priceByDay.get(date) + getTotal(order);
                priceByDay.put(date, price);
            } else {
                priceByDay.put(date, getTotal(order));
            }
        }

        List<SalesDTO> sales = new ArrayList<>();
        for (Map.Entry<String, Double> entry : priceByDay.entrySet()) {
            SalesDTO salesDTO = new SalesDTO();
            salesDTO.setOrderDate(entry.getKey());
            salesDTO.setPriceOrder(entry.getValue());
            sales.add(salesDTO);
        }
        sales.sort(Comparator.comparing(SalesDTO::getOrderDate));
        return sales;
    }

    public List<SalesDTO> getCategorySales(List<Order> orders) {
        Map<String, Integer> productsByCategory = new HashMap<>();
        for (Order order : orders) {
            if (!order.getProducts().isEmpty()) {
                List<OrderProduct> products = order.getProducts();
                for (OrderProduct orderProduct : products) {
                    if (productsByCategory.containsKey(orderProduct.getProduct().getCategory())) {
                        int quantity = productsByCategory.get(orderProduct.getProduct().getCategory()) + orderProduct.getQuantity();
                        productsByCategory.put(orderProduct.getProduct().getCategory(), quantity);
                    } else {
                        productsByCategory.put(orderProduct.getProduct().getCategory(), orderProduct.getQuantity());
                    }
                }
            }
        }

        List<SalesDTO> sales = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : productsByCategory.entrySet()) {
            SalesDTO salesDTO = new SalesDTO();
            salesDTO.setCategory(entry.getKey());
            salesDTO.setCategoryQuantity(entry.getValue());
            sales.add(salesDTO);
        }
        return sales;
    }

    public List<SalesDTO> getIngredientSales(List<Order> orders, Long id) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Map<String, Double> ingredientByDate = new HashMap<>();
        for (Order order : orders) {
            if (!order.getProducts().isEmpty()) {
                String date = dateFormat.format(order.getDate());
                List<OrderProduct> products = order.getProducts();
                for (OrderProduct orderProduct : products) {
                    int numberOfProducts = orderProduct.getQuantity();
                    double ingredientQuantity = getIngredientQuantity(orderProduct.getProduct().getIngredients(), id);
                    if (ingredientQuantity != 0) {
                        if (ingredientByDate.containsKey(date)) {
                            double quantity = ingredientByDate.get(date) + ingredientQuantity * numberOfProducts;
                            ingredientByDate.put(date, quantity);
                        } else {
                            ingredientByDate.put(date, ingredientQuantity * numberOfProducts);
                        }
                    }
                }
            }
        }

        List<SalesDTO> sales = new ArrayList<>();
        for (Map.Entry<String, Double> entry : ingredientByDate.entrySet()) {
            SalesDTO salesDTO = new SalesDTO();
            salesDTO.setOrderDate(entry.getKey());
            salesDTO.setIngredientQuantity(entry.getValue());
            sales.add(salesDTO);
        }
        sales.sort(Comparator.comparing(SalesDTO::getOrderDate));
        return sales;
    }

    private double getIngredientQuantity(List<ProductIngredient> productIngredients, Long ingredientId) {
        for (ProductIngredient productIngredient : productIngredients) {
            if (productIngredient.getIngredient().getId() == ingredientId) {
                return productIngredient.getQuantity();
            }
        }
        return 0;
    }

    private double getTotal(Order order) {
        double total = 0;
        for (OrderProduct product : order.getProducts()) {
            total = total + product.getQuantity() * product.getProduct().getPrice();
        }
        return total;
    }
}
