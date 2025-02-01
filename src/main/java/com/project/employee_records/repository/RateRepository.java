package com.project.employee_records.repository;

import com.project.employee_records.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, Integer>  {
}
