package com.agenda.topicus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agenda.topicus.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.id FROM User u where u.name = :name") 
    User findUserIdByName(String name);
}
