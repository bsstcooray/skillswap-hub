package com.skillswaphub.dao;

import com.skillswaphub.model.Role;
import com.skillswaphub.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
