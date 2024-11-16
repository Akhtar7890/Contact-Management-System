package com.smartcontactManager.SmartContactManager.service;

import com.smartcontactManager.SmartContactManager.dao.UserRepository;
import com.smartcontactManager.SmartContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public void saveUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
