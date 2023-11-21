package by.ycovich.repository;

import by.ycovich.model.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TasksRepository {
    List<Task> findAll();

    void save(Task task);

    Optional<Task> findById(UUID id);
}
