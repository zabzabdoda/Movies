package com.zabzabdoda.repository;

import com.zabzabdoda.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Roles,Integer> {


    Roles getByRoleName(String user);
}
