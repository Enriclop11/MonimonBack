package com.enriclop.kpopbot.security;

import com.enriclop.kpopbot.dto.AdminUser;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
