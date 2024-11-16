package com.smartcontactManager.SmartContactManager.controller;

import com.smartcontactManager.SmartContactManager.dao.ContactRepository;
import com.smartcontactManager.SmartContactManager.dao.UserRepository;
import com.smartcontactManager.SmartContactManager.entities.Contact;
import com.smartcontactManager.SmartContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;


    //search handler
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query")String query, Principal principal){

        System.out.println(query);

        String userName=principal.getName();
        User user=userRepository.getUserByUserName(userName);

        List<Contact> contacts = contactRepository.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }

}
