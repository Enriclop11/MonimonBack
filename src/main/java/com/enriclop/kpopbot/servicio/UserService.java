package com.enriclop.kpopbot.servicio;

import com.enriclop.kpopbot.modelo.Badge;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.repositorio.IUserRepository;
import com.enriclop.kpopbot.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsernameLike(username);
    }

    public User getUserByDiscordUsername(String discordUsername) {
        return userRepository.findByDcUsernameLike(discordUsername);
    }

    public User getUserByTwitchId(String twitchId) {
        return userRepository.findByTwitchIdLike(twitchId);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).get();
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtil.generateToken(userDetails.getUsername());
    }

    public User getUserByToken(String token) {
        String username = jwtUtil.extractUsername(token);
        return getUserByUsername(username);
    }

    public void setBadge(String username, Badge badge) {
        User user = getUserByUsername(username);

        if (user == null) {
            return;
        }

        List<Badge> badges = user.getBadges();

        if (badges.contains(badge)) {
            return;
        }

        badges.add(badge);

        user.setBadges(badges);

        saveUser(user);
    }
}
