package com.enriclop.kpopbot.repositorio;

import com.enriclop.kpopbot.modelo.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBadgesRepository extends JpaRepository<Badge, Integer> {


}
