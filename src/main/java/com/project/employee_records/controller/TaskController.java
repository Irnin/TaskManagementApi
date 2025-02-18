package com.project.employee_records.controller;

import com.project.employee_records.model.*;
import com.project.employee_records.service.CategoryService;
import com.project.employee_records.service.TaskService;
import com.project.employee_records.service.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("/task/{idTask}")
    public ResponseEntity<Task> getTask(@PathVariable Integer idTask){
        return ResponseEntity.of(taskService.getTask(idTask));
    }

    @GetMapping("/tasks")
    public Page<Task> getTasks(Pageable pageable){
        return taskService.getTasks(pageable);
    }

    @PostMapping("/tasks")
    public ResponseEntity<Void> saveTask(@RequestBody Task task){
        Task createTask = taskService.setTask(task);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idTask}").buildAndExpand(createTask.getIdTask()).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        headers.add("Access-Control-Expose-Headers", "*");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/tasks/{idTask}")
    public ResponseEntity<Void> updateTask(@RequestBody Task task, @PathVariable Integer idTask){
        return taskService.getTask(idTask)
                .map(t -> {
                    taskService.setTask(task);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/task/{idTask}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer idTask){
        return taskService.getTask(idTask)
                .map(t -> {
                    taskService.deleteTask(idTask);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // SPECIALIZED ENDPOINTS
    /**
     * Create task
     */
    @PostMapping(value = "/createTask/category/{idCategory}")
    public ResponseEntity<Void> createTask(@RequestBody Task recievedTask, @PathVariable Integer idCategory) {
        Task task = new Task();

        task.setTitle(recievedTask.getTitle());
        task.setDescription(recievedTask.getDescription());
        task.setFinished(false);
        task.setTaskScore(recievedTask.getTaskScore());
        task.setDueDate(recievedTask.getDueDate());

        Category category = categoryService.getCategory(idCategory).get();
        task.setCategory(category);

        task = taskService.setTask(task);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idTask}").buildAndExpand(task.getIdTask()).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        headers.add("Access-Control-Expose-Headers", "*");

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Returns nonassigneed tasks
     */
    @GetMapping("/tasks/unassigned")
    public Page<Task> getUnassignedTasks(Pageable pageable) {
        return taskService.getUnassignedTask(pageable);
    }

    /**
     * Returns tasks assigned to specific user ID
     */
    @GetMapping("/tasks/user/{idUser}")
    public Page<Task> getAssignedTask(@PathVariable Integer idUser, Pageable pageable) {
        return taskService.getAssignedTask(idUser, pageable);
    }

    @GetMapping("/task/{idTask}/assignedUser")
    public User assignedUserToTask(@PathVariable Integer idTask) {
        return taskService.userAssignedToTask(idTask);
    }

    /**
     * Assignee task to user
     */
    @PatchMapping("/tasks/assigne/{idTask}/to/{idUser}")
    public Task assigneTask(@PathVariable Integer idTask,
                            @PathVariable Integer idUser) {
        return taskService.assignUserToTask(idTask, idUser);
    }

    /**
     * Remove user assigned to task
     */
    @PatchMapping("/tasks/unassigne/{idTask}")
    public Task assigneTask(@PathVariable Integer idTask) {
        return taskService.unassigneUser(idTask);
    }

    /**
     * Finish project
     */
    @PatchMapping("/tasks/complete/{idTask}")
    public Task finishProject(@PathVariable Integer idTask) {
        return taskService.finish(idTask);
    }

    /**
     * Find users for task
     */
    @GetMapping("/tasks/findUsersForTask/{idTask}")
    public List<UserForTask> findUsersForTask(@PathVariable Integer idTask) {
        Task task = taskService.getTask(idTask).orElseThrow(() -> new RuntimeException("Can not find task"));
        Category taskCategory = task.getCategory();

        List<User> users = userService.getAllUsers();

        List<UserForTask> usersWithTaskDetails = users.stream().map(user -> new UserForTask(user, userService.getAssignedUnfinishedTasksNumber(user.getIdUser())))
                .collect(Collectors.toList());

        List<UserForTask> usersWithExperience = usersWithTaskDetails.stream()
                .filter(user -> user.getUser().getTasks().stream()
                        .anyMatch(t -> t.getCategory() != null && t.getCategory().equals(taskCategory)))
                .collect(Collectors.toList());

        // If we can not find user with experience
        if(usersWithExperience.isEmpty()) {
            List<UserForTask> usersWithLowLoad = usersWithTaskDetails.stream()
                    .sorted((u1, u2) -> Integer.compare(u1.getAssignedActiveTask(), u2.getAssignedActiveTask()))
                    .toList();

            if(usersWithLowLoad.size() > 5) {
                usersWithLowLoad = usersWithLowLoad.subList(0, 5);
            }

            return usersWithLowLoad;
        }

        usersWithExperience.sort((u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()));

        if(usersWithExperience.size() > 5) {
            usersWithExperience = usersWithExperience.subList(0, 5);
        }

        return usersWithExperience;
    }

    @GetMapping("/task/{idTask}/rate")
    public Rate findRate(@PathVariable Integer idTask) {
        return taskService.getTask(idTask)
                .map(Task::getRate)
                .orElse(null);
    }
}
