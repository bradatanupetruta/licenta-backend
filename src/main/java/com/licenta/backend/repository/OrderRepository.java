package com.licenta.backend.repository;

import com.licenta.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT customer_order from Order customer_order where customer_order.tab.tabindex = :tabIndex and customer_order.status = :status")
    List<Order> findOrderByTableIndex(@Param("tabIndex") int tabIndex, @Param("status") String status);

    @Query("SELECT customer_order from Order customer_order where customer_order.date >= :startDate and customer_order.date <= :endDate")
    List<Order> findOrderByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
