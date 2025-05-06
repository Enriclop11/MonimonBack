package com.enriclop.kpopbot.security;

import com.enriclop.kpopbot.dto.AdminUser;
import com.enriclop.kpopbot.modelo.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "settings")
public class Settings {

    private String channelName;
    private String tokenChannel;
    private String botUsername;
    private String tokenBot;
    private String domain;
    private String adminUsername;
    private String adminPassword;
    private AdminUser adminUser;
    private String clientId;
    private String spotifyClientId;
    private String spotifyClientSecret;

    private List<String> moderators;
    private List<User> moderatorUsers;

    @PostConstruct
    public void initAdminUser() {
        this.adminUser = new AdminUser(adminUsername, adminPassword);
    }

    public String getoAuthTokenChannel() {
        return "oauth:" + tokenChannel;
    }

    public String getoAuthTokenBot() {
        return "oauth:" + tokenBot;
    }
}
