package com.skillswaphub.dao;

import com.skillswaphub.model.Skill;
import com.skillswaphub.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Page<Skill> findByDeletedFalseAndCategoryContainingIgnoreCaseAndDifficultyContainingIgnoreCaseAndNameContainingIgnoreCase(
            String category, String difficulty, String name, Pageable pageable);

    Page<Skill> findByOwnerAndDeletedFalse(User owner, Pageable pageable);

    List<Skill> findByOwnerAndDeletedFalse(User owner);
}

