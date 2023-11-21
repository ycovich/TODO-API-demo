package by.ycovich.repository;

import by.ycovich.entity.Task;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TasksRepositoryInMemoryImpl implements TasksRepository{
    private final List<Task> tasks = new LinkedList<>(){
        {
            this.add(new Task("morning exercises"));
            this.add(new Task("withdraw cash from card"));
            this.add(new Task("buy some jelly bears for my love"));
        }
    };



    @Override
    public List<Task> findAll() {
        return this.tasks;
    }

    @Override
    public void save(Task task) {
        this.tasks.add(task);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return this.tasks.stream()
                .filter(task -> task.id().equals(id))
                .findFirst();
    }
}
