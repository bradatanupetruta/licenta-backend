package com.licenta.backend.controller;

import com.licenta.backend.enums.TabStatus;
import com.licenta.backend.model.Tab;
import com.licenta.backend.service.TabService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tab")
public class TabController {

    TabService tabService;

    public TabController(TabService tabService) {
        this.tabService = tabService;
    }

    @GetMapping("/all")
    public List<Tab> getTabs() {
        return tabService.getAllTabs();
    }

    @PostMapping("/busy")
    public void saveBusyTab(@RequestBody int tab_index) {
        tabService.saveTab(tab_index, TabStatus.BUSY);
    }

    @PostMapping("/vacant")
    public void saveVacantTab(@RequestBody int tab_index) {
        tabService.saveTab(tab_index, TabStatus.VACANT);
    }
}
