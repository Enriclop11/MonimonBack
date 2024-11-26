package com.enriclop.kpopbot.modelo;

import com.enriclop.kpopbot.dto.BadgeDTO;
import com.enriclop.kpopbot.utilities.Utilities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String twitchId;

    private String username;

    private String dcUsername;

    private Integer score;

    private String avatar;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "selected_cards", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "card_id")
    private List<Integer> selectedCards = new ArrayList<>();

    @JsonIgnore
    private String password;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PhotoCard> photoCards = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Badge> badges = new ArrayList<>();

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "items_id")
    private Items items;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "watchtime_id")
    private WatchTime watchTime;

    public User() {
    }

    public User(String twitchId ,String username, String avatar) {
        this.twitchId = twitchId;
        this.username = username;
        this.score = 0;
        this.avatar = avatar;

        this.items = new Items();
        this.watchTime = new WatchTime();
    }

    public void addScore(Integer score) {
        this.score += score;
    }

    public void minusScore(Integer score) {
        this.score -= score;
    }

    public String getUsernameDisplay() {
        return Utilities.firstLetterToUpperCase(this.username);
    }

    public void setPassword(String password) {
        this.password = Utilities.hashPassword(password);
    }

    public void setSelectedCards(List<Integer> cardIds) {
        if (cardIds.size() <= 3) {
            this.selectedCards = cardIds;
        } else {
            throw new IllegalArgumentException("You can only select up to 3 cards.");
        }
    }

    public void addBadge(BadgeDTO badge) {
        Badge newBadge = new Badge(badge);
        this.badges.add(newBadge);
    }

}
