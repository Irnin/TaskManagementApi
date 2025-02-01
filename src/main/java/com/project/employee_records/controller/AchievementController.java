package com.project.employee_records.controller;

import com.project.employee_records.model.Achievement;
import com.project.employee_records.model.Task;
import com.project.employee_records.model.User;
import com.project.employee_records.repository.AchievementRepository;
import com.project.employee_records.service.AchievementService;
import com.project.employee_records.service.TaskService;
import com.project.employee_records.service.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/achievements")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AchievementController {
    private final AchievementService achievementService;
    private final AchievementRepository achievementRepository;
    private final UserService userService;
    private final TaskService taskService;

    @GetMapping("/{idAchiev}")
    public ResponseEntity<Achievement> getAchievement(@PathVariable Integer idAchiev){
        return ResponseEntity.of(achievementService.getAchievement(idAchiev));
    }

    @GetMapping("/")
    public Page<Achievement> getAchievements(Pageable pageable){
        return achievementService.getAchievements(pageable);
    }

    @PostMapping("/")
    public ResponseEntity<Void> saveAchievement(@RequestBody Achievement achievement){
        Achievement createAchievement = achievementService.setAchievement(achievement);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idAchiev}").buildAndExpand(createAchievement.getIdAchiev()).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        headers.add("Access-Control-Expose-Headers", "*");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{idAchiev}")
    public ResponseEntity<Void> updateAchievement(@RequestBody Achievement achievement, @PathVariable Integer idAchiev){
        return achievementService.getAchievement(idAchiev)
                .map(a -> {
                    achievementService.setAchievement(achievement);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idAchiev}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Integer idAchiev){
        return achievementService.getAchievement(idAchiev)
                .map(a -> {
                    achievementService.deleteAchievement(idAchiev);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // SPECIALIZED ENDPOINTS

    /**
     * Getting achievement for particular task
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<Achievement>> getAchievementForTask(@PathVariable Integer taskId, Pageable pageable) {
        Page<Achievement> achievements = achievementService.getAchievementsByTask(taskId, pageable);

        if(achievements == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(achievements);
    }

    @PatchMapping("/confirm/{idAchievement}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Achievement> confirmAchievement(@PathVariable Integer idAchievement) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<Achievement> achievement = achievementService.getAchievement(idAchievement);

        if(achievement.isPresent()) {
            User user = userService.getUserByEmail(email).get();

            Achievement updatedAchievement = achievement.get();

            updatedAchievement.setConfirmedBy(user);
            updatedAchievement.setConfirmedDate(LocalDateTime.now());

            achievementService.setAchievement(updatedAchievement);

            return ok(updatedAchievement);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Achievement> createAchievement(@RequestBody Achievement sentAchievement) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();

        sentAchievement.setCreatedBy(user);

        if(user.isAdmin()) {
            sentAchievement.setConfirmedBy(user);
            sentAchievement.setConfirmedDate(LocalDateTime.now());
        }

        Task task = taskService.getTask(sentAchievement.getTask().getIdTask())
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + sentAchievement.getTask().getIdTask()));

        sentAchievement.setTask(task);

        Achievement savedAchievement = achievementService.setAchievement(sentAchievement);

        return ResponseEntity.ok(savedAchievement);
    }
}
