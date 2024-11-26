package com.enriclop.kpopbot.repositorio;

import com.enriclop.kpopbot.modelo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
    @Query("select u from User u where u.username like ?1")
    User findByUsernameLike(String username);

    @Query("select u from User u where u.twitchId like ?1")
    User findByTwitchIdLike(String twitchId);

    @Query("select u from User u where u.dcUsername like ?1")
    User findByDcUsernameLike(String dcUsername);
}
