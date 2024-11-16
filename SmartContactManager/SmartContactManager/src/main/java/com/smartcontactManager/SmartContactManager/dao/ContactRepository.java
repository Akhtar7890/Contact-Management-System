package com.smartcontactManager.SmartContactManager.dao;

import com.smartcontactManager.SmartContactManager.entities.Contact;
import com.smartcontactManager.SmartContactManager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends CrudRepository<Contact,Long> {

    //pagination



    @Query("from Contact as c where c.user.id =:userId")
    //pageable has two information
    // 1-- current page i.e 0
    // 2-- contact per page i.e 5
    public Page<Contact> findContactsByUserId(@Param("userId") long userId, Pageable pageable);

    //search method
    public List<Contact> findByNameContainingAndUser(String keyword, User user);
}
