package com.enriclop.kpopbot.kpopDB;

import com.enriclop.kpopbot.modelo.Badge;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KpopBadges {

    @Autowired
    KpopService kpopService;

    public User checkBadges(User user) {

        List<Badge> badges = kpopService.getBadges();

        for (Badge badge : badges) {
            if (user.getBadges().stream().anyMatch(b -> b.getName().equals(badge.getName()))) {
                continue;
            }

            if (checkRequirements(user, badge)) {
                user.addBadge(badge);
            }
        }

        return user;
    }

    public static boolean checkRequirements(User user, Badge badge) {

        List<String> requirements = new ArrayList<>(badge.getRequirements());

        // The String list are the idolIds that the user has to have in order to get the badge

        for (PhotoCard card : user.getPhotoCards()) {
            requirements.remove(card.getIdolID());

            if (requirements.isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
