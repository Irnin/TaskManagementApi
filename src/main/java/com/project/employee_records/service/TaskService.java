package com.project.employee_records.service;

import com.project.employee_records.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TaskService {
    Optional<Task> getTask(Integer idTask);
    Page<Task> getTasks(Pageable pageable);
    Task setTask(Task task);
    void deleteTask(Integer idTask);

    Page<Task> getUnassignedTask(Pageable pageable);
    Page<Task> getAssignedTask(Integer idUser, Pageable pageable);
    Task assignUserToTask(Integer taskId, Integer userId);
    Task unassigneUser(Integer taskId);
    Task finish(Integer taskId);
}
