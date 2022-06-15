package com.zabzabdoda.repository;

import com.zabzabdoda.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User readByUsername(String email);

    User readByEmail(String email);



}