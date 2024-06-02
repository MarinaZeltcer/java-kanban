package servise;

import model.Epic;
import model.Subtask;
import model.Task;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File file;


    @Test
    void shouldBeAnEmptyObjectFromAnEmptyFile() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(File.createTempFile("task.csv", null));
        ArrayList<Task> tasks = manager.getAllTasks();
        ArrayList<Subtask> subtasks = manager.getAllSubtasks();
        ArrayList<Epic> epics = manager.getAllEpics();
        assertEquals(0, tasks.size(), "Обьект класса не пустой");
        assertEquals(0, subtasks.size(), "Обьект класса не пустой");
        assertEquals(0, epics.size(), "Обьект класса не пустой");
    }


}