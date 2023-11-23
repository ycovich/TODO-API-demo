package by.ycovich.repository;

import by.ycovich.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskRepositoryImpl implements TasksRepository, RowMapper<Task> {
    private final JdbcOperations jdbcOperations;
    @Autowired
    public TaskRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Task> findAll() {
        return this.jdbcOperations.query("select * from task", this);
    }

    @Override
    public void save(Task task) {
        this.jdbcOperations.update("insert into task(id, details, completed, id_application_user) values (?,?,?,?)",
                new Object[]{task.id(), task.details(), task.completed(), task.applicationUserId()});
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return this.jdbcOperations.query("select * from task where id = ?", new Object[]{id}, this)
                .stream().findFirst();
    }

    @Override
    public List<Task> findByApplicationUserId(UUID id) {
        return this.jdbcOperations.query("select * from task where id_application_user = ?", this, id);
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Task(rs.getObject("id", UUID.class),
                        rs.getString("details"),
                        rs.getBoolean("completed"),
                        rs.getObject("id_application_user", UUID.class));
    }
}
