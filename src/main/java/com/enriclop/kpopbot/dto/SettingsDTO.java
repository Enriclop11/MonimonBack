package com.enriclop.kpopbot.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class SettingsDTO {

    String channelName;

    String oAuthTokenChannel;

    boolean haveChannelToken;

    String botUsername;

    String oAuthTokenBot;

    boolean haveBotToken;

    String discordToken;

    boolean haveDiscordToken;

    String domain;

    int cdMinutes;

    int maxCdMinutes;

    @Nullable
    boolean spawnActive;

    public SettingsDTO() {
    }

    public SettingsDTO(int cdMinutes, int maxCdMinutes, boolean spawnActive, String channelName, boolean haveChannelToken, String botUsername, boolean haveBotToken, String domain, boolean haveDiscordToken) {
        this.cdMinutes = cdMinutes;
        this.spawnActive = spawnActive;
        this.channelName = channelName;
        this.botUsername = botUsername;
        this.domain = domain;
        this.haveBotToken = haveBotToken;
        this.haveChannelToken = haveChannelToken;
        this.haveDiscordToken = haveDiscordToken;
        this.maxCdMinutes = maxCdMinutes;
    }

    public SettingsDTO(int cdMinutes, boolean spawnActive, String channelName, String botUsername, boolean haveBotToken, String domain, boolean haveDiscordToken) {
        this.cdMinutes = cdMinutes;
        this.spawnActive = spawnActive;
        this.channelName = channelName;
        this.botUsername = botUsername;
        this.domain = domain;
        this.haveBotToken = haveBotToken;
        this.haveDiscordToken = haveDiscordToken;
    }
}
