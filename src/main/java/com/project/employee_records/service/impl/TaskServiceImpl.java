package com.project.employee_records.service.impl;

import com.project.employee_records.model.Task;
import com.project.employee_records.model.User;
import com.project.employee_records.repository.TaskRepository;
import com.project.employee_records.repository.UserRepository;
import com.project.employee_records.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;
    private UserRepository userRepository;

    @Override
    public Optional<Task> getTask(Integer idTask) {
        return taskRepository.findById(idTask);
    }

    @Override
    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public Task setTask(Task task) {
        taskRepository.save(task);
        return task;
    }

    @Override
    public void deleteTask(Integer idTask) {
        taskRepository.deleteById(idTask);
    }

    @Override
    public Page<Task> getUnassignedTask(Pageable pageable) {
        return taskRepository.findByUserIsNull(pageable);
    }

    @Override
    public Page<Task> getAssignedTask(Integer idUser, Pageable pageable) {
        return taskRepository.findByUser_IdUser(idUser, pageable);
    }

    @Override
    public Task assignUserToTask(Integer taskId, Integer userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        task.setUser(user);
        return taskRepository.save(task);
    }

    @Override
    public Task unassigneUser(Integer taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        task.setUser(null);

        return taskRepository.save(task);
    }
}
