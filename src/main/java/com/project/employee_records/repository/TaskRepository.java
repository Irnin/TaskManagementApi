package com.project.employee_records.repository;

import com.project.employee_records.model.Task;
import com.project.employee_records.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    Page<Task> findByUserIsNull(Pageable pageable);
    Page<Task> findByUser_IdUser(Integer userIdUser, Pageable pageable);

}
