package com.licenta.backend.controller;

import com.licenta.backend.dto.OrderDTO;
import com.licenta.backend.dto.OrderStatusDTO;
import com.licenta.backend.dto.SalesDTO;
import com.licenta.backend.model.Order;
import com.licenta.backend.service.OrderService;
import com.licenta.backend.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    OrderService orderService;
    ReportService reportService;

    public OrderController(OrderService orderService, ReportService reportService) {
        this.orderService = orderService;
        this.reportService = reportService;
    }

    @PostMapping("/save")
    public void saveOrder(@RequestBody OrderDTO order) {
        orderService.saveOrder(order);
    }

    @PostMapping("/close")
    public void closeOrder(@RequestBody OrderDTO order) {
        orderService.closeOrder(order);
    }

    @GetMapping("/orderByTab/{tabindex}")
    public ResponseEntity getOrderByTabIndex(@PathVariable("tabindex") int tab_index) {
        Order order = orderService.getActiveOrderByTab(tab_index);
        if (order != null) {
            return ResponseEntity.status(HttpStatus.OK).body(order);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/all")
    public List<Order> getOrder() {
        return orderService.getAll();
    }

    @GetMapping("/sales")
    public List<SalesDTO> getSales() {
        List<Order> orders = orderService.getAll();
        List<SalesDTO> result = reportService.getSales(orders);
        return result;
    }

    @GetMapping("/category-sale/{date}")
    public List<SalesDTO> getSalesByCategory(@PathVariable("date") String date) {
        try {
            List<Order> orders = orderService.getAllByDate(date);
            List<SalesDTO> result = reportService.getCategorySales(orders);
            return result;
        } catch (ParseException parseException) {
            return null;
        }
    }

    @GetMapping("/order-status")
    public List<OrderStatusDTO> getOrderStatus() {
        List<OrderStatusDTO> orderStatusList = orderService.getOrderStatus();
        return orderStatusList;
    }


    @GetMapping("/ingredient-sale/{id}")
    public List<SalesDTO> getSalesByCategory(@PathVariable("id") Long id) {
        List<Order> orders = orderService.getAll();
        List<SalesDTO> result = reportService.getIngredientSales(orders, id);
        return result;
    }
}
