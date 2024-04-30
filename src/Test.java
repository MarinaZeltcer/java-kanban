import model.Epic;
import model.Subtask;
import model.Task;
import servise.FileBackedTaskManager;
import servise.Managers;
import servise.TaskManagerable;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {

        /*
         * Проверяем запись в файл.
         * */
        TaskManagerable manager = new FileBackedTaskManager(Managers.getDefaultHistory(),new File("task.csv"));

        Task task = new Task("Похудеть к лету.", "Сбросить 5 кг.");
        manager.createNewTask(task);

        Task task2 = new Task("Открыть лыжный сезон.", "Освоить трассу  10 км");
        manager.createNewTask(task2);

        Epic epic = new Epic("Переезд.", "Сьехать в свой дом.");
        manager.createNewEpic(epic);

        Epic epic2 = new Epic("Поход.", "С палатками, на озеро.");
        manager.createNewEpic(epic2);

        Subtask subtask = new Subtask("Собрать коробки", "С подписями", epic.getId());
        manager.createNewSubTask(subtask);

        Subtask subtask2 = new Subtask("Собрать палатки", "Брезентовые", epic2.getId());
        manager.createNewSubTask(subtask2);

        Subtask subtask3 = new Subtask("Взять гитару", "Аккустическую", epic2.getId());
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
        Subtask subtask4 = new Subtask("Взять зажигалкк", "Газовый балончик", epic2.getId());
        restoredManager.createNewSubTask(subtask4);
        System.out.println(subtask4.getId());

    }
}
