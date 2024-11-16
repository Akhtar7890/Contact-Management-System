package com.smartcontactManager.SmartContactManager.Config;

import com.smartcontactManager.SmartContactManager.dao.UserRepository;
import com.smartcontactManager.SmartContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //fetching user by database
        User user=userRepository.getUserByUserName(email);
        if(user==null){
            throw new UsernameNotFoundException("user not found "+  email);
        }
        return new CustomUserDetails(user);
    }
}
