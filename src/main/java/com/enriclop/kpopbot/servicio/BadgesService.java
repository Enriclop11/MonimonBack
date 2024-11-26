package com.enriclop.kpopbot.servicio;

import com.enriclop.kpopbot.modelo.Badge;
import com.enriclop.kpopbot.repositorio.IBadgesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BadgesService {

    @Autowired
    private IBadgesRepository badgesRepository;

    public BadgesService(IBadgesRepository badgesRepository) {
        this.badgesRepository = badgesRepository;
    }

    public List<Badge> getBadges() {
        return badgesRepository.findAll();
    }

    public void saveBadge(Badge badge) {
        badgesRepository.save(badge);
    }

    public Badge getBadgeById(Integer id) {
        return badgesRepository.findById(id).get();
    }

    public void deleteBadgeById(Integer id) {
        badgesRepository.deleteById(id);
    }

}
