package com.smartcontactManager.SmartContactManager.dao;

import com.smartcontactManager.SmartContactManager.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User,Long> {
     User findByEmail(String email);

    @Query("select u FROM User u WHERE u.email =:email")
    public User getUserByUserName(@Param("email") String email);
}
