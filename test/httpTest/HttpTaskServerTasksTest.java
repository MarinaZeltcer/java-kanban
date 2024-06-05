package httpTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servise.InMemoryTaskManager;
import servise.TaskManagerable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTasksTest {

    private final TaskManagerable manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskServerTasksTest() {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTask();
        manager.removeAllSubTask();
        manager.removeAllEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() {
        // создаём задачу
        Task task = new Task("Test", "Testing", Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // отправляем post запрос и получаем ответ
        HttpResponse<String> postResponse = sendPostRequestAndGetResponse(taskJson, "http://localhost:8080/tasks");
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(postResponse);
        assertEquals(201, postResponse.statusCode());
        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() {
        // создаём задачу
        Task task = new Task("Test", "Testing", Duration.ofMinutes(5), LocalDateTime.now());
        // Сохраняем её в менеджер
        task = manager.createNewTask(task);
        // отправляем get запрос и получаем ответ
        HttpResponse<String> getResponse = sendGetRequestAndGetResponse("http://localhost:8080/tasks");
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(200, getResponse.statusCode());
        // получаем из тела ответа список задач
        List<Task> tasksFromResponse = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>(){}.getType());
        // проверяем список задач
        assertNotNull(tasksFromResponse, "Задачи не возвращаются");
        assertEquals(1, tasksFromResponse.size(), "Некорректное количество задач");
        assertEquals(task.getName(), tasksFromResponse.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() {
        // создаём задачу
        Task task = new Task("Test", "Testing", Duration.ofMinutes(5), LocalDateTime.now());
        // Сохраняем её в менеджер
        task = manager.createNewTask(task);
        // отправляем get запрос и получаем ответ
        HttpResponse<String> getResponse = sendGetRequestAndGetResponse("http://localhost:8080/tasks?id=" + task.getId());
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(200, getResponse.statusCode());
        // получаем из тела ответа список задач
        Task taskFromResponse = gson.fromJson(getResponse.body(), Task.class);
        // проверяем список задач
        assertNotNull(taskFromResponse, "Задачи не возвращаются");
        assertEquals(task.getName(), taskFromResponse.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTaskById() {
        // создаём задачу
        Task task = new Task("Test", "Testing", Duration.ofMinutes(5), LocalDateTime.now());
        // Сохраняем её в менеджер
        task = manager.createNewTask(task);
        // проверяем, что задача сохранилась в менеджер
        assertEquals(1, manager.getAllTasks().size());
        // отправляем delete запрос и получаем ответ
        HttpResponse<String> getResponse = sendDeleteRequestAndGetResponse("http://localhost:8080/tasks?id=" + task.getId());
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(201, getResponse.statusCode());
        // проверяем, что задача была удалена
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void testDeleteAllTasks() {
        // создаём задачи
        Task task1 = new Task("Test1", "Testing", Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Test2", "Testing", Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        // Сохраняем их в менеджер
        manager.createNewTask(task1);
        manager.createNewTask(task2);
        // проверяем, что задачи сохранилась в менеджер
        assertEquals(2, manager.getAllTasks().size());
        // отправляем delete запрос и получаем ответ
        HttpResponse<String> getResponse = sendDeleteRequestAndGetResponse("http://localhost:8080/tasks");
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(201, getResponse.statusCode());
        // проверяем, что задачи были удалены
        assertEquals(0, manager.getAllTasks().size());
    }

    private HttpResponse<String> sendPostRequestAndGetResponse(String body, String uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(uri))
                .build();
        try {
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при отправке запроса " + e);
        }
        return null;
    }

    private HttpResponse<String> sendGetRequestAndGetResponse(String uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();
        try {
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при отправке запроса " + e);
        }
        return null;
    }

    private HttpResponse<String> sendDeleteRequestAndGetResponse(String uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri))
                .build();
        try {
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при отправке запроса " + e);
        }
        return null;
    }
}
