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
        Map<String, SalesDTO> priceByDay = new HashMap<>();
        for (Order order : orders) {
            String date = dateFormat.format(order.getDate());
            if (priceByDay.containsKey(date)) {
                SalesDTO salesDTO = priceByDay.get(date);
                double price = salesDTO.getPriceOrder() + getTotal(order);
                salesDTO.setPriceOrder(price);
                priceByDay.put(date, salesDTO);
            } else {
                SalesDTO salesDTO = new SalesDTO();
                salesDTO.setDateOfOrder(order.getDate());
                salesDTO.setPriceOrder(getTotal(order));
                salesDTO.setOrderDate(date);
                priceByDay.put(date, salesDTO);
            }
        }

        List<SalesDTO> sales = new ArrayList<>();
        sales.addAll(priceByDay.values());
        sales.sort(Comparator.comparing(SalesDTO::getDateOfOrder));
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
        Map<String, SalesDTO> ingredientByDate = new HashMap<>();
        for (Order order : orders) {
            if (!order.getProducts().isEmpty()) {
                String date = dateFormat.format(order.getDate());
                List<OrderProduct> products = order.getProducts();
                for (OrderProduct orderProduct : products) {
                    int numberOfProducts = orderProduct.getQuantity();
                    double ingredientQuantity = getIngredientQuantity(orderProduct.getProduct().getIngredients(), id);
                    if (ingredientQuantity != 0) {
                        if (ingredientByDate.containsKey(date)) {
                            SalesDTO salesDTO = ingredientByDate.get(date);
                            double quantity = salesDTO.getIngredientQuantity() + ingredientQuantity * numberOfProducts;
                            salesDTO.setIngredientQuantity(quantity);
                            ingredientByDate.put(date, salesDTO);
                        } else {
                            SalesDTO salesDTO = new SalesDTO();
                            salesDTO.setIngredientQuantity(ingredientQuantity * numberOfProducts);
                            salesDTO.setOrderDate(date);
                            salesDTO.setDateOfOrder(order.getDate());
                            ingredientByDate.put(date, salesDTO);
                        }
                    }
                }
            }
        }

        List<SalesDTO> sales = new ArrayList<>();
        sales.addAll(ingredientByDate.values());
        sales.sort(Comparator.comparing(SalesDTO::getDateOfOrder));
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
