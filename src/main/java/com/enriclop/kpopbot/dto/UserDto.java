package com.enriclop.kpopbot.dto;

import com.enriclop.kpopbot.modelo.Badge;
import com.enriclop.kpopbot.modelo.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {

    private int id;

    private String twitchId;

    private String username;

    private String dcUsername;

    private Integer score;

    private String avatar;

    private int photoCards;

    private List<Badge> badges;

    UserDto(User user) {
        this.id = user.getId();
        this.twitchId = user.getTwitchId();
        this.username = user.getUsername();
        this.dcUsername = user.getDcUsername();
        this.score = user.getScore();
        this.avatar = user.getAvatar();
        this.photoCards = user.getPhotoCards().size();
        this.badges = user.getBadges();
    }

    public static UserDto fromUser(User user) {
        return new UserDto(user);
    }

    public static List<UserDto> fromUsers(List<User> users) {
        return users.stream().map(UserDto::new).toList();
    }
}
