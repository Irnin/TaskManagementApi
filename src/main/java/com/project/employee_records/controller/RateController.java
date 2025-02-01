package com.project.employee_records.controller;

import com.project.employee_records.model.Category;
import com.project.employee_records.model.Rate;
import com.project.employee_records.model.Task;
import com.project.employee_records.model.User;
import com.project.employee_records.service.CategoryService;
import com.project.employee_records.service.RateService;
import com.project.employee_records.service.TaskService;
import com.project.employee_records.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/rate")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class RateController {
    private final RateService rateService;
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping("/{idCat}")
    public ResponseEntity<Rate> getCategory(@PathVariable Integer idCat){
        return ResponseEntity.of(rateService.getRate(idCat));
    }

    @GetMapping("/")
    public Page<Rate> getCategories(Pageable pageable){
        return rateService.getRates(pageable);
    }

    @PostMapping(value = "/", consumes = "application/json")
    public ResponseEntity<Void> saveCategory(@RequestBody Rate rate){
        Rate createCategory = rateService.setRate(rate);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idRate}").buildAndExpand(createCategory.getIdRate()).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        headers.add("Access-Control-Expose-Headers", "*");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{idCat}")
    public ResponseEntity<Void> updateCategory(@RequestBody Rate rate, @PathVariable Integer idCat){
        return rateService.getRate(idCat)
                .map(c -> {
                    rateService.setRate(rate);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idCat}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer idCat){
        return rateService.getRate(idCat)
                .map(c -> {
                    rateService.deleteRate(idCat);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/tasks/{idTask}/rate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> rateTask(@PathVariable Integer idTask, @RequestBody Rate rate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();

        rate.setCreatedBy(user);

        return taskService.getTask(idTask)
                .map(task -> {
                    task.setRate(rate);
                    taskService.setTask(task);
                    return ResponseEntity.ok(task);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
