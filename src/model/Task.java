package model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Status status = Status.NEW;
    private Integer id;


    public Task(String name, String description) {
        this.name = name;
        this.description = description;


    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }
    @Override
    public String toString() {
        return  id + "," +
                TasksTypes.TASK + "," +
                name + "," +
                status + "," +
                description;
    }
}
