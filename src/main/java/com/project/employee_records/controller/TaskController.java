package com.project.employee_records.controller;

import com.project.employee_records.model.Category;
import com.project.employee_records.model.Task;
import com.project.employee_records.service.CategoryService;
import com.project.employee_records.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;
    private final CategoryService categoryService;

    @GetMapping("/tasks/{idTask}")
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

    @DeleteMapping("/tasks/{idTask}")
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
    @PostMapping(value = "/tasks/createTask/category/{idCategory}")
    public ResponseEntity<Void> createTask(@RequestBody Task recievedTask, @PathVariable Integer idCategory) {
        Task task = new Task();

        task.setTitle(recievedTask.getTitle());
        task.setDescription(recievedTask.getDescription());
        task.setFinished(false);
        task.setTaskScore(recievedTask.getTaskScore());
        task.setStartDate(recievedTask.getStartDate());
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
}
