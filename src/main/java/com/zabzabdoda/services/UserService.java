package com.zabzabdoda.services;

import com.zabzabdoda.model.Roles;
import com.zabzabdoda.model.User;
import com.zabzabdoda.repository.RoleRepository;
import com.zabzabdoda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void createNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Roles role = roleRepository.getByRoleName("USER");
        user.setRole(role);
        userRepository.save(user);
    }

    public boolean usernameExists(User user){
        User otherUser = userRepository.readByUsername(user.getUsername());
        if(otherUser != null){
            return true;
        }
        return false;
    }

    public boolean emailExists(User user){
        User otherUser = userRepository.readByEmail(user.getEmail());
        if(otherUser != null){
            return true;
        }
        return false;
    }


}
