package com.project.employee_records.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor(force = true)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTask;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private Boolean finished;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishedDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignedDate;

    @NonNull
    @Column(length = 2)
    private Integer taskScore;

    @NonNull
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime taskCreated;

    // Due dates
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    @JsonGetter("categoryName")
    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    // Relation
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    @JsonBackReference("category-task")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonBackReference("user-task")
    private User user;
}
