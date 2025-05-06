package com.enriclop.kpopbot.dto;

import com.enriclop.kpopbot.modelo.Badge;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserProfileDto {

    private int id;

    private String twitchId;

    private String username;

    private String dcUsername;

    private Integer score;

    private String avatar;

    private List<PhotoCardDTO> photoCardSelected;

    private int photoCards;

    private List<Badge> badges;

    private boolean isModerator;

    UserProfileDto(User user, boolean isModerator) {
        this.id = user.getId();
        this.twitchId = user.getTwitchId();
        this.username = user.getUsername();
        this.dcUsername = user.getDcUsername();
        this.score = user.getScore();
        this.avatar = user.getAvatar();
        this.photoCards = user.getPhotoCards().size();
        this.isModerator = isModerator;

        List<PhotoCard> userPhotoCards = user.getPhotoCards();
        List<Integer> selectedCards = user.getSelectedCards();

        List<PhotoCardDTO> photoCardSelected = new ArrayList<>();
        for (PhotoCard photoCard : userPhotoCards) {
            if (selectedCards.contains(photoCard.getId())) {
                photoCardSelected.add(new PhotoCardDTO(photoCard));
            }
        }

        this.photoCardSelected = photoCardSelected;

        this.badges = user.getBadges();
    }

    public static UserProfileDto fromUser(User user, boolean isModerator) {
        return new UserProfileDto(user, isModerator);
    }

    public static List<UserProfileDto> fromUsers(List<User> users, List<String> moderatorIds) {
        return users.stream().map(user -> {
            boolean isModerator = moderatorIds.contains(user.getUsername());
            return new UserProfileDto(user, isModerator);
        }).toList();
    }
}
