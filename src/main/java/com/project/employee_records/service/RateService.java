package com.project.employee_records.service;

import com.project.employee_records.model.Rate;
import com.project.employee_records.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RateService {
    Optional<Rate> getRate(Integer idRate);
    Page<Rate> getRates(Pageable pageable);
    Rate setRate(Rate rate);
    void deleteRate(Integer idRate);
}
