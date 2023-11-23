package by.ycovich.repository;


import by.ycovich.entity.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TasksRepository {
    List<Task> findAll();

    void save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findByApplicationUserId(UUID id);
}
