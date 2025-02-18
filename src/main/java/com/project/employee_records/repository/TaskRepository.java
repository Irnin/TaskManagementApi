package com.project.employee_records.repository;

import com.project.employee_records.model.Task;
import com.project.employee_records.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    Page<Task> findByUserIsNull(Pageable pageable);
    Page<Task> findByUser_IdUserAndFinishedFalse(Integer userIdUser, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Task t WHERE (t.finished = true AND t.rate IS NULL) OR t.idTask IN (SELECT a.task.idTask FROM Achievement a WHERE a.confirmedBy IS NULL)")
    Page<Task> findTasksWithoutRateAndUnconfirmedAchievements(Pageable pageable);

}
