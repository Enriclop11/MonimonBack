package com.enriclop.kpopbot.controller;

import com.enriclop.kpopbot.discordConnection.DiscordConnection;
import com.enriclop.kpopbot.dto.AdminUser;
import com.enriclop.kpopbot.dto.Command;
import com.enriclop.kpopbot.dto.SettingsDTO;
import com.enriclop.kpopbot.security.Settings;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.enriclop.kpopbot.twitchConnection.settings.Prices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class SettingsController {

    @Autowired
    TwitchConnection twitchConnection;

     @Autowired
     Settings settings;

     @Autowired
     DiscordConnection discordConnection;

    @GetMapping("/settings")
    public String settings(Model model) {
        int cdMinutes;
        int maxCdMinutes;
        boolean spawnActive;

        if (twitchConnection.getSpawn() != null) {
            cdMinutes = twitchConnection.getSpawn().cdMinutes;
            maxCdMinutes = twitchConnection.getSpawn().maxcdMinutes;
            spawnActive = twitchConnection.getSpawn().active;
        } else {
            cdMinutes = 5;
            maxCdMinutes = 10;
            spawnActive = false;
        }

        boolean haveBotToken = !settings.getTokenBot().isEmpty();
        boolean haveChannelToken = !settings.getTokenChannel().isEmpty();
        //boolean haveDiscordToken = !settings.tokenDiscord.isEmpty();

        SettingsDTO settings = new SettingsDTO(cdMinutes, maxCdMinutes, spawnActive, this.settings.getChannelName(), haveChannelToken, this.settings.getBotUsername(), haveBotToken, this.settings.getDomain(), true);
        model.addAttribute("settings", settings);
        return "settings/settings";
    }

    @PostMapping("/settings")
    public String changeSettings(@ModelAttribute("settings") SettingsDTO settings) {

        if (settings.getCdMinutes() < 1) settings.setCdMinutes(1);

        twitchConnection.setSpawn(settings.isSpawnActive(), settings.getCdMinutes(), settings.getMaxCdMinutes());

        boolean changed = false;

        if (settings.getChannelName() == null) settings.setChannelName("");


        if (!settings.getChannelName().isEmpty() && !this.settings.getChannelName().equals(settings.getChannelName())) {

            if (twitchConnection.getTwitchClient() != null) twitchConnection.getTwitchClient().getChat().leaveChannel(this.settings.getChannelName());

            this.settings.setChannelName(settings.getChannelName());
            changed = true;
        }

        if (!settings.getBotUsername().isEmpty() && !this.settings.getBotUsername().equals(settings.getBotUsername())) {
            this.settings.setBotUsername(settings.getBotUsername());
            changed = true;
        }

        if (settings.getOAuthTokenBot() != null && !settings.getOAuthTokenBot().isEmpty() && !this.settings.getTokenBot().equals(settings.getOAuthTokenBot()) ) {
            System.out.println(settings.getOAuthTokenBot());
            this.settings.setTokenBot(settings.getOAuthTokenBot());
            changed = true;
        }

        if (settings.getOAuthTokenChannel() != null && !settings.getOAuthTokenChannel().isEmpty() && !this.settings.getTokenChannel().equals(settings.getOAuthTokenChannel())) {
            this.settings.setTokenChannel(settings.getOAuthTokenChannel());
            changed = true;
        }

        if (settings.getDiscordToken() != null && !settings.getDiscordToken().isEmpty() && !this.settings.getTokenChannel().equals(settings.getDiscordToken())) {
            //this.settings.tokenDiscord = settings.getDiscordToken();
            discordConnection.restart();
        }

        if (changed) {
            twitchConnection.connect();
        }
        this.settings.setDomain(settings.getDomain());

        return "redirect:/settings";
    }

    @GetMapping("/settings/admin")
    public String admin(Model model) {
        model.addAttribute("adminUser", settings.getAdminUser());
        return "settings/admin";
    }

    @PostMapping("/settings/admin")
    public String changeAdmin(@ModelAttribute("adminUser") AdminUser adminUser) {
        settings.setAdminUser(adminUser);
        return "redirect:/settings/admin";
    }

    @GetMapping("/settings/commands")
    public String commands(Model model) {
        model.addAttribute("commands", twitchConnection.getCommands());
        return "settings/commands";
    }

    @PostMapping("/settings/commands")
    public String changeCommands(@ModelAttribute("command") Command command) {
        System.out.println(command.getName());
        System.out.println(command.isActive());

        List<Command> commands = twitchConnection.getCommands();

        for (Command c : commands) {
            if (c.getName().equals(command.getName())) {
                c.setActive(command.isActive());
                break;
            }
        }

        twitchConnection.setCommands(commands);

        return "redirect:/settings/commands";
    }

    @GetMapping("/settings/rewards")
    public String rewards(Model model) {
        model.addAttribute("commands", twitchConnection.getRewards());
        return "settings/rewards";
    }

    @PostMapping("/settings/rewards")
    public String changeRewards(@ModelAttribute("command") Command reward) {
        System.out.println(reward.getName());
        System.out.println(reward.isActive());

        List<Command> rewards = twitchConnection.getRewards();

        for (Command r : rewards) {
            if (r.getName().equals(reward.getName())) {
                r.setActive(reward.isActive());
                break;
            }
        }

        twitchConnection.setRewards(rewards);

        return "redirect:/settings/rewards";
    }

    @GetMapping("/settings/prices")
    public String prices(Model model) {
        model.addAttribute("prices", twitchConnection.getPrices());
        return "settings/prices";
    }

    @PostMapping("/settings/prices")
    public String changePrices(@ModelAttribute("command") Prices price) {
        twitchConnection.setPrices(price);
        return "redirect:/settings/prices";
    }



}
