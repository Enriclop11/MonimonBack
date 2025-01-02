package com.enriclop.kpopbot.servicio;


import com.enriclop.kpopbot.dto.MarketplaceDTO;
import com.enriclop.kpopbot.modelo.Marketplace;
import com.enriclop.kpopbot.repositorio.IMarketplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketplaceService {

    @Autowired
    private IMarketplaceRepository marketplaceRepository;

    public List<Marketplace> getMarketplace() {
        return marketplaceRepository.findAll();
    }

    public List<MarketplaceDTO> getMarketplaceDTO() {
        return marketplaceRepository.findAllDTO();
    }

    public Marketplace getMarketplaceById(int id) {
        return marketplaceRepository.findById(id).get();
    }

    public void saveMarketplace(Marketplace marketplace) {
        marketplaceRepository.save(marketplace);
    }

    public void deleteMarketplace(int id) {
        marketplaceRepository.deleteById(id);
    }
}
