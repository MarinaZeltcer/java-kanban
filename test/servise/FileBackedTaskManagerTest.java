package servise;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File file;


    @Test
    void shouldBeAnEmptyObjectFromAnEmptyFile() throws IOException{
        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(),File.createTempFile("data", null));
        ArrayList<Task> tasks= manager.getAllTasks();
        ArrayList<Subtask> subtasks= manager.getAllSubtasks();
        ArrayList<Epic> epics= manager.getAllEpics();
        assertEquals(0,tasks.size(), "Обьект класса не пустой");
        assertEquals(0,subtasks.size(), "Обьект класса не пустой");
        assertEquals(0,epics.size(), "Обьект класса не пустой");
    }

    @Test
    void writingAndReadingAFileTest() throws IOException {
        File file = new File("data");
        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(),file);

        Task task = new Task("Похудеть к лету.", "Сбросить 5 кг.");
        manager.createNewTask(task);
        Epic epic = new Epic("Переезд.", "Сьехать в свой дом.");
        manager.createNewEpic(epic);
        Subtask subtask = new Subtask("Собрать коробки", "С подписями", epic.getId());
        manager.createNewSubTask(subtask);
        manager.save();
        FileBackedTaskManager  manager1=FileBackedTaskManager.loadFromFile("data");
        assertEquals(manager.getTaskById(1),manager1.getTaskById(1), "Обьекты не совпадают");

}}