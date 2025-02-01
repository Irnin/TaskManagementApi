package com.project.employee_records.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor(force = true)
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAchiev;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private Integer valueScore;

    @NonNull
    @CreationTimestamp
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "created_by_id_user")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "confirmed_by_id_user")
    private User confirmedBy;

    private LocalDateTime confirmedDate;
}
