package com.licenta.backend.service;

import com.licenta.backend.model.OrderProduct;
import com.licenta.backend.repository.OrderProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderProductService {

    private OrderProductRepository orderProductRepository;

    public OrderProductService(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    public List<OrderProduct> saveAll(List<OrderProduct> orderProducts) {
        return orderProductRepository.saveAll(orderProducts);
    }
}
