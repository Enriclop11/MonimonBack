package com.enriclop.kpopbot.security;

import com.enriclop.kpopbot.servicio.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    Settings settings;

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Loading user: " + username);

        if (username.equals(settings.getAdminUser().getUsername())) {
            return User.withUsername(settings.getAdminUser().getUsername())
                    .password(settings.getAdminUser().getPassword())
                    .roles("ADMIN")
                    .build();
        }

        if (userService.getUserByUsername(username) != null) {
            com.enriclop.kpopbot.modelo.User user = userService.getUserByUsername(username);
            return User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
