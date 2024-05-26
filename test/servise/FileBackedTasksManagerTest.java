package servise;

import model.Epic;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Calendar.FEBRUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest {
    public static final Path path = Path.of("task.csv");
    File file = new File(String.valueOf(path));


    TaskManagerable manager = new FileBackedTaskManager(new File("task.csv"));


    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }


    @Test
    public void shouldCorrectlySaveAndLoad(){
        Task task = new Task("Description", "Title", Duration.ofMinutes(9), LocalDateTime.of(2223, FEBRUARY, 1, 22, 22));
        manager.createNewTask(task);
        Epic epic = new Epic("Description", "Title");
        manager.createNewEpic(epic);
        assertEquals(List.of(task), manager.getAllTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
    }

}