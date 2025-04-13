
package com.sms.SchoolManagementBrahmada.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sms.SchoolManagementBrahmada.models.AppUser;
import com.sms.SchoolManagementBrahmada.repositories.AppUserRepository;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = repo.findByEmail(email);

        if (appUser != null) {
            
            String role = appUser.getRole().toUpperCase();
            
            UserDetails springUser = User.withUsername(appUser.getEmail())
                    .password(appUser.getPassword())
                    .roles(role)
                    .build();

            return springUser;
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}

