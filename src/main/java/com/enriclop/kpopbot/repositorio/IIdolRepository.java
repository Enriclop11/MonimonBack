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



    @Query("select i from Idol i where i.isActive = true and i.popularity between ?1 and ?2")
    List<Idol> findByIsActiveTrueAndPopularityBetween(int popularityStart, int popularityEnd);

    @Query("select i from Idol i where i.name = ?1")
    Idol findByName(String name);

    @Query("select i from Idol i where i.apiName = ?1")
    List<Idol> findByApiName(String apiName);

}
