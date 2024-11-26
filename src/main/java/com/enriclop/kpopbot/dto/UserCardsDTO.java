package com.enriclop.kpopbot.dto;

import com.enriclop.kpopbot.modelo.Badge;
import com.enriclop.kpopbot.modelo.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserCardsDTO {

    private int id;

    private String twitchId;

    private String username;

    private String dcUsername;

    private Integer score;

    private String avatar;

    private List<Integer> photoCardSelected;

    private List<PhotoCardDTO> photoCards;

    private List<Badge> badges;

    public UserCardsDTO(User user) {
        this.id = user.getId();
        this.twitchId = user.getTwitchId();
        this.username = user.getUsername();
        this.dcUsername = user.getDcUsername();
        this.score = user.getScore();
        this.avatar = user.getAvatar();
        this.photoCardSelected = user.getSelectedCards();
        this.photoCards = PhotoCardDTO.fromPhotoCards(user.getPhotoCards());
        this.badges = user.getBadges();
    }

    public static List<UserCardsDTO> fromUsers(List<User> users) {
        return users.stream().map(UserCardsDTO::new).toList();
    }
}
