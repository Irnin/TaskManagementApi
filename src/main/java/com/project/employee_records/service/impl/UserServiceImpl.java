package com.project.employee_records.service.impl;

import com.project.employee_records.model.Role;
import com.project.employee_records.model.User;
import com.project.employee_records.repository.UserRepository;
import com.project.employee_records.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public Optional<User> getUser(Integer idUser) {
        return userRepository.findById(idUser);
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User setUser(User user) {
        userRepository.save(user);
        return user;
    }

    @Override
    public void deleteUser(Integer idUser) {
        userRepository.deleteById(idUser);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public Boolean isUserAdmin(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);

        User user1 = user.get();

        System.out.println(user1.getRole());

        return user.filter(value -> value.getRole() == Role.ADMIN).isPresent();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ===========
    @Override
    public Integer getAssignedUnfinishedTasksNumber(Integer idUser) {
        return userRepository.countUnfinishedTasksByUserId(idUser);
    }
}
