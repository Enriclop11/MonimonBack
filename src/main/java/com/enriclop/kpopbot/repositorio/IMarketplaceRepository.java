package com.enriclop.kpopbot.repositorio;

import com.enriclop.kpopbot.modelo.Marketplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMarketplaceRepository extends JpaRepository<Marketplace, Integer> {
}
