import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    Integer id = 1;

    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();

    public Task createNewTask(Task task) {
        task.setId(id++);
        tasks.put(task.getId(), task);
        return task;
    }

    public void removeAllTask() {
        tasks.clear();
    }

    public void removeTaskById(Integer id) {
        tasks.remove(id);
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);

    }


    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Integer i : tasks.keySet()) {
            allTasks.add(tasks.get(i));
        }
        return allTasks;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }


    public Epic createNewEpic(Epic epic) {
        epic.setId(id++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void removeEpicById(Integer id) {
        Epic epic = epics.remove(id);
        for (Integer i : epic.getSubtaskId()) {
            subtasks.remove(i);
        }
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Integer i : epics.keySet()) {
            allEpics.add(epics.get(i));
        }
        return allEpics;
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
}

    public Epic getEpicById(Integer id) {
        return epics.get(id);

    }


    public Subtask createNewSubTask(Subtask subtask) {
        subtask.setId(id++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskId().add(subtask.getId());
        assignStatusEpic(epic.getId());
        return subtask;
    }

    public void removeAllSubTask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskId().clear();
        }

    }

    public void removeSubTaskById(Integer id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskId().remove(id);
        assignStatusEpic(epic.getId());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Integer i : subtasks.keySet()) {
            allSubtasks.add(subtasks.get(i));
        }
        return allSubtasks;
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        assignStatusEpic(subtask.getEpicId());

    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);

    }

    public ArrayList<Subtask> getAllSubtaskByEpic(Integer EpicId) {
        ArrayList<Subtask> allSubtaskByEpic = new ArrayList<>();
        Epic epic = epics.get(EpicId);
        for (Integer i : epic.getSubtaskId()) {
            allSubtaskByEpic.add(subtasks.get(i));
        }
        return allSubtaskByEpic;
    }

    public void assignStatusEpic(Integer EpicId) {
        Epic epic = epics.get(EpicId);
        if (checkNEW(epic)) {
            return;
        }
        checkDONE(epic);
    }

    public boolean checkNEW(Epic epic) {
        for (Subtask subtask : getAllSubtaskByEpic(epic.getId())) {
            if (subtask.getStatus() != Status.NEW) {
                epic.setStatus(Status.DONE);
                return false;
            }
        }
        epic.setStatus(Status.NEW);
        return true;
    }

    public void checkDONE(Epic epic) {
        for (Subtask subtask : getAllSubtaskByEpic(epic.getId())) {
            if (subtask.getStatus() != Status.DONE) {
                epic.setStatus(Status.IN_PROGRESS);
            }

            }

        }
    }

