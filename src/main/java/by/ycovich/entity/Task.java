package by.ycovich.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "details")
    private String details;
    @Column(name = "completed")
    private boolean completed;

    public Task() {}

    public Task(String details) {
        this.details = details;
    }

    public Task(UUID id, String details, boolean completed) {
        this.id = id;
        this.details = details;
        this.completed = completed;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public boolean isCompleted() {
        return completed;
    }
}
