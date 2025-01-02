package com.enriclop.kpopbot.repositorio;

import com.enriclop.kpopbot.modelo.Idol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IIdolRepository extends JpaRepository<Idol, Integer> {

    @Query("select i from Idol i where i.isActive = true")
    List<Idol> findByIsActiveTrue();

    @Query("select i from Idol i where i.id = ?1")
    Idol findById(String id);
}
