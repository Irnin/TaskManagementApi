package com.project.employee_records.service;

import com.project.employee_records.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUser(Integer idUser);
    Page<User> getUsers(Pageable pageable);
    User setUser(User user);
    void deleteUser(Integer idUser);

    List<User> getAllUsers();
    Optional<User> getUserByEmail(String email);
    Boolean isUserAdmin(String email);

    Integer getAssignedUnfinishedTasksNumber(Integer idUser);
}
