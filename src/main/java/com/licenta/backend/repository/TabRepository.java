package com.licenta.backend.repository;

import com.licenta.backend.model.Tab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TabRepository extends JpaRepository<Tab, Long> {

    List<Tab> findByTabindex(int tabindex);
}
