package com.enriclop.kpopbot.repositorio;

import com.enriclop.kpopbot.dto.MarketplaceDTO;
import com.enriclop.kpopbot.modelo.Marketplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMarketplaceRepository extends JpaRepository<Marketplace, Integer> {

    @Query("SELECT new com.enriclop.kpopbot.dto.MarketplaceDTO(m.id, m.card.idolID, m.card.name, m.card.apiName, m.card.fullName, m.card.band, m.card.photo, m.card.hp, m.card.defense, m.card.attack, m.card.type, m.card.type2, m.card.popularity, m.price, m.card.user.id, m.card.user.username) FROM Marketplace m")
    public List<MarketplaceDTO> findAllDTO();

}
