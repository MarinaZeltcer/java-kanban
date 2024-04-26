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

    /*
    * Что ж ты не сказала, что у тебя уже есть метод, который добавляет в subtaskIds id подзадачи?)
    * Это отменяет необходимость в методе addSubtaskToEpic, поскольку они одинаковые.
    * */
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
  public void addSubtaskToEpic(Subtask subtask){
       subtaskIds.add(subtask.getId());
    }
}
