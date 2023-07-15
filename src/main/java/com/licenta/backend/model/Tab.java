package com.licenta.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tab")
public class Tab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int tabindex;

    @Column(nullable = false)
    private String status;

    public Long getId() {
        return id;
    }

    public int getTabindex() {
        return tabindex;
    }

    public void setTabindex(int tabindex) {
        this.tabindex = tabindex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
