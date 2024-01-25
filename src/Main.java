import model.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import servise.HistoryManager;
import servise.InMemoryTaskManager;
import servise.TaskManagerable;
import servise.Managers;

public class Main {

    public static void main(String[] args) {

        HistoryManager historyManager = Managers.getDefaultHistory();
        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);
        Task task = new Task("Похудеть к лету.", "Сбросить 5 кг.");
        taskManager.createNewTask(task);
        Task task2 = new Task("Открыть лыжный сезон.", "Освоить трассу  10 км");
        taskManager.createNewTask(task2);


        Epic epic = new Epic("Переезд.", "Сьехать в свой дом.");
        taskManager.createNewEpic(epic);

        Subtask subtask = new Subtask("Собрать коробки", "С подписями", epic.getId());
        taskManager.createNewSubTask(subtask);

        System.out.println(taskManager.getAllTasks());

        System.out.println(taskManager.getAllEpics());

        System.out.println(taskManager.getAllSubtaskByEpic(epic.getId()));

        Epic epic2 = new Epic("Поход.", "С палатками, на озеро.");
        taskManager.createNewEpic(epic2);

        System.out.println(taskManager.getAllEpics());

        Subtask subtask2 = new Subtask("Собрать палатки", "Брезентовые", epic2.getId());
        taskManager.createNewSubTask(subtask2);

        Subtask subtask3 = new Subtask("Взять гитару", "Аккустическую", epic2.getId());
        taskManager.createNewSubTask(subtask3);

        System.out.println(taskManager.getAllSubtaskByEpic(epic.getId()));

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);
        System.out.println(epic2.getStatus());

        taskManager.getTaskById(task.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic.getId());
        System.out.println(taskManager.getHistory());
    }

}
