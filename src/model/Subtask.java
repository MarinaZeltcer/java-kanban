package model;

public class Subtask extends Task {

    private Integer epicIds;

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        epicIds = epicId;
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
                epicIds;
    }

}