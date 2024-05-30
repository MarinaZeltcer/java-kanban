package servise;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManagerable {


    HistoryManager historyManager = Managers.getDefaultHistory();

    Integer id = 1;

    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.nullsLast(Comparator.comparing(Task::getStartTime).thenComparing(Task::getId)));

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task createNewTask(Task task) {
        if (!intersectionAnyMatch(task)) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else {
            throw new RuntimeException("Задачи пересекаются");

        }
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
        prioritizedTasks.remove(getTaskById(id));
    }

    @Override
    public Task getTaskById(Integer id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);

    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        if ((intersectionAnyMatch(task))) {
            tasks.put(task.getId(), task);
        } else {
            throw new RuntimeException("Задачи пересекаются");

        }
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
        epic.getSubtaskId().forEach(i -> {
            subtasks.remove(i);
            historyManager.remove(id);
        });
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
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
        if (!intersectionAnyMatch(subtask)) {
            Epic epic = epics.get(subtask.getepicIds());
            if (epic == null) {
                return null;
            }
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtask(subtask);
            assignStatusEpic(epic.getId());
            assignStartTime(epic);
            durationByEpic(epic.getId());
            prioritizedTasks.add(subtask);
        } else {
            throw new RuntimeException("Задачи пересекаются");

        }
        return subtask;

    }

    @Override
    public void removeAllSubTask() {
        subtasks.clear();
        epics.values().forEach(epic -> epic.getSubtaskId().clear());

    }

    @Override
    public void removeSubTaskById(Integer id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getepicIds());
        epic.removeSubtask(subtask);
        assignStatusEpic(epic.getId());
        assignStartTime(epic);
        durationByEpic(epic.getId());
        historyManager.remove(id);
        prioritizedTasks.remove(getSubtaskById(id));
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (intersectionAnyMatch(subtask)) {
            Epic epic = epics.get(subtask.getepicIds());
            subtasks.put(subtask.getId(), subtask);
            assignStatusEpic(subtask.getepicIds());
            assignStartTime(epic);
            durationByEpic(subtask.getepicIds());
        } else {
            throw new RuntimeException("Задачи пересекаются");

        }
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);

    }

    @Override
    public ArrayList<Subtask> getAllSubtaskByEpic(Integer EpicId) {
        ArrayList<Subtask> allSubtaskByEpic;
        Epic epic = epics.get(EpicId);
        allSubtaskByEpic = epic.getSubtaskId().stream().map(i -> subtasks.get(i)).collect(Collectors.toCollection(ArrayList::new));
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
        getAllSubtaskByEpic(epic.getId()).stream().filter(subtask -> subtask.getStatus() != Status.DONE).map(subtask -> Status.IN_PROGRESS).forEach(epic::setStatus);

    }

    @Override
    public void assignStartTime(Epic epic) {
        ArrayList<Subtask> subtask = getAllSubtaskByEpic(epic.getId());
        LocalDateTime startTime = subtask.get(0).getStartTime();
        for (int i = 1; i < subtask.size(); i++) {
            if (startTime.isAfter(subtask.get(i).getStartTime())) {
                startTime = subtask.get(i).getStartTime();
            }
        }
        epic.setStartTime(startTime);
    }

    @Override
    public void durationByEpic(Integer EpicId) {
        Epic epic = epics.get(EpicId);
        ArrayList<Subtask> subtask = getAllSubtaskByEpic(epic.getId());
        Duration sumDuration = Duration.ofMinutes(0);
        for (Subtask value : subtask) {
            sumDuration = sumDuration.plus(value.getDuration());
            epic.setDuration(sumDuration);
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean isTimeIntersection(Task task1, Task task2) {
        if (task2.getStartTime().equals(LocalDateTime.MAX)) {
            return false;
        }
        boolean timeIntersection = task1.getStartTime().isAfter(task2.getEndTime()) || task1.getEndTime().isBefore(task2.getStartTime());
        return !timeIntersection;
    }

    private boolean intersectionAnyMatch(Task newTask) {
        return prioritizedTasks.stream().anyMatch(taskFromSet -> isTimeIntersection(taskFromSet, newTask));
    }
}




