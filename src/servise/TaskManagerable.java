package servise;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.List;
import java.util.ArrayList;


public interface TaskManagerable {

    List<Task> getHistory();
    Task createNewTask(Task task);

    Integer generateId();

    void removeAllTask();

    void removeTaskById(Integer id);

    Task getTaskById(Integer id);

    ArrayList<Task> getAllTasks();

    void updateTask(Task task);

    Epic createNewEpic(Epic epic);

    void removeAllEpic();

    void removeEpicById(Integer id);

    ArrayList<Epic> getAllEpics();

    void updateEpic(Epic epic);

    Epic getEpicById(Integer id);

    Subtask createNewSubTask(Subtask subtask);

    void removeAllSubTask();

    void removeSubTaskById(Integer id);

    ArrayList<Subtask> getAllSubtasks();

    void updateSubtask(Subtask subtask);

    Subtask getSubtaskById(Integer id);

    ArrayList<Subtask> getAllSubtaskByEpic(Integer EpicId);

    void assignStatusEpic(Integer EpicId);

    boolean checkNEW(Epic epic);

    void checkDONE(Epic epic);
}
