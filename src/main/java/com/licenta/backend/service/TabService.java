package com.licenta.backend.service;

import com.licenta.backend.enums.TabStatus;
import com.licenta.backend.model.Tab;
import com.licenta.backend.repository.TabRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TabService {

    private TabRepository tabRepository;

    public TabService(TabRepository tabRepository) {
        this.tabRepository = tabRepository;
    }

    public List<Tab> getAllTabs() {
        return tabRepository.findAll();
    }

    public Tab getTabByIndex(int index) {
        List<Tab> tabs = tabRepository.findByTabindex(index);
        if (!tabs.isEmpty()) {
            return tabs.get(0);
        }
        return null;
    }

    public Tab saveTab(int index, TabStatus tabStatus) {
        Tab tab = getTabByIndex(index);
        if (tab != null && !tabStatus.toString().equals(tab.getStatus())) {
            tab.setStatus(tabStatus.toString());
            return tabRepository.save(tab);
        }
        return tab;
    }
}
