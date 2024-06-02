import model.Epic;
import model.Subtask;
import model.Task;
import servise.FileBackedTaskManager;
import servise.TaskManagerable;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.util.Calendar.FEBRUARY;

public class Test {
    public static void main(String[] args) throws IOException {
        /*
         * Проверяем запись в файл.
         * */
        TaskManagerable manager = new FileBackedTaskManager(new File("task.csv"));

        Task task = new Task("Похудеть к лету.", "Сбросить 5 кг.", Duration.ofMinutes(10), LocalDateTime.of(2222, FEBRUARY, 2, 22, 22));
        manager.createNewTask(task);

        System.out.println(task.getEndTime());
        Task task2 = new Task("Открыть лыжный сезон.", "Освоить трассу  10 км", Duration.ofMinutes(15), LocalDateTime.of(2222, FEBRUARY, 15, 22, 22));
        manager.createNewTask(task2);

        Epic epic = new Epic("Переезд.", "Сьехать в свой дом.");
        manager.createNewEpic(epic);

        Epic epic2 = new Epic("Поход.", "С палатками.");
        manager.createNewEpic(epic2);

        Subtask subtask = new Subtask("Собрать коробки", "С подписями", Duration.ofMinutes(1), LocalDateTime.of(2223, FEBRUARY, 17, 3, 22), epic.getId());
        manager.createNewSubTask(subtask);

        Subtask subtask2 = new Subtask("Собрать палатки", "Брезентовые", Duration.ofMinutes(9), LocalDateTime.of(2222, FEBRUARY, 1, 7, 22), epic2.getId());
        manager.createNewSubTask(subtask2);

        Subtask subtask3 = new Subtask("Взять гитару", "Аккустическую", Duration.ofMinutes(5), LocalDateTime.of(2221, FEBRUARY, 16, 17, 22), epic2.getId());
        manager.createNewSubTask(subtask3);

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(4);
        manager.getEpicById(3);
        manager.getSubtaskById(6);
        manager.getSubtaskById(7);
        manager.getSubtaskById(5);
        manager.getEpicById(4);
        manager.getSubtaskById(7);
        manager.getTaskById(2);

        /*
         * Проверяем чтение из файла.
         * */
        TaskManagerable restoredManager = FileBackedTaskManager.loadFromFile("task.csv");

        System.out.println("\n\n");

        System.out.println(restoredManager.getAllTasks());
        System.out.println(restoredManager.getAllEpics());
        System.out.println(restoredManager.getAllSubtasks());
        System.out.println(restoredManager.getHistory());
        System.out.println("\n");
        System.out.println(manager.getPrioritizedTasks());
    }
}
