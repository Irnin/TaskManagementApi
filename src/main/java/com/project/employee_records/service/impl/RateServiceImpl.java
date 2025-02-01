package com.project.employee_records.service.impl;

import com.project.employee_records.model.Rate;
import com.project.employee_records.model.Task;
import com.project.employee_records.repository.RateRepository;
import com.project.employee_records.service.RateService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RateServiceImpl implements RateService {
    private RateRepository rateRepository;

    @Override
    public Optional<Rate> getRate(Integer idRate) {
    return rateRepository.findById(idRate);
}

    @Override
    public Page<Rate> getRates(Pageable pageable) {
        return rateRepository.findAll(pageable);
    }

    @Override
    public Rate setRate(Rate rate) {
        rateRepository.save(rate);
        return rate;
    }

    @Override
    public void deleteRate(Integer idRate) {
        rateRepository.deleteById(idRate);
    }
}
