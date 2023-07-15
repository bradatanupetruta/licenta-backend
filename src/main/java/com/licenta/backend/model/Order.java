package com.licenta.backend.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "customer_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderid;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Date date;

    @ManyToOne
    private User server;

    @ManyToOne
    private Tab tab;

    @OneToMany
    private List<OrderProduct> products;

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getServer() {
        return server;
    }

    public void setServer(User server) {
        this.server = server;
    }

    public Tab getTable() {
        return tab;
    }

    public void setTable(Tab tab) {
        this.tab = tab;
    }

    public List<OrderProduct> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProduct> products) {
        this.products = products;
    }
}
