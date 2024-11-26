package com.enriclop.kpopbot.repositorio;

import com.enriclop.kpopbot.modelo.PhotoCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPhotoCardRepository extends JpaRepository<PhotoCard, Integer> {

}
