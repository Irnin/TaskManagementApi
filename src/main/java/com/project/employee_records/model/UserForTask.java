package com.project.employee_records.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserForTask {
    private User user;
    private Integer score;
    private Integer assignedActiveTask;
    private Category category;

    public UserForTask(User user, Integer assignedActiveTask, Category category) {
        this.user = user;
        this.category = category;
        this.score = calculateUserScore();
        this.assignedActiveTask = assignedActiveTask;
    }

    private int calculateUserScore() {
        int taskScore = this.user.getTasks().stream()
                .filter(task -> task.getFinished() && task.getCategory().equals(this.category))
                .mapToInt(Task::getTaskScore)
                .sum();
        
        int achievementScore = this.user.getTasks().stream()
                .filter(task -> task.getCategory().equals(this.category))
                .flatMap(task -> task.getAchievements().stream())
                .mapToInt(achievement -> achievement.getValueScore())
                .sum();

        return taskScore + achievementScore;
    }
}
