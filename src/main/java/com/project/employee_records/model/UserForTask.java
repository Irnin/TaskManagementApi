package com.project.employee_records.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserForTask {
    private User user;
    private Integer score;
    private Integer assignedActiveTask;

    public UserForTask(User user, Integer assignedActiveTask) {
        this.user = user;
        this.score = calculateUserScore();
        this.assignedActiveTask = assignedActiveTask;
    }

    private int calculateUserScore() {
        int taskScore = this.user.getTasks().stream()
                .filter(Task::getFinished)
                .mapToInt(Task::getTaskScore)
                .sum();

        int achievementScore = this.user.getTasks().stream()
                .flatMap(task -> task.getAchievements().stream())
                .mapToInt(achievement -> achievement.getValueScore())
                .sum();

        return taskScore + achievementScore;
    }
}
