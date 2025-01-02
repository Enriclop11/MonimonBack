package com.enriclop.kpopbot.dto;

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
    private Integer score;
    private String avatar;
    private long photoCards;

    public UserDto(int id, String twitchId, String username, Integer score, String avatar, long photoCards) {
        this.id = id;
        this.twitchId = twitchId;
        this.username = username;
        this.score = score;
        this.avatar = avatar;
        this.photoCards = photoCards;
    }

    public static List<UserDto> fromUsers(List<User> users) {
        return users.stream().map(user -> new UserDto(user.getId(), user.getTwitchId(), user.getUsername(), user.getScore(), user.getAvatar(), user.getPhotoCards().size())).toList();
    }
}