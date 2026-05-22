package com.enriclop.kpopbot.controller;

import com.enriclop.kpopbot.dto.*;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.security.Settings;
import com.enriclop.kpopbot.servicio.UserService;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.enriclop.kpopbot.utilities.Utilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@RestController
@Slf4j
public class UserController {

    @Autowired
    Settings settings;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TwitchConnection twitchConnection;

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public UserCardsDTO getUserByIdOrUsername(@PathVariable String id) {
        try{
            Integer idInt = Integer.parseInt(id);
            User user = userService.getUserById(idInt);
            if (user == null) {
                user = userService.getUserByUsername(id);
            }
            reOrderUserCards(user);
            return new UserCardsDTO(user);
        } catch (NumberFormatException e) {
            User user = userService.getUserByUsername(id);
            reOrderUserCards(user);
            return new UserCardsDTO(user);
        }
    }

    public void reOrderUserCards(User user) {
        List<PhotoCard> cards = user.getPhotoCards();
        cards.sort((c1, c2) -> c2.getId().compareTo(c1.getId()));
        user.setPhotoCards(cards);
        userService.saveUser(user);
    }

    @PostMapping("/token")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login request: " + loginRequest);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            String token = userService.generateToken(authentication);
            return new LoginResponse(token);
        } catch (AuthenticationException e) {
            log.error("Error in login", e);
            return null;
        }
    }

    @PostMapping("/myuser")
    public UserProfileDto getMyUser(@RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);

        boolean isModerator = settings.getModeratorUsers()
                .stream()
                .anyMatch(moderator -> moderator.getUsername().equals(user.getUsername()));

        return UserProfileDto.fromUser(user, isModerator);
    }

    @PostMapping("/changePassword")
    public void changePassword(@RequestHeader("Authorization") String token, @RequestBody PasswordDTO passwordDTO) {
        User userToken = userService.getUserByToken(token);
        log.info("Changing password for user: " + userToken.getUsername());
        userToken.setPassword(passwordDTO.password);
        userService.saveUser(userToken);
    }
    
    static class PasswordDTO {
        public String password;
    }

    @PostMapping("/selectCard")
    public void selectCard(@RequestHeader("Authorization") String token, @RequestBody SelectCardDTO card) {
        User user = userService.getUserByToken(token);
        List<Integer> selectedCards = user.getSelectedCards();

        if (selectedCards.contains(card.cardId)) {
            selectedCards.remove(card.cardId);
        } else {
            selectedCards.add(card.cardId);
        }

        if (selectedCards.size() > 3) {
            selectedCards.remove(0);
        }

        user.setSelectedCards(selectedCards);
        userService.saveUser(user);
    }

    static class SelectCardDTO {
        public Integer cardId;
    }

    @PostMapping("/loginTwitch")
    public LoginResponse loginTwitch(@RequestBody TwitchLoginDTO twitchLoginDTO) {
        String url = "https://api.twitch.tv/helix/users";

        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + twitchLoginDTO.code);
        headers.set("Client-ID", twitchConnection.getSettings().getClientId());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            if (data.isArray() && data.size() > 0) {
                JsonNode user = data.get(0);

                return setPassword(user.get("id").asText());
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON response", e);
        }

        return null;
    }

    public LoginResponse setPassword(String id)  {
        User user = userService.getUserByTwitchId(id);
        if (user == null) {
            twitchConnection.start(id);
            user = userService.getUserByTwitchId(id);
        }

        String password = Utilities.generatePassword();
        user.setPassword(password);
        userService.saveUser(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), password)
        );

        String token = userService.generateToken(authentication);
        return new LoginResponse(token);
    }

    static class TwitchLoginDTO {
        public String code;
    }

    @PostMapping("/linkDiscord")
    public void linkDiscord(@RequestHeader("Authorization") String token, @RequestBody LinkDiscordDTO linkDiscordDTO) {
        User user = userService.getUserByToken(token);

        String url = "https://discord.com/api/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        headers.setBasicAuth(settings.getDiscordClientId(), settings.getDiscordClientSecret());
        String body = "grant_type=authorization_code&code=" + linkDiscordDTO.discordCode + "&redirect_uri=" + linkDiscordDTO.redirectUri;
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AccessTokenDiscord> response = restTemplate.postForEntity(url, entity, AccessTokenDiscord.class);

        AccessTokenDiscord accessTokenDiscord = response.getBody();
        if (accessTokenDiscord != null) {

            String urlUser = "https://discord.com/api/users/@me";
            HttpHeaders headersUser = new HttpHeaders();
            headersUser.set("Authorization", "Bearer " + accessTokenDiscord.access_token);
            HttpEntity<String> entityUser = new HttpEntity<>(headersUser);
            ResponseEntity<UserDataDiscord> responseUser = restTemplate.exchange(urlUser, HttpMethod.GET, entityUser, UserDataDiscord.class);
            UserDataDiscord dataDiscord = responseUser.getBody();
            if (dataDiscord != null) {
                user.setDcUsername(dataDiscord.id);
                userService.saveUser(user);
                log.info("Linked Discord user " + dataDiscord.id + " to user " + user.getUsername());
            }
        }
    }

    static class LinkDiscordDTO {
        public String discordCode;
        public String redirectUri;
    }

    static class AccessTokenDiscord {
        public String access_token;
        public String token_type;
        public String expires_in;
        public String refresh_token;
        public String scope;
    }


    static class UserDataDiscord {
        public String id;
    }

    @GetMapping("/unlinkDiscord")
    public void unlinkDiscord(@RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);
        user.setDcUsername(null);
        userService.saveUser(user);
    }
}