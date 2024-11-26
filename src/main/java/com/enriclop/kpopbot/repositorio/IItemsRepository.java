package com.enriclop.kpopbot.repositorio;

import com.enriclop.kpopbot.modelo.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IItemsRepository extends JpaRepository<Items, Integer> {
}
