package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Integer epicIds;

    public Subtask(String name, String description,Integer epicIds) {
        super(name, description);
        this.epicIds = epicIds;
    }

    public Subtask(String name, String description, Duration duration, LocalDateTime startTime, Integer epicIds) {
        super(name, description, duration, startTime);
        this.epicIds = epicIds;
    }

    public Integer getepicIds() {
        return epicIds;
    }

    @Override
    public String toString() {
        return getId() + "," +
                TasksTypes.SUBTASK + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription() + "," +
                getDuration().toMinutes() + "," +
                getStartTime() + "," +
                epicIds;
    }

}