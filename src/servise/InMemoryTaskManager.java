package servise;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManagerable {


    HistoryManager historyManager=Managers.getDefaultHistory();

    Integer id = 1;

    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task createNewTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Integer generateId() {
        return id++;
    }

    @Override
    public void removeAllTask() {
        tasks.clear();
    }

    @Override
    public void removeTaskById(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Task getTaskById(Integer id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);

    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Integer i : tasks.keySet()) {
            allTasks.add(tasks.get(i));
        }
        return allTasks;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeEpicById(Integer id) {
        Epic epic = epics.remove(id);
        for (Integer i : epic.getSubtaskId()) {
            subtasks.remove(i);
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Integer i : epics.keySet()) {
            allEpics.add(epics.get(i));
        }
        return allEpics;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic updateEpic = epics.get(epic.getId());
        updateEpic.setName(epic.getName());
        updateEpic.setDescription(epic.getDescription());
    }

    @Override
    public Epic getEpicById(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);

    }

    @Override
    public Subtask createNewSubTask(Subtask subtask) {
        Epic epic = epics.get(subtask.getepicIds());
        if (epic == null) {
            return null;
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        assignStatusEpic(epic.getId());
        return subtask;
    }

    @Override
    public void removeAllSubTask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskId().clear();
        }

    }

    @Override
    public void removeSubTaskById(Integer id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getepicIds());
        epic.removeSubtask(subtask);
        assignStatusEpic(epic.getId());
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Integer i : subtasks.keySet()) {
            allSubtasks.add(subtasks.get(i));
        }
        return allSubtasks;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        assignStatusEpic(subtask.getepicIds());

    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);

    }

    @Override
    public ArrayList<Subtask> getAllSubtaskByEpic(Integer EpicId) {
        ArrayList<Subtask> allSubtaskByEpic = new ArrayList<>();
        Epic epic = epics.get(EpicId);
        for (Integer i : epic.getSubtaskId()) {
            allSubtaskByEpic.add(subtasks.get(i));
        }
        return allSubtaskByEpic;
    }

    @Override
    public void assignStatusEpic(Integer EpicId) {
        Epic epic = epics.get(EpicId);
        if (checkNEW(epic)) {
            return;
        }
        checkDONE(epic);
    }

    @Override
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

    @Override
    public void checkDONE(Epic epic) {
        for (Subtask subtask : getAllSubtaskByEpic(epic.getId())) {
            if (subtask.getStatus() != Status.DONE) {
                epic.setStatus(Status.IN_PROGRESS);
            }

        }

    }
}
