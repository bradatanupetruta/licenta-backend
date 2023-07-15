package com.licenta.backend.dto;

import java.util.List;

public class OrderDTO {
    private Long userId;
    private int tabIndex;
    private Long orderId;
    private String tabStatus;
    private String status;
    private List<OrderProductDTO> products;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTabStatus() {
        return tabStatus;
    }

    public void setTabStatus(String tabStatus) {
        this.tabStatus = tabStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProductDTO> products) {
        this.products = products;
    }
}
