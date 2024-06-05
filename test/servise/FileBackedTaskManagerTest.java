package servise;

import model.Epic;
import model.Subtask;
import model.Task;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
 sprint_9-solution-http-api
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.util.Calendar.FEBRUARY;


import java.util.ArrayList;


 main
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File file;


    @Test
 sprint_9-solution-http-api
    void shouldBeAnEmptyObjectFromAnEmptyFile() throws IOException{
        FileBackedTaskManager manager = new FileBackedTaskManager(File.createTempFile("data", null));
        ArrayList<Task> tasks= manager.getAllTasks();
        ArrayList<Subtask> subtasks= manager.getAllSubtasks();
        ArrayList<Epic> epics= manager.getAllEpics();
        assertEquals(0,tasks.size(), "Обьект класса не пустой");
        assertEquals(0,subtasks.size(), "Обьект класса не пустой");
        assertEquals(0,epics.size(), "Обьект класса не пустой");
    }

    @Test
    void writingAndReadingAFileTest() throws IOException {

        FileBackedTaskManager manager = new FileBackedTaskManager(new File("task.csv"));

        Task task = new Task("Похудеть к лету.", "Сбросить 5 кг.", Duration.ofMinutes(10), LocalDateTime.of(2222, FEBRUARY, 2, 22, 22));
        manager.createNewTask(task);
        Epic epic = new Epic("Переезд.", "Сьехать в свой дом.");
        manager.createNewEpic(epic);
        Subtask subtask = new Subtask("Собрать коробки", "С подписями",Duration.ofMinutes(5), LocalDateTime.of(2221, FEBRUARY, 16, 17, 22), epic.getId());
        manager.createNewSubTask(subtask);
        FileBackedTaskManager  manager1=FileBackedTaskManager.loadFromFile("task.csv");
        assertEquals(manager.getTaskById(1),manager1.getTaskById(1), "Обьекты не совпадают");

}}

    void shouldBeAnEmptyObjectFromAnEmptyFile() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(File.createTempFile("task.csv", null));
        ArrayList<Task> tasks = manager.getAllTasks();
        ArrayList<Subtask> subtasks = manager.getAllSubtasks();
        ArrayList<Epic> epics = manager.getAllEpics();
        assertEquals(0, tasks.size(), "Обьект класса не пустой");
        assertEquals(0, subtasks.size(), "Обьект класса не пустой");
        assertEquals(0, epics.size(), "Обьект класса не пустой");
    }


} main
