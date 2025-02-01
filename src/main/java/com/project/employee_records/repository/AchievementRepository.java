package com.project.employee_records.repository;

import com.project.employee_records.model.Achievement;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    Achievement findByTask_IdTask(Integer taskId);

    Page<Achievement> findAllByTask_IdTask(Integer taskId, Pageable pageable);
}
