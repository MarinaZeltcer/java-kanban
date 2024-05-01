package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }


    public ArrayList<Integer> getSubtaskId() {
        return subtaskIds;
    }

    public void addSubtask(Subtask subtask) {
        subtaskIds.add(subtask.getId());
    }

    public void removeSubtask(Subtask subtask) {
        subtaskIds.remove(subtask.getId());
    }
    @Override
    public String toString() {
        return  getId() + "," +
                TasksTypes.EPIC + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription();
    }

}
