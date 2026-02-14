package com.skillswaphub.dao;

import com.skillswaphub.model.Profile;
import com.skillswaphub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
}
