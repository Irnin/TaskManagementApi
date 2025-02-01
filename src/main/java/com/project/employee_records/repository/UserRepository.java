package com.project.employee_records.repository;

import com.project.employee_records.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.idUser = :userId AND t.finished = false")
    Integer countUnfinishedTasksByUserId(@Param("userId") Integer userId);
}
