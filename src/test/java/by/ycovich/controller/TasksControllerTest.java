package by.ycovich.controller;

import by.ycovich.exception.ErrorResponses;
import by.ycovich.entity.NewTaskDetails;
import by.ycovich.entity.Task;
import by.ycovich.repository.TasksRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class TasksControllerTest {
    @Mock
    TasksRepository tasksRepository;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    TasksController tasksController;

    @Test
    @DisplayName("GET /api/tasks/ returns HTTP status 200 OK and list of tasks")
    void getTasks_ReturnValidResponseEntity(){
        // given
        var tasks = List.of(new Task(UUID.randomUUID(), "task one", false),
                new Task(UUID.randomUUID(), "task two", false));
        doReturn(tasks).when(this.tasksRepository).findAll();
        // when
        var responseEntity = this.tasksController.getTasks();
        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(tasks, responseEntity.getBody());
    }

    @Test
    void createTask_DetailsAreValid_ReturnValidResponseEntity(){
        // given
        var details = "some details";
        // when
        var responseEntity = this.tasksController.createTask(new NewTaskDetails(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"),
                Locale.ENGLISH);
        // then
        assertNotNull(responseEntity);
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        if (responseEntity.getBody() instanceof Task task){
            assertNotNull(task.getId());
            assertEquals(details, task.getDetails());
            assertFalse(task.isCompleted());
            assertEquals(URI.create("http://localhost:8080/api/tasks/" + task.getId()),
                    responseEntity.getHeaders().getLocation());

            verify(this.tasksRepository).save(task);
        } else {
            assertInstanceOf(Task.class, responseEntity.getBody());
        }

        verifyNoMoreInteractions(this.tasksRepository);
    }

    @Test
    void createTask_DetailsAreInvalid_ReturnValidResponseEntity(){
        // given
        var details = "";
        var locale = Locale.ENGLISH;
        var errorMessage = "tasks details are empty";

        doReturn(errorMessage).when(this.messageSource)
                .getMessage("tasks.create.details.error.invalid", new Object[0], locale);
        // when
        var responseEntity = this.tasksController.createTask(
                new NewTaskDetails(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"),
                locale);
        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorResponses(List.of(errorMessage)), responseEntity.getBody());

        verifyNoInteractions(this.tasksRepository);
    }

    @Test
    void getTask_ReturnValidResponseEntity(){
        // given
        var id = UUID.randomUUID();
        Task task = new Task(id, "do something cool", false);
        when(tasksRepository.findById(id)).thenReturn(Optional.of(task));
        // when
        var responseEntity = this.tasksController.getTask(id);
        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(task, responseEntity.getBody());
    }

    @Test
    void getTask_ReturnInvalidResponseEntity(){
        // given
        var id = UUID.randomUUID();

        when(tasksRepository.findById(id)).thenReturn(Optional.empty());
        // when
        var responseEntity = this.tasksController.getTask(id);
        // then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}