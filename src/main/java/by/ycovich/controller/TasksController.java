package by.ycovich.controller;


import by.ycovich.entity.ApplicationUser;
import by.ycovich.exception.ErrorResponses;
import by.ycovich.entity.NewTaskDetails;
import by.ycovich.entity.Task;
import by.ycovich.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/tasks")
public class TasksController {
    private final TasksRepository tasksRepository;
    private final MessageSource messageSource;
    @Autowired
    public TasksController(TasksRepository tasksRepository, MessageSource messageSource) {
        this.tasksRepository = tasksRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@AuthenticationPrincipal  ApplicationUser user){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.tasksRepository.findByApplicationUserId(user.id()));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody NewTaskDetails details,
                                        @AuthenticationPrincipal ApplicationUser user,
                                        UriComponentsBuilder uriComponentsBuilder,
                                        Locale locale) {
        if (details == null || details.details().isBlank()) {
            final var message = this.messageSource
                    .getMessage("tasks.create.details.error.invalid", new Object[0], locale);

            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponses(List.of(message)));
        }

        var task = new Task(details.details(), user.id());
        this.tasksRepository.save(task);
        return ResponseEntity.created(uriComponentsBuilder
                        .path("api/tasks/{id}")
                        .build(Map.of("id", task.id())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(task);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable("id") UUID id){
        return ResponseEntity.of(this.tasksRepository.findById(id));
    }
}
